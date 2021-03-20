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
import org.gradle.api.artifacts.Configuration
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.TaskAction

import org.nosphere.honker.gradle.deptree.GradleDepTreeLoader
import org.nosphere.honker.visitors.LicensingReportVisitor

@CompileStatic
class HonkerReportTask extends DefaultTask
{
  @InputFiles
  Configuration configuration = project.configurations.getByName 'runtime'

  @TaskAction
  void report()
  {
    def depTree = new GradleDepTreeLoader( project, configuration ).load()
    def visitor = new LicensingReportVisitor();

    println "------------------------------------------------------------------------------------------------------"
    println ""
    depTree.accept( visitor );
    println ""
    println "------------------------------------------------------------------------------------------------------"
  }
}
