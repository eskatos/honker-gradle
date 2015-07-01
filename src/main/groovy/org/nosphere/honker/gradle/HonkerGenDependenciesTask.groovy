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
package org.nosphere.honker.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

import org.nosphere.honker.gradle.deptree.GradleDepTreeLoader
import org.nosphere.honker.visitors.DependenciesByOrganizationsVisitor

/**
 * DEPENDENCIES Generation Task.
 */
class HonkerGenDependenciesTask extends DefaultTask {

    @Input
    def configuration = project.configurations.runtime

    @Optional @Input
    def String header

    @Optional @Input
    def String footer

    @OutputDirectory
    def File outputDir = project.file( "$project.buildDir/generated-resources/dependencies" )

    @Input
    def String resourcePath = 'META-INF/DEPENDENCIES.txt'

    @TaskAction
    void generate()
    {
        def depTree = new GradleDepTreeLoader( project, configuration.resolvedConfiguration ).load()
        def depsVisitor = new DependenciesByOrganizationsVisitor()
        depTree.accept( depsVisitor )

        def File target = new File( outputDir, resourcePath )
        target.parentFile.mkdirs()

        def dependencies = depsVisitor.dependenciesByOrganizations
        def depsText = ''
        if( header ) {
            depsText += "$header\n"
        }
        depsText += """
            // ------------------------------------------------------------------
            // Transitive dependencies of this project determined from the
            // build dependencies listed by organization.
            // ------------------------------------------------------------------

            ${project.honker.projectName ?: project.name}

            """.stripIndent()
        dependencies.keySet().each { orgName ->
            def artifacts = dependencies[orgName]
            def orgUrl = artifacts.iterator()[0].organizationUrl
            depsText += "\nFrom: $orgName ${orgUrl ? "- $orgUrl" : ''}\n\n"
            artifacts.each { artifact ->
                depsText += "  - ${artifact.name} ${artifact.version} (${artifact.coordinates}) ${artifact.url}\n"
                artifact.getDetectedLicenses().each { lic ->
                    depsText += "    License: ${lic.preferedName} ${lic.preferedUrl ? "- ${lic.preferedUrl}" : ''}\n"
                }
            }
        }
        if( footer ) {
            depsText += "\n$footer"
        }
        target.text = depsText
        project.logger.info "Generated DEPENDENCIES file into $target.absolutePath"
    }
}
