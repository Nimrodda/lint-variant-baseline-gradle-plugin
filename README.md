# Android Lint Variant Baseline

A Gradle plugin that adds support to baseline XML per Android variant for both app and library modules.

## How does it work?

The plugin stores each `lint-baseline.xml` file under a corresponding variant folder. When you run lint for a specific
variant, the plugin registers a dependency on a task that copies the baseline file to the location you specify in 
lintOptions block.

## Available tasks

The plugin adds the following tasks per variant:

### generateLintBaseline<VariantName>

This task will run lint to generate the baseline file at the location you specified in lintOptions block and after that,
it will copy the file to the variant specific folder.

### copyLintBaseline<VariantName>

This task is run just before lint<VariantName> as a dependency. It is not meant to be run by the user.

## Download

```Gradle
plugins {
    id "com.nimroddayan.lint-variant-baseline" version "0.1.0"
}
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
