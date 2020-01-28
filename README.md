# Android Lint Variant Baseline

A Gradle plugin that adds support to baseline XML per Android variant for both app and library modules.

## How does it work?

The plugin stores each `lint-baseline.xml` file under a corresponding variant folder. When you run lint for a specific
variant, the plugin registers a dependency on a task that copies the baseline file to the location you specify in 
lintOptions block.

## Usage

### 1. Gradle configuration

```Gradle
plugins {
    id "com.nimroddayan.lint-variant-baseline" version "0.1.0"
}

// Apply the plugin and configure Android Gradle plugin Lint options per each module
subprojects {
    apply plugin: 'com.nimroddayan.lint-variant-baseline'

    def configureKotlin = {
        android {
            lintOptions {
                baseline file("lint-baseline.xml") // Relative to the module root
                checkDependencies false
                checkReleaseBuilds true
                abortOnError true
        
                // Custom Lint configuration file for ignoring specific lint checks
                lintConfig file("$rootDir/lint-config.xml")
            }
        }
        
        plugins.withType(com.android.build.gradle.AppPlugin, configureKotlin)
        plugins.withType(com.android.build.gradle.LibraryPlugin, configureKotlin)
    }
}

``` 

### 2. Generating the baseline files per module variant

Let's assume that we have one product flavor defined in the app and some feature modules like so:

```
android {
    flavorDimensions "type"
    productFlavors {
        free {
            dimension "type"
        }
        paid {
            dimension "type"
        }
    }
}
```

Now that you have the Gradle config in place, you can run the tasks to generate baseline files per each module
 in the project per variant:

```
./gradlew generateLintBaselinePaidRelease -Dlint.baselines.continue=true \
generateLintBaselineFreeRelease -Dlint.baselines.continue=true \
generateLintBaselinePaidDebug -Dlint.baselines.continue=true \
generateLintBaselineFreeDebug -Dlint.baselines.continue=true
```

For library modules which don't have product flavors, you can run generate baseline files like so:

```
./gradlew generateLintBaselineRelease -Dlint.baselines.continue=true \
generateLintBaselineRelease -Dlint.baselines.continue=true \
generateLintBaselineDebug -Dlint.baselines.continue=true \
generateLintBaselineDebug -Dlint.baselines.continue=true
```

The above will execute the tasks for all modules which apply the Android Gradle app or library plugin.
 
>Note that `-Dlint.baselines.continue=true` is necessary so that lint will continue without failing the build.

When the tasks are finished, you will see baseline files in the variant source folder. For example:

```
/
/app/src/paidRelease/lint-baseline.xml
/app/src/paidDebug/lint-baseline.xml
/app/src/freeRelease/lint-baseline.xml
/app/src/freeDebug/lint-baseline.xml
/feature/src/paidRelease/lint-baseline.xml
/feature/src/paidDebug/lint-baseline.xml
/feature/src/freeRelease/lint-baseline.xml
/feature/src/freeDebug/lint-baseline.xml
```

>Note that you will also see baseline files in modules' root. **You
should ignore these files in your .gitignore. See the section below about it.**

### 3. Version control

It is recommended to git ignore baseline files at the module root and exclude the variant specific ones:

```
# Ignore baseline file in module root, but keep ones in variant specific folders
lint-baseline.xml
!**/*[Rr]elease/lint-baseline.xml
!**/*[Dd]ebug/lint-baseline.xml

``` 

### 4. Running lint

After you generated the baseline files, run lint as you'd normally would. The plugin adds a task which is run prior to the
lint task to copy the variant specific baseline file to the module root.

For example: `./gradlew lintPaidRelease`.

## Available tasks

The plugin exposes the following tasks:

### generateLintBaseline{VariantName} -Dlint.baselines.continue=true

This task will run lint to generate the baseline file at the location you specified in lintOptions block and after that,
it will copy the file to the variant specific folder. Note that this file will overwrite existing baseline files.

Example: `./gradlew generateLintBaselinePaidRelease -Dlint.baselines.continue=true`

>Note that `-Dlint.baselines.continue=true` is necessary so that lint will continue without failing the build.

### deleteLintBaseline

Deletes the baseline file specified in lintOptions, e.g.: the one in the module's root, not the variant
specific one. This task can be used when you're setting up this plugin. It is recommended that you first generate
the variant specific baseline files and then use this task to clean up the leftovers in the module root folder.

Note that after each time you run lint, a new baseline file will appear in the module root and therefore it is
recommended to simply ignore these files in .gitignore. See section "Version control" above.

Example: `./gradlew deleteLintBaseline`

### copyLintBaseline{VariantName}

This task is run just before lint<VariantName> as a dependency. It is not meant to be run by the user.

Example: `./gradlew copyLintBaselinePaidRelease`

## Contributing

Make a Pull Request.

## Reporting an issue

Use Github issue tracker.

## License

Copyright 2020 Nimrod Dayan nimroddayan.com

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
