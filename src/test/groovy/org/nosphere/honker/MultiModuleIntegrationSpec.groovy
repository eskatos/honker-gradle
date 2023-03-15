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

import org.gradle.testkit.runner.BuildResult
import spock.lang.Unroll

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

/**
 * Multi-module project Honker Plugin IntegrationSpec.
 */
class MultiModuleIntegrationSpec extends AbstractIntegrationSpec {

    def build = '''
        plugins {
            id 'base'
            id 'org.nosphere.honker' apply false
        }
        allprojects {
            group 'acme'
        }
        subprojects {
            apply plugin: 'org.nosphere.honker'
            honker {
                license = 'Apache License 2.0'
            }
            /*
            sourceSets {
                main {
                    output.dir( honkerGenDependencies.outputDir, builtBy: honkerGenDependencies )
                    output.dir( honkerGenLicense.outputDir, builtBy: honkerGenLicense )
                    output.dir( honkerGenNotice.outputDir, builtBy: honkerGenNotice )
                }
            }
            */
            repositories { mavenCentral() }
        }
    '''.stripIndent()

    def settings = '''
        include 'api', 'core'
        rootProject.name = 'root'
        '''.stripIndent()

    def apiBuild = '''
        dependencies {
            implementation 'org.slf4j:slf4j-api:1.7.12'
        }
    '''.stripIndent()

    def coreBuild = '''
        dependencies {
            implementation project( ':api' )
        }
    '''.stripIndent()

    @Unroll
    def 'runs honkerReport (gradle=#testedGradleVersion)'() {
        setup:
        gradleVersion = testedGradleVersion
        buildFile << build
        createFile('settings.gradle') << settings
        createFile('api/build.gradle') << apiBuild
        createFile('core/build.gradle') << coreBuild

        when:
        BuildResult result = runTasksSuccessfully('honkerReport')

        then:
        result.task(':api:honkerReport').outcome == SUCCESS
        result.task(':core:honkerReport').outcome == SUCCESS

        where:
        testedGradleVersion << TestEnv.TESTED_GRADLE_VERSIONS
    }

    @Unroll
    def 'runs honkerGenDependencies (gradle=#testedGradleVersion)'() {
        setup:
        gradleVersion = testedGradleVersion
        buildFile << build
        createFile('settings.gradle') << settings
        createFile('api/build.gradle') << apiBuild
        createFile('core/build.gradle') << coreBuild

        when:
        BuildResult result = runTasksSuccessfully('honkerGenDependencies')

        then:
        result.task(':api:honkerGenDependencies').outcome == SUCCESS
        fileExists('api/build/generated-resources/dependencies/META-INF/DEPENDENCIES.txt')
        def apiDeps = file('api/build/generated-resources/dependencies/META-INF/DEPENDENCIES.txt').text
        println "==== api ====\n$apiDeps\n========"
        apiDeps.contains 'org.slf4j:slf4j-api'

        and:
        result.task(':core:honkerGenDependencies').outcome == SUCCESS
        fileExists('core/build/generated-resources/dependencies/META-INF/DEPENDENCIES.txt')
        def coreDeps = file('core/build/generated-resources/dependencies/META-INF/DEPENDENCIES.txt').text
        println "==== core ====\n$coreDeps\n========"
        coreDeps.contains 'org.slf4j:slf4j-api'
        coreDeps.contains 'acme:api'

        where:
        testedGradleVersion << TestEnv.TESTED_GRADLE_VERSIONS
    }
}

