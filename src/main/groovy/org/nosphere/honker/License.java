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
package org.nosphere.honker;

import java.io.IOException;
import java.io.InputStream;

import static java.util.Arrays.asList;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

/**
 * License.
 */
public enum License
{
    APACHE_2(
        asList(
            "The Apache Software License, Version 2.0",
            "Apache 2",
            "Apache Software License, Version 2.0",
            "Apache License, version 2.0.",
            "Apache License, version 2.0",
            "Apache Software License 2.0",
            "Apache License Version 2.0",
            "Apache License 2.0"
        ),
        asList(
            "http://www.apache.org/licenses/LICENSE-2.0",
            "http://www.apache.org/licenses/LICENSE-2.0.txt",
            "http://www.apache.org/licenses/LICENSE-2.0.html",
            "http://apache.org/licenses/LICENSE-2.0",
            "http://apache.org/licenses/LICENSE-2.0.txt",
            "http://apache.org/licenses/LICENSE-2.0.html",
            "http://www.opensource.org/licenses/Apache-2.0",
            "http://opensource.org/licenses/Apache-2.0",
            "http://opensource.org/licenses/apache2.0.php"
        )
    ),
    APACHE_1_1(
        asList(
            "The Apache Software License, Version 1.1",
            "Apache 1.1",
            "Apache Software License, Version 1.1",
            "Apache Software License 1.1",
            "Apache License 1.1"
        ),
        asList(
            "http://www.apache.org/licenses/LICENSE-1.1",
            "http://www.apache.org/licenses/LICENSE-1.1.txt",
            "http://apache.org/licenses/LICENSE-1.1",
            "http://apache.org/licenses/LICENSE-1.1.txt",
            "http://www.opensource.org/licenses/Apache-1.1",
            "http://opensource.org/licenses/Apache-1.1"
        )
    ),
    PHP_3_1(
        asList(
            "The PHP License, version 3.01",
            "PHP License 3.01",
            "PHP License, version 3.01"
        ),
        asList(
            "http://php.net/license/3_01.txt"
        )
    ),
    BSD_3_CLAUSES(
        asList(
            "BSD 3-Clause License",
            "BSD",
            "BSD License",
            "New BSD License",
            "Revised BSD License",
            "BSD 3-Clause",
            "BSD 3-Clause \"New\" or \"Revised\" license"
        ),
        asList(
            "http://opensource.org/licenses/BSD-3-Clause",
            "http://asm.objectweb.org/license.html",
            "http://asm.ow2.org/license.html",
            "http://antlr.org/license.html"
        )
    ),
    BSD_2_CLAUSES(
        asList(
            "BSD 2-Clause \"Simplified\" or \"FreeBSD\" license",
            "FreeBSD",
            "FreeBSD License",
            "Simplified BSD License"
        ),
        asList(
            "http://opensource.org/licenses/BSD-2-Clause"
        )
    ),
    MIT_X11(
        asList(
            "MIT License",
            "MIT",
            "X11",
            "X11 License",
            "MIT/X Consortium License",
            "Expat License"
        ),
        asList(
            "http://opensource.org/licenses/MIT",
            "http://www.opensource.org/licenses/MIT",
            "http://opensource.org/licenses/mit-license.php",
            "http://www.opensource.org/licenses/mit-license.php"
        )
    ),
    ICU(
        asList(
            "ICU License",
            "ICU"
        ),
        asList(
            "http://source.icu-project.org/repos/icu/icu/trunk/license.html"
        )
    ),
    UoL_NCSA(
        asList(
            "The University of Illinois/NCSA Open Source License (NCSA)",
            "UoI-NCSA",
            "The University of Illinois/NCSA Open Source License",
            "University of Illinois/NCSA Open Source License (NCSA)",
            "University of Illinois/NCSA Open Source License"
        ),
        asList(
            "http://opensource.org/licenses/UoI-NCSA.php"
        )
    ),
    W3C(
        asList(
            "The W3C SOFTWARE NOTICE AND LICENSE (W3C)",
            "W3C",
            "The W3C SOFTWARE NOTICE AND LICENSE",
            "W3C SOFTWARE NOTICE AND LICENSE (W3C)",
            "W3C SOFTWARE NOTICE AND LICENSE",
            "W3C® SOFTWARE NOTICE AND LICENSE"
        ),
        asList(
            "http://opensource.org/licenses/W3C.php"
        )
    ),
    ZLIB(
        asList(
            "The zlib/libpng License (Zlib)",
            "Zlib",
            "The zlib/libpng License"
        ),
        asList(
            "http://opensource.org/licenses/zlib-license.php"
        )
    ),
    AFL(
        asList(
            "Academic Free License (\"AFL\") v. 3.0",
            "AFL",
            "AFL 3.0",
            "Academic Free License 3.0"
        ),
        asList(
            "http://opensource.org/licenses/afl-3.0.php"
        )
    ),
    MS_PL(
        asList(
            "Microsoft Public License (MS-PL)",
            "MS-PL",
            "Microsoft Public License"
        ),
        asList(
            "http://opensource.org/licenses/ms-pl.html",
            "http://opensource.org/licenses/MS-PL"
        )
    ),
    CC_A(
        asList(
            "Creative Commons Attribution (CC-A) 3.0",
            "CC-A",
            "CC-A 2.5",
            "CC-A 3.0",
            "Attribution 2.5 Generic (CC BY 2.5)",
            "Attribution 3.0 Unported (CC BY 3.0)",
            "Creative Commons Attribution (CC-A) 2.5"
        ),
        asList(
            "https://creativecommons.org/licenses/by/3.0/",
            "https://creativecommons.org/licenses/by/2.5/"
        )
    ),
    PYTHON(
        asList(
            "Python License, Version 2 (Python-2.0)",
            "Python",
            "Python License",
            "Python License 2.0",
            "Python License (Python-2.0)",
            "Python Software Foundation License",
            "PYTHON SOFTWARE FOUNDATION LICENSE VERSION 2"
        ),
        asList(
            "http://www.opensource.org/licenses/PythonSoftFoundation.php",
            "http://www.opensource.org/licenses/PythonSoftFoundation",
            "http://opensource.org/licenses/PythonSoftFoundation.php",
            "http://opensource.org/licenses/PythonSoftFoundation"
        )
    ),
    BSL(
        asList(
            "Boost Software License 1.0 (BSL-1.0)",
            "BSL",
            "BSL-1.0",
            "BSL 1.0",
            "Boost Software License Version 1.0",
            "Boost Software License 1.0"
        ),
        asList(
            "http://www.opensource.org/licenses/BSL-1.0.php",
            "http://www.opensource.org/licenses/BSL-1.0",
            "http://opensource.org/licenses/BSL-1.0.php",
            "http://opensource.org/licenses/BSL-1.0"
        )
    ),
    EDL(
        asList(
            "Eclipse Distribution License (EDL)",
            "EDL",
            "EDL 1.0",
            "Eclipse Distribution License",
            "Eclipse Distribution License - v 1.0"
        ),
        asList(
            "http://www.eclipse.org/org/documents/edl-v10.php",
            "http://www.eclipse.org/org/documents/edl-v10.html"
        )
    ),
    EPL(
        asList(
            "Eclipse Public License (EPL)",
            "EPL",
            "EPL 1.0",
            "Eclipse Public License",
            "Eclipse Public License - v 1.0"
        ),
        asList(
            "http://www.eclipse.org/legal/epl-v10.html",
            "http://opensource.org/licenses/EPL-1.0",
            "http://www.opensource.org/licenses/EPL-1.0"
        )
    ),
    WTFPL(
        asList(
            "WTFPL – Do What the Fuck You Want to Public License",
            "WTFPL",
            "Do What the Fuck You Want to Public License"
        ),
        asList(
            "http://www.wtfpl.net/",
            "http://www.wtfpl.net/txt/copying/"
        )
    ),
    RUBY(
        asList(
            "Ruby License",
            "Ruby"
        ),
        asList(
            "http://www.ruby-lang.org/en/LICENSE.txt",
            "https://www.ruby-lang.org/en/about/license.txt"
        )
    ),
    MOZILLA(
        asList(
            "Mozilla Public License 2.0",
            "MPL"
        ),
        asList( "http://opensource.org/licenses/MPL-2.0" )
    ),
    CDDL(
        asList(
            "Common Development and Distribution License",
            "CDDL",
            "Common Development and Distribution License (CDDL)",
            "CDDL License",
            "COMMON DEVELOPMENT AND DISTRIBUTION LICENSE (CDDL) Version 1.0"
        ),
        asList(
            "http://opensource.org/licenses/CDDL-1.0",
            "http://www.opensource.org/licenses/cddl1.php",
            "https://glassfish.dev.java.net/public/CDDLv1.0.html"
        )
    ),
    GPL(
        asList(
            "GNU General Public License",
            "GPL",
            "GNU General Public License (GPL)",
            "GNU General Public License, version 1",
            "GNU General Public License, version 2",
            "GNU General Public License, version 3",
            "The GNU General Public License, Version 1",
            "The GNU General Public License, Version 2",
            "The GNU General Public License, Version 3"
        ),
        asList(
            "http://www.gnu.org/licenses/gpl-3.0.html",
            "http://www.gnu.org/licenses/gpl-1.0.txt",
            "http://www.gnu.org/licenses/gpl-1.0.html",
            "http://www.gnu.org/licenses/gpl-2.0.txt",
            "http://www.gnu.org/licenses/gpl-2.0.html",
            "http://www.gnu.org/licenses/gpl-3.0.txt",
            "http://www.opensource.org/licenses/gpl-license",
            "http://www.opensource.org/licenses/GPL-1.0",
            "http://www.opensource.org/licenses/GPL-1.0.php",
            "http://www.opensource.org/licenses/GPL-2.0",
            "http://www.opensource.org/licenses/GPL-2.0.php",
            "http://www.opensource.org/licenses/GPL-3.0",
            "http://www.opensource.org/licenses/GPL-3.0.php",
            "http://opensource.org/licenses/gpl-license",
            "http://opensource.org/licenses/GPL-1.0",
            "http://opensource.org/licenses/GPL-1.0.php",
            "http://opensource.org/licenses/GPL-2.0",
            "http://opensource.org/licenses/GPL-2.0.php",
            "http://opensource.org/licenses/GPL-3.0",
            "http://opensource.org/licenses/GPL-3.0.php"
        )
    ),
    LGPL(
        asList(
            "GNU Lesser General Public License",
            "LGPL",
            "GNU Library General Public License",
            "GNU Library General Public License (LGPL)",
            "GNU Lesser General Public License (LGPL)",
            "GNU \"Lesser\" General Public License",
            "GNU \"Lesser\" General Public License (LGPL)",
            "GNU Library or \"Lesser\" General Public License",
            "GNU Library or \"Lesser\" General Public License (LGPL)",
            "GNU Lesser Public License"
        ),
        asList(
            "http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html",
            "http://www.opensource.org/licenses/lgpl-license",
            "http://www.opensource.org/licenses/lgpl-license.php",
            "http://opensource.org/licenses/lgpl-license",
            "http://opensource.org/licenses/lgpl-license.php",
            "http://www.gnu.org/licenses/lgpl.html"
        )
    ),
    AGPL(
        asList(
            "The Affero GPL License",
            "Affero GPL",
            "AGPL",
            "Affero GPL 3",
            "GNU AFFERO GENERAL PUBLIC LICENSE, Version 3 (AGPL-3.0)",
            "GNU AFFERO GENERAL PUBLIC LICENSE, Version 3",
            "GNU AFFERO GENERAL PUBLIC LICENSE (AGPL-3.0)",
            "GNU AFFERO GENERAL PUBLIC LICENSE"
        ),
        asList(
            "http://www.gnu.org/licenses/agpl.html",
            "http://www.gnu.org/licenses/agpl.txt",
            "http://www.opensource.org/licenses/agpl-v3.html",
            "http://www.opensource.org/licenses/agpl-v3",
            "http://opensource.org/licenses/agpl-v3.html",
            "http://opensource.org/licenses/agpl-v3"
        )
    ),
    SLEEPYCAT(
        asList(
            "The Sleepycat License",
            "Sleepycat",
            "The Sleepycat License (Sleepycat)",
            "Sleepycat License",
            "The Sleepycat Public License",
            "The Sleepycat License",
            "Berkeley Database License",
            "The Berkeley Database License"
        ),
        asList(
            "http://opensource.org/licenses/sleepycat.php",
            "http://opensource.org/licenses/sleepycat"
        )
    ),
    CPL(
        asList(
            "Common Public License Version 1.0"
        ),
        asList(
            "http://www.opensource.org/licenses/cpl1.0.txt"
        )
    ),
    OSGI(
        asList(
            "OSGi Specification License, Version 2.0"
        ),
        asList(
            "http://www.osgi.org/Specifications/Licensing"
        )
    ),
    JSON(
        asList(
            "The JSON License"
        ),
        asList(
            "http://www.json.org/license.html"
        )
    ),
    PUBLIC_DOMAIN(
        asList(
            "Public Domain"
        ),
        asList(
            "https://creativecommons.org/licenses/publicdomain/"
        )
    );

    public static License valueOfLicenseName( String licenseName )
    {
        if( StringUtils.isEmpty( licenseName ) )
        {
            return null;
        }
        String normalizedLicenseName = licenseName.toLowerCase( Locale.US );
        for( License license : License.values() )
        {
            for( String name : license.names )
            {
                if( name.toLowerCase( Locale.US ).equals( normalizedLicenseName ) )
                {
                    return license;
                }
            }
        }
        return null;
    }

    public static License valueOfLicenseUrl( String licenseUrl )
    {
        if( StringUtils.isEmpty( licenseUrl ) )
        {
            return null;
        }
        for( License license : License.values() )
        {
            for( String url : license.urls )
            {
                if( url.equals( licenseUrl ) )
                {
                    return license;
                }
            }
        }
        return null;
    }

    private final Set<String> names = new LinkedHashSet<>();
    private final Set<String> urls = new LinkedHashSet<>();

    private License( Collection<String> names, Collection<String> urls )
    {
        this.names.addAll( names );
        this.urls.addAll( urls );
    }

    public String getPreferedName()
    {
        return names.iterator().next();
    }

    public String getPreferedUrl()
    {
        return urls.iterator().next();
    }

    public boolean conflictWith( License other )
    {
        return CONFLICTS.get( other ).contains( this );
    }

    public boolean needMentionInLicenseFor( License other )
    {
        return NEED_MENTION_IN_LICENSE.get( other ).contains( this );
    }

    public boolean needMentionInNoticeFor( License other )
    {
        return NEED_MENTION_IN_NOTICE.get( other ).contains( this );
    }

    public String licenseTemplate()
    {
        return loadTemplate( "LICENSE_" + name() + ".template" );
    }

    public String noticeTemplate()
    {
        return loadTemplate( "NOTICE_" + name() + ".template" );
    }

    private String loadTemplate( String templateName )
    {
        InputStream templateStream = getClass().getResourceAsStream( templateName );
        try
        {
            if( templateStream == null )
            {
                return null;
            }
            return IOUtils.toString( templateStream, "UTF-8" );
        }
        catch( IOException ignored )
        {
            return null;
        }
        finally
        {
            IOUtils.closeQuietly( templateStream );
        }
    }

    private static final Map<License, Set<License>> CONFLICTS;
    private static final Map<License, Set<License>> NEED_MENTION_IN_LICENSE;
    private static final Map<License, Set<License>> NEED_MENTION_IN_NOTICE;

    static
    {
        Map<License, Set<License>> conflicts = new HashMap<>();
        EnumSet<License> apache2Conflicts = EnumSet.allOf( License.class );
        apache2Conflicts.removeAll( EnumSet.of(
            // Licenses compatible with Apache 2
            APACHE_2, APACHE_1_1, PHP_3_1, BSD_2_CLAUSES, BSD_3_CLAUSES,
            MIT_X11, ICU, UoL_NCSA, W3C, ZLIB, AFL, MS_PL, CC_A, PYTHON, BSL, EDL, WTFPL, RUBY, CPL,
            PUBLIC_DOMAIN, CDDL, JSON
        ) );
        conflicts.put( APACHE_2, apache2Conflicts );
        CONFLICTS = conflicts;

        Map<License, Set<License>> needLicense = new HashMap<>();
        needLicense.put( APACHE_2, EnumSet.of( BSD_2_CLAUSES, BSD_3_CLAUSES, CDDL, CPL, EPL, MOZILLA ) );
        NEED_MENTION_IN_LICENSE = needLicense;

        Map<License, Set<License>> needNotice = new HashMap<>();
        needNotice.put( APACHE_2, EnumSet.of( BSD_2_CLAUSES, BSD_3_CLAUSES, CDDL, CPL, EPL, MOZILLA ) );
        NEED_MENTION_IN_NOTICE = needNotice;
    }
}
