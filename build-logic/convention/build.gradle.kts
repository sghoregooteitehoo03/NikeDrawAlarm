import org.gradle.kotlin.dsl.`kotlin-dsl`
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "com.nikealarm.build_logic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.room.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("androidApplicationConventionPlugin") {
            id = libs.plugins.android.application.convention.plugin.get().pluginId
            implementationClass = "com.nikealarm.convention.AndroidApplicationConventionPlugin"
        }
        register("androidApplicationComposeConventionPlugin") {
            id = libs.plugins.android.application.compose.convention.plugin.get().pluginId
            implementationClass = "com.nikealarm.convention.AndroidApplicationComposeConventionPlugin"
        }
        register("androidLibraryConventionPlugin") {
            id = libs.plugins.android.library.convention.plugin.get().pluginId
            implementationClass = "com.nikealarm.convention.AndroidLibraryConventionPlugin"
        }
        register("androidLibraryComposeConventionPlugin") {
            id = libs.plugins.android.library.compose.convention.plugin.get().pluginId
            implementationClass = "com.nikealarm.convention.AndroidLibraryComposeConventionPlugin"
        }
        register("featureApiConventionPlugin") {
            id = libs.plugins.feature.api.convention.plugin.get().pluginId
            implementationClass = "com.nikealarm.convention.FeatureApiConventionPlugin"
        }
        register("featureImplConventionPlugin") {
            id = libs.plugins.feature.impl.convention.plugin.get().pluginId
            implementationClass = "com.nikealarm.convention.FeatureImplConventionPlugin"
        }
        register("roomConventionPlugin") {
            id = libs.plugins.room.convention.plugin.get().pluginId
            implementationClass = "com.nikealarm.convention.RoomConventionPlugin"
        }
        register("hiltConventionPlugin") {
            id = libs.plugins.hilt.convention.plugin.get().pluginId
            implementationClass = "com.nikealarm.convention.HiltConventionPlugin"
        }
    }
}