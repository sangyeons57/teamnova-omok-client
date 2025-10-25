plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.teamnovaomok"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.teamnovaomok"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "TCP_HOST", "\"bamsol.net\"")
        buildConfigField("int", "TCP_PORT", "15015")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":designsystem"))

    implementation(project(":core-di"))

    implementation(project(":application"))
    implementation(project(":domain"))
    implementation(project(":data"))
    implementation(project(":infra"))

    implementation(project(":feature_auth"))
    implementation(project(":feature_game"))
    implementation(project(":feature_home"))

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.core.splashscreen)

    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Credential Manager
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)

    // Sign in with Google (Google ID SDK)
    implementation(libs.googleid)

    // AuthorizationClient (권한부여/토큰)
    implementation(libs.play.services.auth)
    implementation(libs.androidx.room.runtime)
}
