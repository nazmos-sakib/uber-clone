pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        repositories {

            maven { url = uri("https://jitpack.io") }
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

rootProject.name = "uber-clone"
include(":app")
 