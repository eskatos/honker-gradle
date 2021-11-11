/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.nosphere.honker

import nebula.test.IntegrationSpec
import nebula.test.functional.ExecutionResult
import spock.lang.Unroll

/**
 * Honker Plugin IntegrationSpec.
 */
class HonkerPluginIntegrationSpec extends IntegrationSpec {

    def build = '''
            apply plugin: 'org.nosphere.honker'
            honker {
                license 'Apache License 2.0'
            }
            sourceSets {
                main {
                    output.dir( honkerGenDependencies.outputDir, builtBy: honkerGenDependencies )
                    output.dir( honkerGenLicense.outputDir, builtBy: honkerGenLicense )
                    output.dir( honkerGenNotice.outputDir, builtBy: honkerGenNotice )
                }
            }
            honkerGenNotice { footer = 'This product includes software developed at\\nThe Apache Software Foundation (http://www.apache.org/).\\n' }
            repositories { mavenCentral() }
            dependencies {

                // MIT
                implementation 'org.slf4j:slf4j-api:1.7.12'

                // APACHE_2
                implementation 'org.ow2.asm:asm:5.0.4'
                implementation 'joda-time:joda-time:2.3'

                // EPL
                implementation 'junit:junit:4.12'

                // GPL- Conflict
                implementation 'mysql:mysql-connector-java:5.1.35'

                // APACHE_2 - Complex License information in Manifest
                implementation 'org.osgi:org.osgi.core:4.2.0'

                // No license data!
                implementation 'asm:asm:3.1'

            }
        '''.stripIndent()

    @Unroll
    def 'runs honkerReport (gradle=#testedGradleVersion)'() {
        setup:
        fork = true
        gradleVersion = testedGradleVersion
        buildFile << build

        when:
        ExecutionResult result = runTasksSuccessfully 'honkerReport'

        then:
        result.wasExecuted 'honkerReport'

        where:
        testedGradleVersion << TestEnv.TESTED_GRADLE_VERSIONS
    }

    @Unroll
    def 'honkerCheck failures (gradle=#testedGradleVersion)'() {
        setup:
        fork = true
        gradleVersion = testedGradleVersion
        buildFile << build

        when:
        ExecutionResult result = runTasksWithFailure 'honkerCheck'

        then:
        result.wasExecuted 'honkerCheck'
        result.standardError.contains 'Execution failed for task \':honkerCheck\'.'
        result.standardError.contains 'License check failures: 2'
        result.standardError.contains 'mysql:mysql-connector-java:5.1.35:jar GNU General Public License conflicts with The Apache Software License, Version 2.0'
        result.standardError.contains 'asm:asm:3.1:jar no licensing data could be found'

        where:
        testedGradleVersion << TestEnv.TESTED_GRADLE_VERSIONS
    }

    @Unroll
    def 'honkerGenAll generate DEPENDENCIES, LICENSE and NOTICE files that are present in JAR (gradle=#testedGradleVersion)'() {
        setup:
        fork = true
        gradleVersion = testedGradleVersion
        buildFile << build

        when:
        ExecutionResult result = runTasksSuccessfully 'jar'

        then:

        result.wasExecuted 'honkerGenDependencies'
        fileExists 'build/generated-resources/dependencies/META-INF/DEPENDENCIES.txt'
        def dependenciesText = file('build/generated-resources/dependencies/META-INF/DEPENDENCIES.txt').text
        dependenciesText.contains 'From: France Telecom R&D'

        result.wasExecuted 'honkerGenLicense'
        fileExists 'build/generated-resources/license/META-INF/LICENSE.txt'
        def licenseText = file('build/generated-resources/license/META-INF/LICENSE.txt').text
        licenseText.contains 'TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION'

        result.wasExecuted 'honkerGenNotice'
        fileExists 'build/generated-resources/notice/META-INF/NOTICE.txt'
        def noticeText = file('build/generated-resources/notice/META-INF/NOTICE.txt').text
        noticeText.contains "Copyright ${Calendar.getInstance().get(Calendar.YEAR)} The Apache Software Foundation"
        noticeText.contains 'This product includes software developed at'

        where:
        testedGradleVersion << TestEnv.TESTED_GRADLE_VERSIONS
    }

    @Unroll
    def 'dependency license override (gradle=#testedGradleVersion)'() {
        setup:
        fork = true
        gradleVersion = testedGradleVersion
        buildFile << build + '''
        honker {
            licenseOverride { candidate ->
                if( candidate.group == 'asm' && candidate.module == 'asm' ) {
                    candidate.license = 'BSD 3-Clause'
                }
            }
        }
        '''.stripIndent()

        when:
        ExecutionResult result = runTasksWithFailure 'honkerCheck'

        then:
        result.wasExecuted 'honkerCheck'
        result.standardError.contains 'Execution failed for task \':honkerCheck\'.'
        result.standardError.contains 'mysql:mysql-connector-java:5.1.35:jar GNU General Public License conflicts with The Apache Software License, Version 2.0'
        !result.standardError.contains('asm:asm:3.1:jar no licensing data could be found')

        where:
        testedGradleVersion << TestEnv.TESTED_GRADLE_VERSIONS
    }
}
