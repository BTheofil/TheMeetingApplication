pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS

    repositories {
        mavenCentral()
    }

    versionCatalogs {
        // Remote catalog. Keep this version in sync with `ktor` in gradle/libs.versions.toml.
        create("ktorLibs") {
            from("io.ktor:ktor-version-catalog:3.4.2")
        }
    }
}

rootProject.name = "MeetingServer"
