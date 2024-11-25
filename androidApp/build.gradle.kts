import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinx.serialization)
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.spacey.newsbuddy.android"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.spacey.newsbuddy.android"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        val keyStoreFile = project.rootProject.file("secrets.properties")
        val properties = Properties()
        properties.load(FileInputStream(keyStoreFile))
        buildConfigField("String", "GEMINI_API_KEY", properties.getProperty("GEMINI_API_KEY"))
        buildConfigField("String", "NEWS_API_KEY", properties.getProperty("NEWS_API_KEY"))
    }
    buildFeatures {
        buildConfig = true
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(projects.shared)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.material.icons.extended)

    implementation(libs.androidx.navigation.compose)
    implementation(libs.kotlinx.serialization.json)

    implementation(libs.work.manager.ktx)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)

    debugImplementation(libs.compose.ui.tooling)
}