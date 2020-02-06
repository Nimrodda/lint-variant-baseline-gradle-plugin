/*
 * Copyright 2020 Nimrod Dayan nimroddayan.com
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nimroddayan.androidlintvariantbaseline

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.android.build.gradle.internal.dsl.LintOptions
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Delete
import java.io.File

private const val DELETE_BASELINE_TASK_NAME = "deleteLintBaseline"

/**
 * Gradle plugin that adds support for lint-baseline.xml file per Android variant
 * in app and library modules.
 */
@Suppress("unused")
class AndroidLintVariantBaselinePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.plugins.withType(AppPlugin::class.java) {
            val extension = project.extensions.getByName("android") as BaseAppModuleExtension
            extension.applicationVariants.all { variant ->
                createPluginTasks(project, variant.name, extension.lintOptions)
            }
            registerDeleteLintBaselineTask(project, extension.lintOptions)
        }
        project.plugins.withType(LibraryPlugin::class.java) {
            val extension = project.extensions.getByName("android") as LibraryExtension
            extension.libraryVariants.all { variant ->
                createPluginTasks(project, variant.name, extension.lintOptions)
            }
            registerDeleteLintBaselineTask(project, extension.lintOptions)
        }
        System.setProperty("lint.baselines.continue", "true")
    }

    private fun createPluginTasks(
        project: Project,
        variantName: String,
        lintOptions: LintOptions
    ) {
        registerCopyBaselineTask(project, variantName, lintOptions)
        registerGenerateLintBaselineTask(project, variantName, lintOptions)
        registerDeleteVariantBaselineTask(project, variantName, lintOptions)
    }

    private fun registerCopyBaselineTask(
        project: Project,
        variantName: String,
        lintOptions: LintOptions
    ) {
        val variantNameCaps = variantName.capitalize()
        val copyBaselineTaskName = "copyLintBaseline$variantNameCaps"
        project.tasks.register(copyBaselineTaskName, Copy::class.java) { copy ->
            with(copy) {
                description = "Copies baseline xml to module root for $variantName"
                group = "Pre Lint"
                from(getVariantBaselineFile(project, variantName, lintOptions.baselineFile))
                into(lintOptions.baselineFile.parentFile)
                includeEmptyDirs = false
            }
        }
        val lintTask = project.tasks.first { it.name == "lint$variantNameCaps" }
        lintTask.dependsOn(copyBaselineTaskName)
        lintTask.mustRunAfter(getDeleteVariantBaselineTaskName(variantNameCaps))
    }

    private fun registerGenerateLintBaselineTask(project: Project, variantName: String, lintOptions: LintOptions) {
        val variantNameCaps = variantName.capitalize()
        project.tasks.register("generateLintBaseline$variantNameCaps", Copy::class.java) { copy ->
            with(copy) {
                description = "Generates lint-baseline.xml file per variant and places it under" +
                    "variant directory. This task overwrites the existing baseline file."
                group = "Pre Lint"
                from(lintOptions.baselineFile)
                into(getVariantDir(project, variantName))
            }
        }
    }

    private fun registerDeleteVariantBaselineTask(project: Project, variantName: String, lintOptions: LintOptions) {
        val variantNameCaps = variantName.capitalize()
        project.tasks.register(getDeleteVariantBaselineTaskName(variantNameCaps), Delete::class.java) { delete ->
            with(delete) {
                description = "Deletes this variant baseline file"
                group = "Pre Lint"
                delete(getVariantBaselineFile(project, variantName, lintOptions.baselineFile))
            }
        }
    }

    private fun getDeleteVariantBaselineTaskName(variantNameCaps: String) = "delete${variantNameCaps}Baseline"

    private fun registerDeleteLintBaselineTask(project: Project, lintOptions: LintOptions) {
        project.tasks.register(DELETE_BASELINE_TASK_NAME, Delete::class.java) { delete ->
            with(delete) {
                description =
                    "Deletes the baseline specified in lintOptions. Useful before generating baseline file."
                group = "Pre Lint"
                delete(lintOptions.baselineFile)
            }
        }
    }

    private fun getVariantDir(
        project: Project,
        variantName: String
    ): File = File(project.projectDir, "src/$variantName")

    private fun getVariantBaselineFile(
        project: Project,
        variantName: String,
        baselineFile: File
    ): File = File(getVariantDir(project, variantName), baselineFile.name)
}
