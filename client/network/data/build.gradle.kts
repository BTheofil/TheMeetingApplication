plugins {
    alias(libs.plugins.meet.android.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "hu.tb.network.data"
}
dependencies {
    implementation(libs.kotlinx.serialization.json)
}
