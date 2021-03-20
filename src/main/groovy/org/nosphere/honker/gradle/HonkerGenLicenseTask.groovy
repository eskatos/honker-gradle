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

import groovy.text.SimpleTemplateEngine
import groovy.transform.CompileStatic
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.nosphere.honker.License

/**
 * LICENSE Generation Task.
 */
@CompileStatic
class HonkerGenLicenseTask extends DefaultTask
{

  @Optional
  @InputFile
  File template = null

  @Optional
  @Input
  String header

  @Optional
  @Input
  String footer

  @OutputDirectory
  File outputDir = project.file "$project.buildDir/generated-resources/license"

  @Input
  String resourcePath = 'META-INF/LICENSE.txt'

  @TaskAction
  void generate()
  {
    def honker = project.extensions.getByType HonkerExtension
    File target = new File( outputDir, resourcePath )
    target.parentFile.mkdirs()

    def lic = License.valueOfLicenseName honker.license
    def templateText = template?.exists() ? template.text : lic.licenseTemplate()
    if( templateText )
    {

      def binding = [
        'projectName'        : honker.projectName ?: project.name,
        'projectTimespan'    : honker.projectTimespan ?: "${ Calendar.getInstance().get( Calendar.YEAR ) }",
        'projectOrganization': honker.projectOrganization,
        'header'             : header, 'footer': footer
      ]
      target.text = new SimpleTemplateEngine().createTemplate( templateText ).make( binding ).toString()
      project.logger.info "Generated LICENSE file into $target.absolutePath"
    }
    else
    {
      project.logger.warn 'No LICENSE template, no LICENSE file will be generated'
    }
  }
}
