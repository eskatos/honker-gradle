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
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ResolvedArtifact
import org.gradle.api.artifacts.ResolvedDependency

import org.nosphere.honker.deptree.Gav

@CompileStatic
class HonkerUtils
{

  static Gav gavOf( Dependency dependency )
  {
    return new Gav( dependency.getGroup(), dependency.getName(), dependency.getVersion() )
  }

  static Gav gavOf( ResolvedDependency dependency )
  {
    return new Gav( dependency.getModuleGroup(), dependency.getModuleName(), dependency.getModuleVersion() )
  }

  static Gav gavOf( ResolvedArtifact artifact )
  {
    return new Gav(
      artifact.getModuleVersion().getId().getGroup(),
      artifact.getModuleVersion().getId().getName(),
      artifact.getModuleVersion().getId().getVersion()
    )
  }

  private HonkerUtils()
  {
  }
}
