plugins {
    alias(libs.plugins.android.library.convention.plugin)
}

android {
    namespace = "com.nikealarm.core.model"
}

dependencies {
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.junit)
}