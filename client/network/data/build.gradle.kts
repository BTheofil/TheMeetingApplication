plugins {
    alias(libs.plugins.meet.android.library)
}

android {
    namespace = "hu.tb.network.data"
}
dependencies {
    implementation(libs.kotlinx.serialization.json)
}
