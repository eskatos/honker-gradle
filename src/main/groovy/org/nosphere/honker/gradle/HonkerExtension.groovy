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

import org.gradle.api.Project

public class HonkerExtension {

    String license
    String projectName
    String projectTimespan
    String projectOrganization

    void license( String license ) {
        this.license = license
    }

    void projectName( String projectName ) {
        this.projectName = projectName
    }

    void projectTimespan( String projectTimespan ) {
        this.projectTimespan = projectTimespan
    }

    void projectOrganization( String projectOrganization ) {
        this.projectOrganization = projectOrganization
    }

    private final Project project

    public HonkerExtension( Project project ) {
        this.project = project
    }
}
