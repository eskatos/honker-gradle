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
package org.apache.gradle.licensing

import nebula.test.IntegrationSpec
import nebula.test.functional.ExecutionResult

/**
 * Honker Plugin IntegrationSpec.
 */
class HonkerPluginIntegrationSpec extends IntegrationSpec {

    def build = '''
            apply plugin: 'org.nosphere.honker'
            honker {
                license = 'Apache License 2.0'
            }
            sourceSets {
                main {
                    output.dir( honkerGenDependencies.outputDir, builtBy: honkerGenDependencies )
                    output.dir( honkerGenLicense.outputDir, builtBy: honkerGenLicense )
                    output.dir( honkerGenNotice.outputDir, builtBy: honkerGenNotice )
                }
            }
            honkerGenNotice { footer = 'This product includes software developed at\\nThe Apache Software Foundation (http://www.apache.org/).' }
            repositories { jcenter() }
            dependencies {

                // MIT
                compile 'org.slf4j:slf4j-api:1.7.12'

                // APACHE_2
                compile 'org.ow2.asm:asm:5.0.4'
                compile 'joda-time:joda-time:2.3'

                // CPL & BSD_3_CLAUSES
                compile 'junit:junit:4.11'

                // GPL- Conflict
                compile 'mysql:mysql-connector-java:5.1.35'

                // No license data!
                compile 'org.antlr:stringtemplate:3.2.1'

            }
        '''.stripIndent()

    def 'runs honkerReport'() {
        setup:
        fork = true
        buildFile << build
        
        when:
        ExecutionResult result = runTasksSuccessfully('honkerReport')

        then:
        wasExecuted('honkerReport')
    }

    def 'honkerCheck failures'() {
        setup:
        fork=true
        buildFile << build

        when:
        ExecutionResult result = runTasksWithFailure('honkerCheck')

        then:
        wasExecuted('honkerCheck')
        result.standardError.contains('Execution failed for task \':honkerCheck\'.')
        result.standardError.contains('License check failures: 2')
        result.standardError.contains('mysql:mysql-connector-java:5.1.35:jar \'GPL\' licensed conflicts with the \'Apache 2\' license')
        result.standardError.contains('org.antlr:stringtemplate:3.2.1:jar licensing data not found')
    }

    def 'honkerGenNotice generate NOTICE file and is present in JAR'() {
        setup:
        fork=true
        buildFile << build

        when:
        ExecutionResult result = runTasksSuccessfully('jar')

        then:

        wasExecuted('honkerGenDependencies')
        fileExists('build/generated-resources/dependencies/META-INF/DEPENDENCIES.txt')
        def dependenciesText = file('build/generated-resources/dependencies/META-INF/DEPENDENCIES.txt').text
        dependenciesText.contains 'From: France Telecom R&D'

        wasExecuted('honkerGenLicense')
        fileExists('build/generated-resources/license/META-INF/LICENSE.txt')
        def licenseText = file('build/generated-resources/license/META-INF/LICENSE.txt').text
        licenseText.contains 'TERMS AND CONDITIONS FOR USE, REPRODUCTION, AND DISTRIBUTION'

        wasExecuted('honkerGenNotice')
        fileExists('build/generated-resources/notice/META-INF/NOTICE.txt')
        def noticeText = file('build/generated-resources/notice/META-INF/NOTICE.txt').text
        noticeText.contains 'Copyright 2015 The Apache Software Foundation'
        noticeText.contains 'This product includes software developed at'
    }
}
