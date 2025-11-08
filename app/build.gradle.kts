plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
    kotlin("plugin.serialization") version "2.1.21"
    id("org.jetbrains.kotlin.kapt")
}

android {
    namespace = "com.example.vibezapp10"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.vibezapp10"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

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
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // AndroidX / Jetpack
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    // Glide
    implementation(libs.glide)
    implementation(libs.firebase.messaging)
    kapt(libs.glide.compiler)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // ExoPlayer
    implementation(libs.exoplayer)

    // Firebase
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.database)

    // Kotlin Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

// Ktor (Android-safe, aligned to 2.3.12)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)


    // Supabase (BOM ensures version alignment)
    implementation(platform(libs.supabase.bom))
    implementation(libs.supabase.gotrue)
    implementation(libs.supabase.postgrest)
    implementation(libs.supabase.storage)
    implementation(libs.supabase.realtime)

// Core library desugaring

        coreLibraryDesugaring ("com.android.tools:desugar_jdk_libs:2.1.5") // Or the latest stable version

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation("com.google.android.gms:play-services-auth:21.4.0")



}
