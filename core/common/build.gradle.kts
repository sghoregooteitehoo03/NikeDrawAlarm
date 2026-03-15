plugins {
    alias(libs.plugins.android.library.convention.plugin)
}

android {
    namespace = "com.nikealarm.core.common"
}

dependencies {
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    testImplementation(libs.junit)
}