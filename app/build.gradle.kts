plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("plugin.serialization") version "2.1.20"
    id("kotlin-kapt")
    id("com.google.gms.google-services")
    id("com.google.devtools.ksp") version "2.1.20-2.0.1"
}

android {
    namespace = "com.example.financeapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.financeapp"
        minSdk = 26
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "META-INF/DEPENDENCIES"
            excludes += "META-INF/INDEX.LIST"
            excludes += "META-INF/LICENSE.md"
            excludes += "META-INF/LICENSE.txt"
            excludes += "META-INF/NOTICE"
            excludes += "META-INF/NOTICE.txt"
            excludes += "META-INF/LICENSE-notice.md"
            excludes += "META-INF/native-image/**"
        }
    }
}

dependencies {

    // Kotlin Standard Library
    implementation(kotlin("stdlib-jdk8"))

    // Android Jetpack - Core & Lifecycle
    implementation(libs.androidx.core.ktx) // Core Kotlin extensions
    implementation(libs.androidx.lifecycle.runtime.ktx) // Lifecycle runtime Kotlin extensions
    implementation(libs.androidx.activity.compose) // Activity Compose integration

    // Android Jetpack - Jetpack Compose UI
    implementation(platform(libs.androidx.compose.bom)) // Compose Bill of Materials (BOM) for version alignment
    implementation(libs.androidx.ui) // Compose UI core
    implementation(libs.androidx.ui.graphics) // Compose UI graphics
    implementation(libs.androidx.ui.tooling.preview) // Compose UI tooling for previews
    implementation(libs.androidx.material3) // Material Design 3 components for Compose
    implementation(libs.androidx.material.icons.core) // Core Material Design icons
    implementation(libs.androidx.material.icons.extended) // Extended Material Design icons
    implementation("androidx.compose.runtime:runtime-livedata:1.7.8") // Compose runtime LiveData integration

    // Android Jetpack - Navigation
    val nav_version = "2.8.5"
    implementation("androidx.navigation:navigation-compose:$nav_version") // Navigation for Jetpack Compose
    implementation("androidx.navigation:navigation-fragment:$nav_version") // Navigation for Fragments
    implementation("androidx.navigation:navigation-ui:$nav_version") // Navigation UI components
    implementation("androidx.navigation:navigation-dynamic-features-fragment:$nav_version") // Navigation for dynamic feature modules with Fragments

    // Android Jetpack - Room Persistence Library
    val room_version = "2.7.0-alpha11"
    implementation("androidx.room:room-runtime:$room_version") // Room runtime
    implementation("androidx.room:room-ktx:$room_version") // Room Kotlin extensions (Coroutines support)
    ksp("androidx.room:room-compiler:$room_version") // Room annotation processor (using KSP)

    // Firebase - Bill of Materials (BOM) and Services
    implementation(platform("com.google.firebase:firebase-bom:33.13.0")) // Firebase Bill of Materials for version alignment
    implementation("com.google.firebase:firebase-auth-ktx") // Firebase Authentication with Kotlin extensions
    implementation("com.google.firebase:firebase-analytics") // Firebase Analytics
    implementation("com.google.android.gms:play-services-auth:21.3.0") // Google Sign-In (used with Firebase Auth)

    // Networking - Retrofit for API calls
    implementation("com.squareup.retrofit2:retrofit:2.9.0") // Retrofit HTTP client
    implementation("com.squareup.retrofit2:converter-gson:2.9.0") // Gson converter for Retrofit (JSON parsing)
    implementation("com.jakewharton.retrofit:retrofit2-kotlin-coroutines-adapter:0.9.2") // Retrofit adapter for Kotlin Coroutines

    // JSON Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3") // Kotlinx Serialization for JSON

    // Charting Libraries
    implementation(libs.vico.compose) // Vico charting library for Compose
    implementation(libs.vico.compose.m2) // Vico Material 2 components for Compose
    implementation(libs.vico.compose.m3) // Vico Material 3 components for Compose
    implementation(libs.vico.views) // Vico charting library for Views (if used)
    implementation("io.github.dautovicharis:charts-android:2.0.0") // Another charts library
    implementation("co.yml:ycharts:2.1.0") // Another charts library (YCharts)

    // Google Cloud Services
    implementation ("com.google.cloud:google-cloud-aiplatform:3.63.0") // Google Cloud AI Platform (Vertex AI)

    // Java Desugaring for older Android versions
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.1.5")

    // Miscellaneous
    implementation(libs.core) // A general core library

    // --- Testing Dependencies ---

    // Unit Testing
    testImplementation(libs.junit) // JUnit for unit tests

    // Android Instrumentation Testing - Core
    androidTestImplementation(libs.androidx.junit) // AndroidX Test Library for JUnit extensions
    androidTestImplementation(libs.androidx.espresso.core) // Espresso for UI testing

    // Android Instrumentation Testing - Compose
    androidTestImplementation(platform(libs.androidx.compose.bom)) // Compose BOM for testing version alignment
    androidTestImplementation(libs.androidx.ui.test.junit4) // Compose UI testing with JUnit4

    // Android Instrumentation Testing - Navigation
    androidTestImplementation("androidx.navigation:navigation-testing:$nav_version") // Navigation testing utilities

    // Debugging - Compose UI Tooling
    debugImplementation(libs.androidx.ui.tooling) // Compose UI tooling for layout inspection, etc.
    debugImplementation(libs.androidx.ui.test.manifest) // Compose UI test manifest utilities

    //implementation(libs.protolite.well.known.types)
    //annotationProcessor("androidx.room:room-compiler:$room_version") // Old way for Room, KSP is preferred
    //kapt("androidx.room:room-compiler:$room_version") // Old way for Room with Kapt, KSP is preferred
}