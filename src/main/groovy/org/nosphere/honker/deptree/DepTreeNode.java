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
package org.nosphere.honker.deptree;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

public class DepTreeNode
    implements DepTreeVisitor.Visitable
{
    private final DepTreeData data;
    private final Set<DepTreeNode> children = new LinkedHashSet<>();

    public DepTreeNode( DepTreeData data )
    {
        this.data = data;
    }

    public DepTreeNode( DepTreeNode parentNode, DepTreeData data )
    {
        this.data = data;
        parentNode.children.add( this );
    }

    public DepTreeData getData()
    {
        return data;
    }

    @Override
    public boolean accept( DepTreeVisitor visitor )
    {
        if( visitor.visitEnter( this ) )
        {
            for( DepTreeNode child : children )
            {
                if( !child.accept( visitor ) )
                {
                    break;
                }
            }
        }
        return visitor.visitExit( this );
    }

    @Override
    public String toString()
    {
        return data.toString();
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode( this.data );
        hash = 37 * hash + Objects.hashCode( this.children );
        return hash;
    }

    @Override
    public boolean equals( Object obj )
    {
        if( obj == this )
        {
            return true;
        }
        if( obj == null )
        {
            return false;
        }
        if( getClass() != obj.getClass() )
        {
            return false;
        }
        final DepTreeNode other = (DepTreeNode) obj;
        if( !Objects.equals( this.data, other.data ) )
        {
            return false;
        }
        return Objects.equals( this.children, other.children );
    }
}
