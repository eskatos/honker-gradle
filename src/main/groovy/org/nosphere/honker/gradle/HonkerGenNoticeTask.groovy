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
package org.nosphere.honker.gradle;

import groovy.text.SimpleTemplateEngine

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

import org.nosphere.honker.License

/**
 * NOTICE Generation Task.
 */
class HonkerGenNoticeTask extends DefaultTask {

    @Optional @Input
    def File template = null

    @Optional @Input
    def String header

    @Optional @Input
    def String footer
    
    @OutputDirectory
    def File outputDir = project.file( "$project.buildDir/generated-resources/notice" )

    @Input
    def String resourcePath = 'META-INF/NOTICE.txt'

    @TaskAction
    void generate()
    {
        def File target = new File( outputDir, resourcePath )
        target.parentFile.mkdirs()

        def lic = License.valueOfLicenseName( project.honker.license )
        def templateText = template?.exists() ? template.text : lic.noticeTemplate()
        if( templateText ) {

            def binding = [
                'projectName': project.honker.projectName ?: project.name,
                'projectTimespan': project.honker.projectTimespan ?: "${Calendar.getInstance().get( Calendar.YEAR )}",
                'projectOrganization': project.honker.projectOrganization,
                'header': header, 'footer': footer
            ]
            target.text = new SimpleTemplateEngine().createTemplate( templateText ).make( binding ).toString()
            project.logger.info "Generated NOTICE file into $target.absolutePath"

        } else {
            project.logger.warn 'No NOTICE template, no NOTICE file will be generated'
        }
    }
}
