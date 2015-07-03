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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

public final class DepTreeManifestLoader
{
    public DepTreeData.Manifest load( File artifactFile )
    {
        String extension = FilenameUtils.getExtension( artifactFile.getName() );
        if( extension == null )
        {
            return DepTreeData.Manifest.EMPTY;
        }
        try
        {
            switch( extension.toLowerCase() )
            {
                case "mf":
                    return loadFromManifest( artifactFile );
                case "zip":
                case "jar":
                case "war":
                case "ear":
                    return loadFromZip( artifactFile );
                default:
                    return DepTreeData.Manifest.EMPTY;
            }
        }
        catch( IOException ex )
        {
            throw new RuntimeException( ex.getMessage(), ex );
        }
    }

    private DepTreeData.Manifest loadFromManifest( File manifestFile )
        throws IOException
    {
        InputStream manifestStream = null;
        try
        {
            manifestStream = new FileInputStream( manifestFile );
            return loadData( new Manifest( manifestStream ) );
        }
        finally
        {
            IOUtils.closeQuietly( manifestStream );
        }
    }

    private DepTreeData.Manifest loadFromZip( File zipFile )
        throws IOException
    {
        JarFile jar = null;
        try
        {
            jar = new JarFile( zipFile );
            Manifest mf = jar.getManifest();
            if( mf == null )
            {
                return DepTreeData.Manifest.EMPTY;
            }
            return loadData( mf );
        }
        finally
        {
            if( jar != null )
            {
                try
                {
                    jar.close();
                }
                catch( IOException ignored )
                {
                    // Ignored
                }
            }
        }
    }

    private DepTreeData.Manifest loadData( java.util.jar.Manifest mf )
    {
        Attributes attr = mf.getMainAttributes();
        String name = attr.getValue( "Bundle-Name" );
        if( name == null )
        {
            name = attr.getValue( "Implementation-Title" );
        }
        if( name == null )
        {
            name = attr.getValue( "Bundle-SymbolicName" );
        }
        String version = attr.getValue( "Bundle-Version" );
        if( version == null )
        {
            version = attr.getValue( "Implementation-Version" );
        }
        if( version == null )
        {
            version = attr.getValue( "Specification-Version" );
        }
        String vendor = attr.getValue( "Bundle-Vendor" );
        if( vendor == null )
        {
            vendor = attr.getValue( "Implementation-Vendor" );
        }
        String url = attr.getValue( "Bundle-DocURL" );
        String license = attr.getValue( "Bundle-License" );
        return new DepTreeData.Manifest( name, version, vendor, url, license );
    }
}
