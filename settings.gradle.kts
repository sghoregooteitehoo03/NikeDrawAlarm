pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "NikeDrawAlarm"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":app")

include(":core:domain")
include(":core:common")
include(":core:model")
