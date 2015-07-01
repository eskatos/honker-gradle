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
package org.nosphere.honker.visitors;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.nosphere.honker.deptree.DepTreeData;
import org.nosphere.honker.deptree.DepTreeNode;
import org.nosphere.honker.deptree.DepTreeVisitor;

/**
 * Dependencies Visitor.
 */
public class DependenciesByOrganizationsVisitor
    implements DepTreeVisitor
{
    private final Map<String, Set<DepTreeData.Artifact>> dependenciesByOrganizations = new TreeMap<>(
        String.CASE_INSENSITIVE_ORDER
    );

    public Map<String, Set<DepTreeData.Artifact>> getDependenciesByOrganizations()
    {
        return dependenciesByOrganizations;
    }

    @Override
    public boolean visitEnter( DepTreeNode node )
    {
        for( DepTreeData.Artifact artifact : node.getData().getArtifacts() )
        {
            String org = artifact.getOrganization();
            if( !dependenciesByOrganizations.containsKey( org ) )
            {
                dependenciesByOrganizations.put( org, new LinkedHashSet<DepTreeData.Artifact>() );
            }
            dependenciesByOrganizations.get( org ).add( artifact );
        }
        return true;
    }

    @Override
    public boolean visitExit( DepTreeNode node )
    {
        return true;
    }
}
