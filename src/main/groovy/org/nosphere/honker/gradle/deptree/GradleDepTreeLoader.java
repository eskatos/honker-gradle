/*
 * Copyright (c) 2015 the original author or authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.nosphere.honker.gradle.deptree;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.gradle.api.Project;
import org.gradle.api.artifacts.ResolvedArtifact;
import org.gradle.api.artifacts.ResolvedConfiguration;
import org.gradle.api.artifacts.ResolvedDependency;

import org.apache.commons.lang.StringUtils;

import org.nosphere.honker.deptree.DepTree;
import org.nosphere.honker.deptree.DepTreeData;
import org.nosphere.honker.deptree.DepTreeFilesLoader;
import org.nosphere.honker.deptree.DepTreeLoader;
import org.nosphere.honker.deptree.DepTreeManifestLoader;
import org.nosphere.honker.deptree.DepTreeNode;
import org.nosphere.honker.deptree.DepTreePomLoader;
import org.nosphere.honker.deptree.Gav;

public class GradleDepTreeLoader
    implements DepTreeLoader
{
    private final DepTreeManifestLoader manifestLoader;
    private final DepTreePomLoader pomLoader;
    private final DepTreeFilesLoader licenseFilesLoader;
    private final ResolvedConfiguration configuration;

    public GradleDepTreeLoader( Project project, ResolvedConfiguration configuration )
    {
        this.manifestLoader = new DepTreeManifestLoader();
        this.pomLoader = new GradlePomLoader( project );
        this.licenseFilesLoader = new DepTreeFilesLoader();
        this.configuration = configuration;
    }

    @Override
    public DepTree load()
    {
        List<DepTreeNode> rootNodes = new ArrayList<>();
        for( ResolvedDependency dep : configuration.getFirstLevelModuleDependencies() )
        {
            rootNodes.add( createRootNode( dep ) );
        }
        return new DepTree( rootNodes );
    }

    private DepTreeNode createRootNode( ResolvedDependency dependency )
    {
        DepTreeNode node = new DepTreeNode( gatherDependencyData( dependency ) );
        for( ResolvedDependency child : dependency.getChildren() )
        {
            createNode( node, child );
        }
        return node;
    }

    private void createNode( DepTreeNode parentNode, ResolvedDependency dependency )
    {
        DepTreeNode node = new DepTreeNode( parentNode, gatherDependencyData( dependency ) );
        for( ResolvedDependency child : dependency.getChildren() )
        {
            createNode( node, child );
        }
    }

    private DepTreeData gatherDependencyData( ResolvedDependency dependency )
    {
        Set<DepTreeData.Artifact> artifacts = new LinkedHashSet<>();
        for( ResolvedArtifact artifact : dependency.getModuleArtifacts() )
        {
            artifacts.add( gatherArtifactData( artifact ) );
        }
        String coordinates = dependency.getModule().getId().toString();
        return new DepTreeData( coordinates, artifacts );
    }

    private DepTreeData.Artifact gatherArtifactData( ResolvedArtifact artifact )
    {
        StringBuilder coordinates = new StringBuilder();
        coordinates.append( artifact.getModuleVersion().getId().toString() )
            .append( ':' ).append( artifact.getType() );
        if( !StringUtils.isEmpty( artifact.getClassifier() ) )
        {
            coordinates.append( ':' ).append( artifact.getClassifier() );
        }
        DepTreeData.Manifest manifest = manifestLoader.load( artifact.getFile() );
        DepTreeData.Pom pom = pomLoader.load(
            artifact.getFile(),
            new Gav(
                artifact.getModuleVersion().getId().getGroup(),
                artifact.getModuleVersion().getId().getName(),
                artifact.getModuleVersion().getId().getVersion()
            )
        );
        List<DepTreeData.SomeFile> licenseFiles = licenseFilesLoader.load( artifact.getFile() );
        return new DepTreeData.Artifact( coordinates.toString(), manifest, pom, licenseFiles );
    }
}
