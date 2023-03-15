# Honker

> The Honker Gradle Plugin helps you assemble legal bits

**WARNING** Only handle The Apache Software License, version 2.0, for now.

Build script snippet for use in all Gradle versions >= 2.1:

```groovy
plugins {
  id "org.nosphere.honker" version "0.3.0"
}
```

Build script snippet for previous Gradle versions

```groovy
buildscript {
  repositories {
    maven {
      url "https://plugins.gradle.org/m2/"
    }
  }
  dependencies {
    classpath "gradle.plugin.org.nosphere.honker:honker-gradle:0.3.0"
  }
}

apply plugin: "org.nosphere.honker"
```

## DSL Extension

```groovy
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
```

## Usage

### Check for licensing conflicts

Simply invoke the `honkerCheck` task.

To hook it into the default `check` task:

```groovy
check.dependsOn honkerCheck
```


### DEPENDENCIES, NOTICE and LICENSE in META-INF

```groovy
sourceSets {
    main {
        output.dir( honkerGenDependencies.outputDir, builtBy: honkerGenDependencies )
        output.dir( honkerGenLicense.outputDir, builtBy: honkerGenLicense )
        output.dir( honkerGenNotice.outputDir, builtBy: honkerGenNotice )
    }
}
```

## Compatibility matrix

| Plugin | Min Java | Min Gradle | Max Gradle | [Configuration Cache](https://docs.gradle.org/current/userguide/configuration_cache.html) | [Build Cache](https://docs.gradle.org/current/userguide/build_cache.html)
| --- | --- | --- | --- | --- | ---
| `0.4.0` | `1.8` | `5.x`  | `7.x` | 🔴 | 🟢
| `0.3.2` | `1.8` | `5.x`  | `7.x` | 🔴 | 🟢
| `0.2.3` | `1.7` | `2.14` | `6.x` | 🔴 | 🟢
| `0.1.5` | `1.6` | `2.14` | `4.x` | 🔴 | 🟢

