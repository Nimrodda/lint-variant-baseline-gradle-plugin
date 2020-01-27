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

import java.io.File
import org.gradle.testkit.runner.GradleRunner
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@Suppress("FunctionName")
class AndroidLintVariantBaselinePluginFunctionalTest {
    @Test
    fun `deleteLintBaseline task deletes baseline successfully`() {
        val projectDir = File("build/functionalTest")
        projectDir.mkdirs()
        val baselineFile = projectDir.resolve("lint-baseline.xml")
        baselineFile.writeText("")
        projectDir.resolve("settings.gradle").writeText("""
            pluginManagement {
                repositories {
                    google()
                }
            }
        """.trimIndent())
        projectDir.resolve("build.gradle").writeText("""
            plugins {
                id 'com.android.library' version '3.5.3'            
                id 'com.nimroddayan.lint-variant-baseline' version '0.1.0'
            }
            android {
                compileSdkVersion 29
                lintOptions {
                    baseline file("lint-baseline.xml")
                }
            }
        """)

        GradleRunner.create().apply {
            forwardOutput()
            withPluginClasspath()
            withArguments("deleteLintBaseline")
            withProjectDir(projectDir)
            build()
        }

        assertFalse(baselineFile.exists())

        projectDir.deleteRecursively()
    }

    @Test
    fun `generate baseline per variant successfully`() {
        val projectDir = File("build/functionalTest")
        projectDir.mkdirs()
        val mainSrcDir = File(projectDir, "src/main")
        mainSrcDir.mkdirs()
        mainSrcDir.resolve("AndroidManifest.xml").writeText("""
            <manifest package="com.nimroddayan.lintvariantbaseline" />
        """.trimIndent())
        projectDir.resolve("settings.gradle").writeText("""
            pluginManagement {
                repositories {
                    google()
                }
            }
        """.trimIndent())
        projectDir.resolve("build.gradle").writeText("""
            plugins {
                id 'com.android.library' version '3.5.3'            
                id 'com.nimroddayan.lint-variant-baseline' version '0.1.0'
            }
            repositories {
                google()
                jcenter()
            }
            android {
                compileSdkVersion 29
                lintOptions {
                    baseline file("lint-baseline.xml")
                }
                android.flavorDimensions "type", "store"
                android.productFlavors {
                    free {
                        dimension "type"
                    }
                    paid {
                        dimension "type"
                    }
                    playstore {
                        dimension "store"
                    }
                    amazon {
                        dimension "store"
                    }
                }
            }
        """.trimIndent())

        GradleRunner.create().apply {
            forwardOutput()
            withPluginClasspath()
            withArguments("generateLintBaselineFreePlaystoreRelease", "-Dlint.baselines.continue=true")
            withProjectDir(projectDir)
            build()
        }

        val variantBaseline = File(projectDir, "src/freePlaystoreRelease").resolve("lint-baseline.xml")
        assertTrue(variantBaseline.exists())

        projectDir.deleteRecursively()
    }

    @Test
    fun `copy variant baseline to lintOptions baseline path successfully`() {
        val projectDir = File("build/functionalTest")
        projectDir.mkdirs()
        projectDir.resolve("settings.gradle").writeText("""
            pluginManagement {
                repositories {
                    google()
                }
            }
        """.trimIndent())
        projectDir.resolve("build.gradle").writeText("""
            plugins {
                id 'com.android.library' version '3.5.3'            
                id 'com.nimroddayan.lint-variant-baseline' version '0.1.0'
            }
            android {
                compileSdkVersion 29
                lintOptions {
                    baseline file("lint-baseline.xml")
                }
                android.flavorDimensions "type", "store"
                android.productFlavors {
                    free {
                        dimension "type"
                    }
                    paid {
                        dimension "type"
                    }
                    playstore {
                        dimension "store"
                    }
                    amazon {
                        dimension "store"
                    }
                }
            }
        """.trimIndent())

        val variantSrcRoot = File(projectDir, "src/freePlaystoreRelease")
        variantSrcRoot.mkdirs()
        val variantBaseline = variantSrcRoot.resolve("lint-baseline.xml")
        variantBaseline.writeText("")

        GradleRunner.create().apply {
            forwardOutput()
            withPluginClasspath()
            withArguments("copyLintBaselineFreePlaystoreRelease")
            withProjectDir(projectDir)
            build()
        }


        assertTrue(projectDir.resolve("lint-baseline.xml").exists())

        projectDir.deleteRecursively()
    }
}
