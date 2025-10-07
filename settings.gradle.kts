pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "TeamnovaOmok"
include(":app")
include(":core-api")
include(":core-di")
include(":application")
include(":domain")
include(":data")
include(":infra")
include(":feature_auth")
include(":feature_home")
include(":feature_game")
include(":designsystem")
