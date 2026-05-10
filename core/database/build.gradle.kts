plugins {
    alias(libs.plugins.android.library.convention.plugin)
    alias(libs.plugins.hilt.convention.plugin)
    alias(libs.plugins.room.convention.plugin)
}

android {
    namespace = "com.nikedrawalarm.core.database"
}

dependencies {
    api(project(":core:model"))

    implementation(libs.androidx.room.paging)
}
