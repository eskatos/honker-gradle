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
import java.util.Set;

import org.nosphere.honker.License;
import org.nosphere.honker.deptree.DepTreeData;
import org.nosphere.honker.deptree.DepTreeNode;
import org.nosphere.honker.deptree.DepTreeVisitor;

public class LicenseConflictVisitor
    implements DepTreeVisitor
{
    private final Set<DepTreeData.Artifact> conflicts = new LinkedHashSet<>();
    private final License referenceLicense;

    public LicenseConflictVisitor( License referenceLicense )
    {
        this.referenceLicense = referenceLicense;
    }

    public Set<DepTreeData.Artifact> conflicts()
    {
        return conflicts;
    }

    @Override
    public boolean visitEnter( DepTreeNode node )
    {
        for( DepTreeData.Artifact artifact : node.getData().getArtifacts() )
        {
            if( !artifact.getDetectedLicenses().isEmpty() )
            {
                int conflictsCount = 0;
                for( License lic : artifact.getDetectedLicenses() )
                {
                    if( lic.conflictWith( referenceLicense ) )
                    {
                        conflictsCount++;
                    }
                }
                if( conflictsCount == artifact.getDetectedLicenses().size() )
                {
                    conflicts.add( artifact );
                }
            }
        }
        return true;
    }

    @Override
    public boolean visitExit( DepTreeNode node )
    {
        return true;
    }
}
