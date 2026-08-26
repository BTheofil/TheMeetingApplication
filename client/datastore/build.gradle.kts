plugins {
    alias(libs.plugins.meet.android.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "hu.tb.datastore"
}

dependencies {
    implementation(libs.datastore)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.koin)
}