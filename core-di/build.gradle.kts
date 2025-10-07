plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.example.core_di"
    compileSdk = 36

    defaultConfig {
        minSdk = 29

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    api(project(":core-api"))
    implementation(project(":infra"))
    implementation(project(":application"))
    implementation(project(":data"))

    implementation(libs.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    testImplementation(libs.orgJson)
    testImplementation(libs.archCoreTesting)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}
