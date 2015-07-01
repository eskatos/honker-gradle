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

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.plugins.JavaPlugin

import org.nosphere.honker.License

/**
 * Honker Plugin.
 * <p>
 * Crawls dependencies for licensing information, detects ambiguities and conflicts,
 * generates LICENSE, NOTICE and DEPENDENCIES files.
 */
class HonkerPlugin implements Plugin<Project> {

    void apply(Project project) {
        if( !project.plugins.hasPlugin(JavaPlugin) ) {
            project.plugins.apply(JavaPlugin)
        }
        project.extensions.create( 'honker', HonkerExtension )
        Task reportTask = project.task(
            'honkerReport',
            type: HonkerReportTask,
            group: 'Honker',
            description: 'Report dependencies licensing.'
        )
        Task checkTask = project.task(
            'honkerCheck',
            type: HonkerCheckTask,
            group: 'Honker',
            description: 'Check for dependencies licensing issues (missing and conflicts).'
        )
        Task genLicenseTask = project.task(
            'honkerGenLicense',
            type: HonkerGenLicenseTask,
            group: 'Honker',
            description: 'Generate project\'s LICENSE file.'
        )
        Task genNoticeTask = project.task(
            'honkerGenNotice',
            type: HonkerGenNoticeTask,
            group: 'Honker',
            description: 'Generate project\'s NOTICE file.'
        )
        Task genDependenciesTask = project.task(
            'honkerGenDependencies',
            type: HonkerGenDependenciesTask,
            group: 'Honker',
            description: 'Generate project\'s DEPENDENCIES file.'
        )
        project.afterEvaluate { proj ->
            if(proj.honker.license && !License.valueOfLicenseName( proj.honker.license )) {
                throw new GradleException( "Invalid/unknown project's license: '$proj.honker.license'" );
            }
        }
    }

}

