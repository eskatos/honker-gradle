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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import org.nosphere.honker.License;

public final class DepTreeData
{
    private final String coordinates;
    private final Set<Artifact> artifacts;

    public DepTreeData( String coordinates, Collection<Artifact> artifacts )
    {
        this.coordinates = coordinates;
        Set<Artifact> theArtifacts = new LinkedHashSet<>( artifacts.size() );
        theArtifacts.addAll( artifacts );
        this.artifacts = Collections.unmodifiableSet( theArtifacts );
    }

    public String getCoordinates()
    {
        return coordinates;
    }

    public Set<Artifact> getArtifacts()
    {
        return artifacts;
    }

    @Override
    public String toString()
    {
        return "Dependency{" + coordinates + ", " + artifacts + '}';
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 71 * hash + Objects.hashCode( this.coordinates );
        hash = 71 * hash + Objects.hashCode( this.artifacts );
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
        final DepTreeData other = (DepTreeData) obj;
        if( !Objects.equals( this.coordinates, other.coordinates ) )
        {
            return false;
        }
        return Objects.equals( this.artifacts, other.artifacts );
    }

    public static final class Artifact
    {
        private final String coordinates;
        private final Manifest manifest;
        private final Pom pom;
        private final Set<SomeFile> licenseFiles = new LinkedHashSet<>();
        private final String overridenLicense;
        private final Set<License> detectedLicenses = new LinkedHashSet<>();

        public Artifact( String coordinates,
                         Manifest manifest,
                         Pom pom,
                         Collection<SomeFile> licenseFiles,
                         String overridenLicense )
        {
            this.coordinates = coordinates;
            this.manifest = manifest;
            this.pom = pom;
            if( licenseFiles != null )
            {
                this.licenseFiles.addAll( licenseFiles );
            }
            this.overridenLicense = overridenLicense;
            detectLicenses();
        }

        private void detectLicenses()
        {
            License mfLicense = License.valueOfLicenseName( manifest.license );
            if( mfLicense != null )
            {
                detectedLicenses.add( mfLicense );
            }
            License mfLicenseUrl = License.valueOfLicenseUrl( manifest.license );
            if( mfLicenseUrl != null )
            {
                detectedLicenses.add( mfLicenseUrl );
            }
            for( DepTreeData.PomLicense pomLicense : pom.licenses )
            {
                License nameLicense = License.valueOfLicenseName( pomLicense.name );
                if( nameLicense != null )
                {
                    detectedLicenses.add( nameLicense );
                }
                License urlLicense = License.valueOfLicenseUrl( pomLicense.url );
                if( urlLicense != null )
                {
                    detectedLicenses.add( urlLicense );
                }
            }
            License ovLicense = License.valueOfLicenseName( overridenLicense );
            if( ovLicense != null )
            {
                detectedLicenses.add( ovLicense );
            }
            License ovLicenseUrl = License.valueOfLicenseUrl( overridenLicense );
            if( ovLicenseUrl != null )
            {
                detectedLicenses.add( ovLicenseUrl );
            }
        }

        public String getOrganization()
        {
            if( StringUtils.isNotEmpty( pom.organization ) )
            {
                return pom.organization;
            }
            if( StringUtils.isNotEmpty( manifest.vendor ) )
            {
                return manifest.vendor;
            }
            return "Unknown Organization";
        }

        public String getOrganizationUrl()
        {
            if( StringUtils.isNotEmpty( pom.organizationUrl ) )
            {
                return pom.organizationUrl;
            }
            return "";
        }

        public String getName()
        {
            if( StringUtils.isNotEmpty( pom.name ) )
            {
                return pom.name;
            }
            if( StringUtils.isNotEmpty( manifest.name ) )
            {
                return manifest.name;
            }
            return "Unknown Name";
        }

        public String getVersion()
        {
            if( StringUtils.isNotEmpty( pom.version ) )
            {
                return pom.version;
            }
            if( StringUtils.isNotEmpty( manifest.version ) )
            {
                return manifest.version;
            }
            return "";
        }

        public String getUrl()
        {
            if( StringUtils.isNotEmpty( pom.url ) )
            {
                return pom.url;
            }
            if( StringUtils.isNotEmpty( manifest.url ) )
            {
                return manifest.url;
            }
            return "";
        }

        public String getCoordinates()
        {
            return coordinates;
        }

        public Set<License> getDetectedLicenses()
        {
            return Collections.unmodifiableSet( detectedLicenses );
        }

        @Override
        public String toString()
        {
            return "Artifact{" + coordinates + ", " + manifest + ", " + pom + ", " + licenseFiles + ", "
                   + detectedLicenses + '}';
        }

        @Override
        public int hashCode()
        {
            int hash = 3;
            hash = 89 * hash + Objects.hashCode( this.coordinates );
            hash = 89 * hash + Objects.hashCode( this.manifest );
            hash = 89 * hash + Objects.hashCode( this.pom );
            hash = 89 * hash + Objects.hashCode( this.licenseFiles );
            hash = 89 * hash + Objects.hashCode( this.detectedLicenses );
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
            final Artifact other = (Artifact) obj;
            if( !Objects.equals( this.coordinates, other.coordinates ) )
            {
                return false;
            }
            if( !Objects.equals( this.manifest, other.manifest ) )
            {
                return false;
            }
            if( !Objects.equals( this.pom, other.pom ) )
            {
                return false;
            }
            if( !Objects.equals( this.licenseFiles, other.licenseFiles ) )
            {
                return false;
            }
            return Objects.equals( this.detectedLicenses, other.detectedLicenses );
        }
    }

    public static final class Manifest
    {
        public static final Manifest EMPTY = new Manifest( null, null, null, null, null );

        private final String name;
        private final String version;
        private final String vendor;
        private final String url;
        private final String license;

        public Manifest( String name, String version, String vendor, String url, String license )
        {
            this.name = name;
            this.version = version;
            this.vendor = vendor;
            this.url = url;
            this.license = license;
        }

        @Override
        public String toString()
        {
            return "Manifest{" + "name=" + name + ", version=" + version + ", vendor=" + vendor
                   + ", url=" + url + ", license=" + license + '}';
        }

        @Override
        public int hashCode()
        {
            int hash = 5;
            hash = 97 * hash + Objects.hashCode( this.name );
            hash = 97 * hash + Objects.hashCode( this.version );
            hash = 97 * hash + Objects.hashCode( this.vendor );
            hash = 97 * hash + Objects.hashCode( this.url );
            hash = 97 * hash + Objects.hashCode( this.license );
            return hash;
        }

        @Override
        public boolean equals( Object obj )
        {
            if( obj == null )
            {
                return false;
            }
            if( getClass() != obj.getClass() )
            {
                return false;
            }
            final Manifest other = (Manifest) obj;
            if( !Objects.equals( this.name, other.name ) )
            {
                return false;
            }
            if( !Objects.equals( this.version, other.version ) )
            {
                return false;
            }
            if( !Objects.equals( this.vendor, other.vendor ) )
            {
                return false;
            }
            if( !Objects.equals( this.url, other.url ) )
            {
                return false;
            }
            return Objects.equals( this.license, other.license );
        }
    }

    public static final class Pom
    {
        private final String organization;
        private final String organizationUrl;
        private final String name;
        private final String version;
        private final String url;
        private final Set<PomLicense> licenses = new LinkedHashSet<>();

        public Pom( String organization, String organizationUrl,
                    String name, String version, String url )
        {
            this( organization, organizationUrl, name, version, url, null );
        }

        public Pom( String organization, String organizationUrl,
                    String name, String version, String url,
                    Collection<PomLicense> licenses )
        {
            this.organization = organization;
            this.organizationUrl = organizationUrl;
            this.name = name;
            this.version = version;
            this.url = url;
            if( licenses != null )
            {
                this.licenses.addAll( licenses );
            }
        }

        void addLicense( PomLicense license )
        {
            licenses.add( license );
        }

        @Override
        public String toString()
        {
            return "Pom{" + "name=" + name + ", version=" + version + ", organization=" + organization
                   + ", url=" + url + ", licenses=" + licenses + '}';
        }

        @Override
        public int hashCode()
        {
            int hash = 7;
            hash = 37 * hash + Objects.hashCode( this.name );
            hash = 37 * hash + Objects.hashCode( this.version );
            hash = 37 * hash + Objects.hashCode( this.organization );
            hash = 37 * hash + Objects.hashCode( this.url );
            hash = 37 * hash + Objects.hashCode( this.licenses );
            return hash;
        }

        @Override
        public boolean equals( Object obj )
        {
            if( obj == null )
            {
                return false;
            }
            if( getClass() != obj.getClass() )
            {
                return false;
            }
            final Pom other = (Pom) obj;
            if( !Objects.equals( this.name, other.name ) )
            {
                return false;
            }
            if( !Objects.equals( this.version, other.version ) )
            {
                return false;
            }
            if( !Objects.equals( this.organization, other.organization ) )
            {
                return false;
            }
            if( !Objects.equals( this.url, other.url ) )
            {
                return false;
            }
            return Objects.equals( this.licenses, other.licenses );
        }
    }

    public static final class PomLicense
    {
        private final String name;
        private final String url;
        private final String distribution;
        private final String comments;

        public PomLicense( String name, String url, String distribution, String comments )
        {
            this.name = name;
            this.url = url;
            this.distribution = distribution;
            this.comments = comments;
        }

        @Override
        public String toString()
        {
            return "PomLicense{" + name + ", " + url + ", " + distribution + ", " + comments + '}';
        }

        @Override
        public int hashCode()
        {
            int hash = 7;
            hash = 29 * hash + Objects.hashCode( this.name );
            hash = 29 * hash + Objects.hashCode( this.url );
            hash = 29 * hash + Objects.hashCode( this.distribution );
            hash = 29 * hash + Objects.hashCode( this.comments );
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
            final PomLicense other = (PomLicense) obj;
            if( !Objects.equals( this.name, other.name ) )
            {
                return false;
            }
            if( !Objects.equals( this.url, other.url ) )
            {
                return false;
            }
            if( !Objects.equals( this.distribution, other.distribution ) )
            {
                return false;
            }
            return Objects.equals( this.comments, other.comments );
        }
    }

    public static final class SomeFile
    {
        private final String name;
        private final String originalPath;
        private final String content;

        SomeFile( String name, String originalPath, String content )
        {
            this.name = name;
            this.originalPath = originalPath;
            this.content = content;
        }

        @Override
        public String toString()
        {
            return "SomeFile{" + name + ", " + originalPath + '}';
        }

        @Override
        public int hashCode()
        {
            int hash = 3;
            hash = 67 * hash + Objects.hashCode( this.name );
            hash = 67 * hash + Objects.hashCode( this.originalPath );
            hash = 67 * hash + Objects.hashCode( this.content );
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
            final SomeFile other = (SomeFile) obj;
            if( !Objects.equals( this.name, other.name ) )
            {
                return false;
            }
            if( !Objects.equals( this.originalPath, other.originalPath ) )
            {
                return false;
            }
            return Objects.equals( this.content, other.content );
        }
    }
}
