package com.ns.convention.internal

import com.android.build.api.dsl.CommonExtension
import com.ns.convention.extension.libs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import java.lang.module.ModuleFinder.compose

internal fun Project.configureAndroidCompose(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    commonExtension.apply {
        buildFeatures {
            compose = true
        }

        dependencies {
            val bom = platform(libs.findLibrary("androidx.compose.bom").get())
            "androidTestImplementation"(bom)

            "debugImplementation"(libs.findLibrary("androidx.compose.ui.tooling").get())
            "debugImplementation"(libs.findLibrary("androidx.compose.ui.test.manifest").get())

            "implementation"(bom)
            "implementation"(libs.findLibrary("androidx.compose.ui").get())
            "implementation"(libs.findLibrary("androidx.compose.ui.tooling.preview").get())
            "implementation"(libs.findLibrary("androidx.compose.material3").get())
            "implementation"(libs.findLibrary("androidx.constraintlayout.compose").get())
        }
    }
}
