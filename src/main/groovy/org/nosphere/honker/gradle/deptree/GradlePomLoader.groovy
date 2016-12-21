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
package org.nosphere.honker.gradle.deptree

import com.google.common.cache.CacheBuilder
import com.google.common.cache.CacheLoader
import com.google.common.cache.LoadingCache
import com.google.common.collect.ImmutableMap
import groovy.util.slurpersupport.GPathResult
import java.util.concurrent.atomic.AtomicLong
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import org.apache.commons.io.FilenameUtils
import org.apache.commons.io.IOUtils
import org.apache.commons.lang.StringUtils
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ResolvedArtifact
import org.nosphere.honker.deptree.DepTreeData
import org.nosphere.honker.deptree.DepTreeData.Pom
import org.nosphere.honker.deptree.DepTreePomLoader
import org.nosphere.honker.deptree.Gav

class GradlePomLoader implements DepTreePomLoader
{
  private final Project project;

  GradlePomLoader( Project project )
  {
    this.project = project;
  }

  Pom load( File artifactFile, Gav gav )
  {
    GPathResult pomXml = slurpPom( artifactFile );
    if( pomXml == null )
    {
      Map pomGav = [
        "group"  : gav.groupId,
        "name"   : gav.artifactId,
        "version": gav.version,
        "ext"    : "pom"
      ]

      Collection<ResolvedArtifact> artifacts = resolveArtifacts( pomGav )
      pomXml = artifacts?.inject( pomXml ) { GPathResult memo, ResolvedArtifact resolved ->
        try
        {
          memo = memo ?: slurpPom( resolved.file )
        }
        catch( Exception ex )
        {
          ex.printStackTrace()
        }
        return memo
      }
    }
    return pomData( pomXml );
  }

  private GPathResult slurpPom( File fileToSlurp )
  {
    if( "pom.xml" == fileToSlurp.getName() )
    {
      return new XmlSlurper().parse( fileToSlurp );
    }

    String extension = FilenameUtils.getExtension( fileToSlurp.getName() );
    if( StringUtils.isEmpty( extension ) )
    {
      return null;
    }
    switch( extension.toLowerCase() )
    {
      case "pom":
        return new XmlSlurper().parse( fileToSlurp );
      case "zip":
      case "jar":
      case "war":
      case "ear":
        return slurpXmlOfFirstPomFoundInArchive( fileToSlurp );
      default:
        return null;
    }
  }

  private GPathResult slurpXmlOfFirstPomFoundInArchive( File archiveFile )
  {
    ZipFile zip = null;
    try
    {
      zip = new ZipFile( archiveFile, ZipFile.OPEN_READ );
      Enumeration<? extends ZipEntry> entries = zip.entries();
      ZipEntry pomEntry = null;
      while( entries.hasMoreElements() )
      {
        ZipEntry entry = entries.nextElement();
        if( entry.getName().toLowerCase().endsWith( "pom.xml" )
          || entry.getName().toLowerCase().endsWith( ".pom" ) )
        {
          pomEntry = entry;
          break;
        }
      }
      if( pomEntry == null )
      {
        return null;
      }
      InputStream pomStream = zip.getInputStream( pomEntry );
      try
      {
        return new XmlSlurper().parse( pomStream );
      }
      finally
      {
        IOUtils.closeQuietly( pomStream );
      }
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

  private DepTreeData.Pom pomData( GPathResult pomXml )
  {
    String organization = pomXml.organization?.name?.text()
    String organizationUrl = pomXml.organization?.url?.text()
    String name = pomXml.name?.text()
    if( !name || name == '${project.artifactId}' )
    {
      name = pomXml.artifactId.text()
    }
    String url = pomXml.url?.text()
    String version = pomXml.version?.text()
    return pomData( pomXml, new DepTreeData.Pom( organization, organizationUrl, name, version, url ) );
  }

  private DepTreeData.Pom pomData( GPathResult pomXml, DepTreeData.Pom data )
  {
    if( pomXml == null )
    {
      return data;
    }
    if( !pomXml.parent.children().isEmpty() )
    {
      GPathResult parentContent = pomXml.parent
      Map<String, String> parent = [
        "group"  : parentContent.groupId.text(),
        "name"   : parentContent.artifactId.text(),
        "version": parentContent.version.text(),
        "ext"    : "pom"
      ]
      Collection<ResolvedArtifact> parentArtifacts = resolveArtifacts( parent )
      if( parentArtifacts )
      {
        ( parentArtifacts*.file as Set ).each { File file ->
          data = pomData( new XmlSlurper().parse( file ), data )
        }
      }
    }
    pomXml.licenses?.license?.each { GPathResult license ->
      data.addLicense(
        new DepTreeData.PomLicense(
          license.name?.text(),
          license.url?.text(),
          license.distribution?.text(),
          license.comments?.text()
        )
      );
    }
    return data;
  }

  private Collection<ResolvedArtifact> resolveArtifacts( Map<String, String> gav )
  {
    try
    {
      // Get artifacts from cache, first resolving them if necessary
      return resolvedArtifactCache.getUnchecked( ImmutableMap.copyOf( gav ) );
    }
    catch( Exception ex )
    {
      // Unresolved artifacts, will return an empty Collection
      project.logger.warn( "Unresolveable artifacts spec: $gav", ex )
      return Collections.emptyList();
    }
  }

  private static final AtomicLong POM_RESOLVING_CONFIG_COUNT = new AtomicLong();

  private final LoadingCache<Map<String, String>, Collection<ResolvedArtifact>> resolvedArtifactCache =
    CacheBuilder.newBuilder().concurrencyLevel( 1 ).build(
      new CacheLoader<Map<String, String>, Collection<ResolvedArtifact>>() {
        @Override
        public Collection<ResolvedArtifact> load( Map<String, String> gav ) throws Exception
        {
          String configName = "honkerPomLoader${ POM_RESOLVING_CONFIG_COUNT.getAndIncrement() }";
          project.getConfigurations().create( configName );
          project.getDependencies().add( configName, gav );
          Configuration config = project.getConfigurations().getByName( configName );
          Collection<ResolvedArtifact> artifacts = config.getResolvedConfiguration().getResolvedArtifacts();
          if( artifacts != null )
          {
            for(
              ResolvedArtifact artifact :
                artifacts )
            {
              // Download! This will throw exceptions eagerly!
              artifact.getFile();
            }
            return artifacts;
          }
          return Collections.emptyList();
        }
      }
    );
}
