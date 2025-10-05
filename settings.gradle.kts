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
include(":feature_auth")
include(":domain")
include(":data")
include(":core-api")
include(":feature_home")
include(":designsystem")
include(":application")
include(":infra")
include(":core-di")
include(":ui-widgets")
include(":feature_game")
