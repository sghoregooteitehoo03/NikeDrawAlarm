plugins {
    alias(libs.plugins.android.library.convention.plugin)
    alias(libs.plugins.hilt.convention.plugin)
}

android { namespace = "com.nikealarm.core.domain" }

dependencies {
    api(project(":core:model"))
    api(project(":core:common"))
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(libs.androidx.paging.common.ktx)

    testImplementation(libs.junit)
}