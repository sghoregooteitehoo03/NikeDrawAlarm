plugins {
    alias(libs.plugins.android.library.convention.plugin)
    alias(libs.plugins.hilt.convention.plugin)
}

android {
    namespace = "com.nikealarm.core.datastore"
}

dependencies {
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.androidx.datastore.preferences.core)
}
