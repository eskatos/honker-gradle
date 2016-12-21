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

import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.Project

@CompileStatic
class HonkerExtension
{

  String license
  String projectName
  String projectTimespan
  String projectOrganization
  List<Action<HonkerLicenseOverrideCandidate>> licenseOverrides = [ ]

  void license( String license )
  {
    this.license = license
  }

  void projectName( String projectName )
  {
    this.projectName = projectName
  }

  void projectTimespan( String projectTimespan )
  {
    this.projectTimespan = projectTimespan
  }

  void projectOrganization( String projectOrganization )
  {
    this.projectOrganization = projectOrganization
  }

  void licenseOverride( Action<HonkerLicenseOverrideCandidate> action )
  {
    licenseOverrides.add( action )
  }

  List<Action<HonkerLicenseOverrideCandidate>> getLicenseOverrides()
  {
    return licenseOverrides
  }

  private final Project project

  HonkerExtension( Project project )
  {
    this.project = project
  }
}
