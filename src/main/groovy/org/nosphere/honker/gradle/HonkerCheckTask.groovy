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

import groovy.transform.CompileStatic;
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction

import org.nosphere.honker.License
import org.nosphere.honker.gradle.deptree.GradleDepTreeLoader
import org.nosphere.honker.visitors.LicenseConflictVisitor
import org.nosphere.honker.visitors.LicensePresenceVisitor

@CompileStatic
class HonkerCheckTask extends DefaultTask
{
  @InputFiles
  Configuration configuration = project.configurations.getByName 'runtime'

  @TaskAction
  void check()
  {
    def honker = project.extensions.getByType HonkerExtension
    def depTree = new GradleDepTreeLoader( project, configuration ).load()

    def errors = [ ] as List<String>

    // Ensure no artifact without license
    def presenceVisitor = new LicensePresenceVisitor()
    depTree.accept presenceVisitor
    def noLic = presenceVisitor.artifactsWithoutLicense()
    if( !noLic.isEmpty() )
    {
      errors.addAll noLic.collect {
        "  $it.coordinates no licensing data could be found".toString()
      }
    }

    if( honker.license )
    {
      def lic = License.valueOfLicenseName honker.license

      // Ensure no artifact with licensing conflict
      def conflictVisitor = new LicenseConflictVisitor( lic )
      depTree.accept conflictVisitor
      def conflicts = conflictVisitor.conflicts()
      if( !conflicts.isEmpty() )
      {
        errors.addAll conflicts.collect {
          "  $it.coordinates ${ it.detectedLicenses.collect { it.preferedName }.join( ' / ' ) } conflicts with $lic.preferedName".toString()
        }
      }
    }

    if( errors )
    {
      if( errors.size() == 1 )
      {
        throw new GradleException( errors[ 0 ] )
      }
      throw new GradleException( "License check failures: ${ errors.size() }\n" + errors.join( '\n' ) )
    }
  }
}
