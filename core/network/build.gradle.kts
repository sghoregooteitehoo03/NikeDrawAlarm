plugins {
    alias(libs.plugins.android.library.convention.plugin)
    alias(libs.plugins.hilt.convention.plugin)
}

android { namespace = "com.nikealarm.core.network" }

dependencies {
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)

    testImplementation(libs.junit)
}