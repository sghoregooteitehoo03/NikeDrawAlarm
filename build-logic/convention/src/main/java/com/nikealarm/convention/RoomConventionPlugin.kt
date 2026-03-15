package com.nikealarm.convention

import androidx.room.gradle.RoomExtension
import com.nikealarm.convention.extension.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class RoomConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("androidx.room")
            pluginManager.apply("com.google.devtools.ksp")

            extensions.configure<RoomExtension> {
                schemaDirectory("$projectDir/schemas")
            }

            dependencies {
                "implementation"(libs.findLibrary("androidx.room.ktx").get())
                "implementation"(libs.findLibrary("androidx.room.runtime").get())
                "ksp"(libs.findLibrary("androidx.room.compiler").get())
            }
        }
    }
}