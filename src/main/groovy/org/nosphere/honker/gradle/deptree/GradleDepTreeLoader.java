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
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.Dependency;
import org.gradle.api.artifacts.ResolvedArtifact;
import org.gradle.api.artifacts.ResolvedDependency;
import org.gradle.api.artifacts.ProjectDependency;

import org.apache.commons.lang.StringUtils;

import org.nosphere.honker.License;
import org.nosphere.honker.deptree.DepTree;
import org.nosphere.honker.deptree.DepTreeData;
import org.nosphere.honker.deptree.DepTreeFilesLoader;
import org.nosphere.honker.deptree.DepTreeLoader;
import org.nosphere.honker.deptree.DepTreeManifestLoader;
import org.nosphere.honker.deptree.DepTreeNode;
import org.nosphere.honker.deptree.DepTreePomLoader;
import org.nosphere.honker.deptree.Gav;
import org.nosphere.honker.gradle.HonkerExtension;
import org.nosphere.honker.gradle.HonkerUtils;

public class GradleDepTreeLoader
    implements DepTreeLoader
{
    private final Project project;
    private final DepTreeManifestLoader manifestLoader;
    private final DepTreePomLoader pomLoader;
    private final DepTreeFilesLoader licenseFilesLoader;
    private final Configuration configuration;
    private final Set<Gav> projectDependencies = new HashSet<>();
    private final Set<Gav> loaded = new HashSet<>();

    public GradleDepTreeLoader( Project project, Configuration configuration )
    {
        this.project = project;
        this.manifestLoader = new DepTreeManifestLoader();
        this.pomLoader = new GradlePomLoader( project );
        this.licenseFilesLoader = new DepTreeFilesLoader();
        this.configuration = configuration;

        // Recursively record project's Dependencies GAV in order to distinguish ResolvedDependencies later
        for( Dependency dep : configuration.getAllDependencies() )
        {
            if( ProjectDependency.class.isAssignableFrom( dep.getClass() ) )
            {
                recordProjectDependency( (ProjectDependency) dep );
            }
        }
    }

    private void recordProjectDependency( ProjectDependency dependency )
    {
        Gav gav = new Gav( dependency.getGroup(), dependency.getName(), dependency.getVersion() );
        if( projectDependencies.contains( gav ) )
        {
            // Already recorded
            return;
        }
        projectDependencies.add( gav );
        Project depProject = dependency.getDependencyProject();
        Configuration depProjectConfig = depProject.getConfigurations().getByName( configuration.getName() );
        for( Dependency dep : depProjectConfig.getAllDependencies() )
        {
            if( ProjectDependency.class.isAssignableFrom( dep.getClass() ) )
            {
                recordProjectDependency( (ProjectDependency) dep );
            }
        }
    }

    private boolean isProjectDependency( ResolvedDependency dependency )
    {
        // return projectDependencies.contains( HonkerUtils.gavOf( dependency ) );
        for( Gav projectDep : projectDependencies )
        {
            if( dependency.getModuleGroup().equals( projectDep.getGroupId() )
                && dependency.getModuleName().equals( projectDep.getArtifactId() )
                && dependency.getModuleVersion().equals( projectDep.getVersion() ) )
            {
                return true;
            }
        }
        return false;
    }

    @Override
    public DepTree load()
    {
        try
        {
            List<DepTreeNode> rootNodes = new ArrayList<>();
            for( ResolvedDependency dep : configuration.getResolvedConfiguration().getFirstLevelModuleDependencies() )
            {
                DepTreeNode rootNode = createRootNode( dep );
                if( rootNode != null )
                {
                    rootNodes.add( rootNode );
                }
            }
            return new DepTree( rootNodes );
        }
        finally
        {
            loaded.clear();
        } 
    }

    private DepTreeNode createRootNode( ResolvedDependency dependency )
    {
        Gav gav = HonkerUtils.gavOf( dependency );
        if( loaded.contains( gav ) )
        {
            // Prevent infinite recursion
            return null;
        }
        DepTreeNode node = new DepTreeNode( gatherDependencyData( dependency ) );
        loaded.add( gav );
        for( ResolvedDependency child : dependency.getChildren() )
        {
            createNode( node, child );
        }
        return node;
    }

    private void createNode( DepTreeNode parentNode, ResolvedDependency dependency )
    {
        Gav gav = HonkerUtils.gavOf( dependency );
        if( loaded.contains( gav ) )
        {
            // Prevent infinite recursion
            return;
        }
        DepTreeNode node = new DepTreeNode( parentNode, gatherDependencyData( dependency ) );
        loaded.add( gav );
        for( ResolvedDependency child : dependency.getChildren() )
        {
            createNode( node, child );
        }
    }

    private DepTreeData gatherDependencyData( ResolvedDependency dependency )
    {
        if( isProjectDependency( dependency ) )
        {
            return gatherProjectDependencyData( dependency );
        }
        else
        {
            return gatherExternalDependencyData( dependency );
        }
    }

    private DepTreeData gatherProjectDependencyData( ResolvedDependency dependency )
    {
        Set<DepTreeData.Artifact> artifacts = new LinkedHashSet<>();
        for( ResolvedArtifact artifact : dependency.getModuleArtifacts() )
        {
            artifacts.add( gatherProjectArtifactData( artifact ) );
        }
        String coordinates = dependency.getModule().getId().toString();
        return new DepTreeData( coordinates, artifacts );
    }

    private DepTreeData.Artifact gatherProjectArtifactData( ResolvedArtifact artifact )
    {
        HonkerExtension ext = (HonkerExtension) project.getExtensions().getByName( "honker" );

        String organization = StringUtils.isNotEmpty( ext.getProjectOrganization() )
                              ? ext.getProjectOrganization()
                              : artifact.getModuleVersion().getId().getGroup();
        String name = StringUtils.isNotEmpty( ext.getProjectName() ) ? ext.getProjectName() : artifact.getName();
        String version = artifact.getModuleVersion().getId().getVersion();

        License lic = License.valueOfLicenseName( ext.getLicense() );
        DepTreeData.Pom pom = new DepTreeData.Pom(
            organization, null,
            name, version, null,
            Arrays.asList( new DepTreeData.PomLicense( lic.getPreferedName(), lic.getPreferedUrl(), null, null ) )
        );

        return new DepTreeData.Artifact( extractCoordinates( artifact ), DepTreeData.Manifest.EMPTY, pom, null );
    }

    private DepTreeData gatherExternalDependencyData( ResolvedDependency dependency )
    {
        Set<DepTreeData.Artifact> artifacts = new LinkedHashSet<>();
        for( ResolvedArtifact artifact : dependency.getModuleArtifacts() )
        {
            artifacts.add( gatherExternalArtifactData( artifact ) );
        }
        String coordinates = dependency.getModule().getId().toString();
        return new DepTreeData( coordinates, artifacts );
    }

    private DepTreeData.Artifact gatherExternalArtifactData( ResolvedArtifact artifact )
    {
        Gav gav = HonkerUtils.gavOf( artifact );
        String coordinates = extractCoordinates( artifact );
        DepTreeData.Manifest manifest = manifestLoader.load( artifact.getFile() );
        DepTreeData.Pom pom = pomLoader.load( artifact.getFile(), gav );
        List<DepTreeData.SomeFile> licenseFiles = licenseFilesLoader.load( artifact.getFile() );
        return new DepTreeData.Artifact( coordinates, manifest, pom, licenseFiles );
    }

    private static String extractCoordinates( ResolvedArtifact artifact )
    {
        StringBuilder coordinates = new StringBuilder();
        coordinates.append( artifact.getModuleVersion().getId().toString() )
            .append( ':' ).append( artifact.getType() );
        if( !StringUtils.isEmpty( artifact.getClassifier() ) )
        {
            coordinates.append( ':' ).append( artifact.getClassifier() );
        }
        return coordinates.toString();
    }
}
