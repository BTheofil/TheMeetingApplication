plugins {
    alias(libs.plugins.meet.jvm.library)
}

dependencies {
    api(libs.compose.runtime)
    implementation(libs.kotlinx.datetime)
}