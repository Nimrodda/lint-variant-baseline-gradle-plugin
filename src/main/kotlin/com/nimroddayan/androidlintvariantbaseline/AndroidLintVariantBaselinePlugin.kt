/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package com.nimroddayan.androidlintvariantbaseline

import org.gradle.api.Project
import org.gradle.api.Plugin

/**
 * A simple 'hello world' plugin.
 */
class AndroidLintVariantBaselinePlugin: Plugin<Project> {
    override fun apply(project: Project) {
        // Register a task
        project.tasks.register("greeting") { task ->
            task.doLast {
                println("Hello from plugin 'com.nimroddayan.androidlintvariantbaseline.greeting'")
            }
        }
    }
}