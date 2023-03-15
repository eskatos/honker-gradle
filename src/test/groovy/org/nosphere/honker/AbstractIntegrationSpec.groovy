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
import org.gradle.testkit.runner.GradleRunner
import spock.lang.Specification
import spock.lang.TempDir

class AbstractIntegrationSpec extends Specification {

    private static final List<String> EXTRA_ARGS = ['--stacktrace', '--warning-mode=fail']

    @TempDir
    File tmpDir

    GradleDsl dsl = GradleDsl.GROOVY

    enum GradleDsl {
        GROOVY, KOTLIN
    }

    String gradleVersion = null

    File buildRootDir

    def setup() {
        buildRootDir = new File(tmpDir, "test-root")
        buildRootDir.mkdirs()
        settingsFile << ''
    }

    String getGradleScriptExtension() {
        switch (dsl) {
            case GradleDsl.GROOVY:
                return '.gradle'
            case GradleDsl.KOTLIN:
                return '.gradle.kts'
            default:
                throw new IllegalStateException()
        }
    }

    File file(String path) {
        return new File(buildRootDir, path)
    }

    File createFile(String path) {
        return file(path).tap { file ->
            file.parentFile.mkdirs()
            file.text = ''
        }
    }

    boolean fileExists(String path) {
        return file(path).exists()
    }

    File getBuildFile() {
        return createFile("build$gradleScriptExtension")
    }

    File getSettingsFile() {
        return createFile("settings$gradleScriptExtension")
    }

    File dir(String path) {
        return file(path)
    }

    File createDir(String path) {
        return dir(path).tap { dir ->
            dir.mkdirs()
        }
    }

    void printTree(String path = null) {
        File file = path ? file(path) : buildRootDir
        if (file.isFile()) println(path)
        else file.traverse { println(buildRootDir.relativePath(it)) }
    }

    GradleRunner gradleRunnerFor(String... arguments) {
        return GradleRunner.create()
                .withProjectDir(buildRootDir)
                .withPluginClasspath()
                .forwardOutput()
                .withArguments(arguments as List<String> + EXTRA_ARGS)
                .tap { runner ->
                    if (gradleVersion != null) {
                        runner.withGradleVersion(gradleVersion)
                    }
                }
    }

    BuildResult runTasksSuccessfully(String... arguments) {
        return gradleRunnerFor(arguments).build()
    }

    BuildResult runTasksWithFailure(String... arguments) {
        return gradleRunnerFor(arguments).buildAndFail()
    }
}
