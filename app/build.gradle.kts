plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.android.libraries.mapsplatform.secrets.gradle.plugin)
    id("com.google.gms.google-services") version "4.4.2" apply false
}

android {
    namespace = "com.exoticdg.weatheralerts"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.exoticdg.weatheralerts"
        minSdk = 30
        targetSdk = 35
        versionCode = 1
        versionName = "ALPHA"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
    implementation(libs.androidx.preference)
    implementation(libs.play.services.maps)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.runtime.android)
    implementation(libs.androidx.ui.android)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("com.google.firebase:firebase-analytics")
    implementation(platform("com.google.firebase:firebase-bom:33.9.0"))
    implementation(platform("androidx.compose:compose-bom:2025.02.00"))
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation("androidx.activity:activity-ktx:1.10.1")
    implementation("androidx.fragment:fragment:1.8.6")
    implementation("androidx.fragment:fragment-ktx:1.8.6")
    implementation("androidx.fragment:fragment-compose:1.8.6")
    debugImplementation("androidx.fragment:fragment-testing:1.8.6")



}