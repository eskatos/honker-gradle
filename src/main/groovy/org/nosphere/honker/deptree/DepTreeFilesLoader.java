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
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

public final class DepTreeFilesLoader
{
    private static final List<String> BASENAMES = Arrays.asList(
        "license", "copyright", "copying", "copying.lesser", "notice"
    );

    public List<DepTreeData.SomeFile> load( File artifactFile )
    {
        String fileExtension = FilenameUtils.getExtension( artifactFile.getName() );
        if( fileExtension == null )
        {
            return new ArrayList<>();
        }
        switch( fileExtension.toLowerCase() )
        {
            case "zip":
            case "jar":
            case "war":
            case "ear":
                return loadFilesFromZip( artifactFile );
            default:
                return new ArrayList<>();
        }
    }

    private List<DepTreeData.SomeFile> loadFilesFromZip( File zipFile )
    {
        ZipFile zip = null;
        try
        {
            zip = new ZipFile( zipFile, ZipFile.OPEN_READ );
            List<DepTreeData.SomeFile> files = new ArrayList<>();
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while( entries.hasMoreElements() )
            {
                ZipEntry entry = entries.nextElement();
                String basename = StringUtils.substringAfterLast( entry.getName(), "/" );
                String extension = FilenameUtils.getExtension( basename );
                if( !StringUtils.isEmpty( extension ) )
                {
                    basename = FilenameUtils.removeExtension( basename );
                }
                basename = basename.toLowerCase();
                if( BASENAMES.contains( basename ) )
                {
                    InputStream fileStream = zip.getInputStream( entry );
                    try
                    {
                        String content = IOUtils.toString( fileStream, "UTF-8" );
                        files.add( new DepTreeData.SomeFile( basename, entry.getName(), content ) );
                    }
                    finally
                    {
                        IOUtils.closeQuietly( fileStream );
                    }
                }
            }
            return files;
        }
        catch( IOException ex )
        {
            throw new RuntimeException( ex.getMessage(), ex );
        }
        finally
        {
            if( zip != null )
            {
                try
                {
                    zip.close();
                }
                catch( IOException ignored )
                {
                    // Ignored
                }
            }
        }
    }
}
