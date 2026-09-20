plugins {
    alias(libs.plugins.meet.android.application)
    alias(libs.plugins.meet.android.application.compose)
    alias(libs.plugins.google.services)
}

android {
    namespace = "hu.tb.meeting"

    defaultConfig {
        applicationId = "hu.tb.meeting"
        versionCode = 1
        versionName = "1.0"

        buildConfigField("String", "REVENUECAT_API_KEY", "\"goog_woFqVWSmuUGLXSybzVtkIlmqJHy\"")
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            optimization {
                enable = true
            }

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
}

dependencies {
    implementation(projects.navigator)
    implementation(projects.designSystem)
    implementation(projects.auth.presentation)
    implementation(projects.profile.presentation)
    implementation(projects.profile.data)
    implementation(projects.network)
    implementation(projects.datastore)
    implementation(projects.dashboard.presentation)
    implementation(projects.search.presentation)
    implementation(projects.notification.presentation)
    implementation(projects.notification.data)
    implementation(projects.schedule.presentation)

    implementation(libs.koin)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.core.splashscreen)
}
