plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id ("org.jetbrains.kotlin.plugin.compose") version libs.versions.kotlin
}

android {
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.togitech.togii"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.compileSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
    buildFeatures {
        compose = true
    }

    namespace = "com.togitech.togii"
}

dependencies {
    implementation(platform(libs.compose.bom))
    implementation(libs.accompanist.systemuicontroller)
    implementation(libs.androidx.core)
    implementation(libs.androidx.lifecycle.runtime)
    implementation(libs.compose.activity)
    implementation(libs.compose.material)
    implementation(libs.compose.tooling)
    implementation(libs.compose.ui)
    implementation(libs.androidx.material3.android)
    implementation(projects.ccp)
    testImplementation(libs.junit)
}
