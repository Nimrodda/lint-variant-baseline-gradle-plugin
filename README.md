# Android Lint Variant Baseline

A Gradle plugin that adds support to baseline XML per Android variant for both app and library modules.

## How does it work?

The plugin stores each `lint-baseline.xml` file under a corresponding variant folder. When you run lint for a specific
variant, the plugin registers a dependency on a task that copies the baseline file to the location you specify in 
lintOptions block.

Simply execute your lint task as usual, for example: `./gradlew lintPaidRelease`.

## Available tasks

Before running lint, you want to generate the baseline file per variant. The plugin adds the following tasks to help
with that:

### generateLintBaseline{VariantName} -Dlint.baselines.continue=true

This task will run lint to generate the baseline file at the location you specified in lintOptions block and after that,
it will copy the file to the variant specific folder. Note that this file will overwrite existing baseline files.

Example: `./gradlew generateLintBaselinePaidRelease -Dlint.baselines.continue=true`

>Note that `-Dlint.baselines.continue=true` is necessary so that lint will continue without failing the build.

### deleteLintBaseline

Deletes the baseline file specified in lintOptions, e.g.: the one used by Android Gradle plugin, not the variant
specific one. This task can be used when you starting with this plugin. It is recommended that you first generate
the variant specific baseline files and then use this task to clean up the leftovers in the module root folder.

Example: `./gradlew deleteLintBaseline`

### copyLintBaseline{VariantName}

This task is run just before lint<VariantName> as a dependency. It is not meant to be run by the user.

Example: `./gradlew copyLintBaselinePaidRelease`

## Download

```Gradle
plugins {
    id "com.nimroddayan.lint-variant-baseline" version "0.1.0"
}
``` 

## Config

It is recommended to git ignore baseline files at the module root like so:

```
# We ignore baseline files in module root
# each module has baseline stored per variant which is the one that is added to git
*/lint-baseline.xml

``` 

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
