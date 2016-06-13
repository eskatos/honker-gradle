# Honker

> The Honker Gradle Plugin helps you assemble legal bits

**WARNING** Only handle The Apache Software License, version 2.0, for now.

Build script snippet for use in all Gradle versions:

    buildscript {
      repositories {
        maven {
          url "https://plugins.gradle.org/m2/"
        }
      }
      dependencies {
        classpath "gradle.plugin.org.nosphere.honker:honker-gradle:0.2.2"
      }
    }

    apply plugin: "org.nosphere.honker"

Build script snippet for new, incubating, plugin mechanism introduced in Gradle 2.1:

    plugins {
      id "org.nosphere.honker" version "0.2.2"
    }


## DSL Extension

    honker {
        // Your project license
        license 'Apache 2'

        // Your project information, used to generate NOTICE files
        projectName 'Your Fancy Project'
        projectTimespan '2010-2015'
        projectOrganization 'ACME Inc.'

        // For dependencies that contains no licensing information (pom, manifest etc..)
        // you can define your licensing strategy, for example:
        licenseOverride { candidate ->
            if( candidate.group == 'something' && candidate.module == 'whatever' ) {
                candidate.license = 'BSD 3-Clause'
            }
        }
    }


## Usage

### Check for licensing conflicts

Simply invoke the `honkerCheck` task.

To hook it into the default `check` task:

    check.dependsOn honkerCheck


### DEPENDENCIES, NOTICE and LICENSE in META-INF

    sourceSets {
        main {
            output.dir( honkerGenDependencies.outputDir, builtBy: honkerGenDependencies )
            output.dir( honkerGenLicense.outputDir, builtBy: honkerGenLicense )
            output.dir( honkerGenNotice.outputDir, builtBy: honkerGenNotice )
        }
    }

