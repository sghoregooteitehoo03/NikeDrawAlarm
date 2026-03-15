package com.nikealarm.convention

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

class FeatureApiConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("androidLibraryConventionPlugin")
            apply(plugin = "org.jetbrains.kotlin.plugin.serialization")

            dependencies {
//                "api"(project(":core:navigation"))
            }
        }
    }
}