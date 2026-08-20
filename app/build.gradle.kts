import com.google.gms.googleservices.GoogleServicesPlugin.MissingGoogleServicesStrategy

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.devtools.ksp)
    alias(libs.plugins.roborazzi)
    alias(libs.plugins.secrets)
    alias(libs.plugins.google.services)
}

android {
    namespace = "com.example"

    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.aistudio.kxa.wvtr"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            val keystorePath =
                System.getenv("KEYSTORE_PATH")
                    ?: "${rootDir}/my-upload-key.jks"

            storeFile = file(keystorePath)
            storePassword = System.getenv("STORE_PASSWORD")
            keyAlias = "upload"
            keyPassword = System.getenv("KEY_PASSWORD")
        }

        create("debugConfig") {
            storeFile = file("${rootDir}/debug.keystore")
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
    }

    buildTypes {
        release {
            isCrunchPngs = false
            isMinifyEnabled = false

            proguardFiles(
                getDefaultProguardFile(
                    "proguard-android-optimize.txt"
                ),
                "proguard-rules.pro"
            )

            signingConfig = signingConfigs.getByName("release")
        }

        debug {
            signingConfig = signingConfigs.getByName("debugConfig")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }

    dependenciesInfo {
        includeInApk = false
        includeInBundle = true
    }
}

/*
 * Google/Maps Secrets Gradle Plugin
 */
secrets {
    propertiesFileName = ".env"
    defaultPropertiesFileName = ".env.example"
    ignoreList.add("FIREBASE_APPCHECK_DEBUG_TOKEN")
}

/*
 * Google Services
 */
googleServices {
    missingGoogleServicesStrategy =
        MissingGoogleServicesStrategy.WARN
}


/*
 * =========================================================
 * DEPENDENCIES
 * =========================================================
 */

dependencies {

    // -----------------------------------------------------
    // Jetpack Compose
    // -----------------------------------------------------

    implementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.activity.compose)

    implementation(libs.androidx.compose.material.icons.core)
    implementation(libs.androidx.compose.material.icons.extended)

    implementation(libs.androidx.compose.material3)

    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)


    // -----------------------------------------------------
    // Android Core
    // -----------------------------------------------------

    implementation(libs.androidx.core.ktx)


    // -----------------------------------------------------
    // Lifecycle / ViewModel
    // -----------------------------------------------------

    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)


    // -----------------------------------------------------
    // Navigation
    // -----------------------------------------------------

    implementation(libs.androidx.navigation.compose)


    // -----------------------------------------------------
    // Room
    // -----------------------------------------------------

    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.runtime)

    ksp(libs.androidx.room.compiler)


    // -----------------------------------------------------
    // Kotlin Coroutines
    // -----------------------------------------------------

    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)


    // -----------------------------------------------------
    // Kotlin Serialization
    // -----------------------------------------------------

    // Serialization plugin is enabled above.
    // Supabase uses KotlinX Serialization by default.


    // -----------------------------------------------------
    // Image Loading
    // -----------------------------------------------------

    implementation(libs.coil.compose)


    // -----------------------------------------------------
    // Retrofit / Networking
    // -----------------------------------------------------

    implementation(libs.retrofit)
    implementation(libs.converter.moshi)

    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)


    // -----------------------------------------------------
    // Moshi
    // -----------------------------------------------------

    implementation(libs.moshi.kotlin)

    ksp(libs.moshi.kotlin.codegen)


    // -----------------------------------------------------
    // DataStore
    // -----------------------------------------------------

    implementation(libs.androidx.datastore.preferences)


    // =====================================================
    // SUPABASE
    // =====================================================

    // Supabase BOM controls compatible Supabase module versions.
    implementation(platform(libs.supabase.bom))

    // Authentication
    implementation(libs.supabase.auth)

    // PostgreSQL / Data API
    implementation(libs.supabase.postgrest)

    // Realtime chat / room synchronization
    implementation(libs.supabase.realtime)

    // Profile photos / chat media
    implementation(libs.supabase.storage)


    // =====================================================
    // KTOR
    // =====================================================

    // Android HTTP engine required by Supabase
    implementation(libs.ktor.client.android)

    implementation(libs.ktor.client.core)

    implementation(libs.ktor.client.content.negotiation)


    // -----------------------------------------------------
    // Firebase
    // -----------------------------------------------------

    implementation(platform(libs.firebase.bom))

    implementation(libs.firebase.ai)

    implementation(libs.firebase.appcheck.recaptcha)


    // -----------------------------------------------------
    // Google Credential Manager
    // -----------------------------------------------------

    // Keep disabled for now.
    // We'll enable Google login after basic Supabase
    // email/password authentication works.

    // implementation(libs.firebase.auth)
    // implementation(libs.androidx.credentials)
    // implementation(libs.androidx.credentials.play.services)
    // implementation(libs.googleid)


    // -----------------------------------------------------
    // Tests
    // -----------------------------------------------------

    testImplementation(libs.androidx.compose.ui.test.junit4)
    testImplementation(libs.androidx.core)
    testImplementation(libs.androidx.junit)
    testImplementation(libs.junit)

    testImplementation(libs.kotlinx.coroutines.test)

    testImplementation(libs.robolectric)
    testImplementation(libs.roborazzi)
    testImplementation(libs.roborazzi.compose)
    testImplementation(libs.roborazzi.junit.rule)


    // -----------------------------------------------------
    // Android Tests
    // -----------------------------------------------------

    androidTestImplementation(
        platform(libs.androidx.compose.bom)
    )

    androidTestImplementation(
        libs.androidx.compose.ui.test.junit4
    )

    androidTestImplementation(
        libs.androidx.espresso.core
    )

    androidTestImplementation(
        libs.androidx.junit
    )

    androidTestImplementation(
        libs.androidx.runner
    )


    // -----------------------------------------------------
    // Debug
    // -----------------------------------------------------

    debugImplementation(
        libs.androidx.compose.ui.test.manifest
    )

    debugImplementation(
        libs.androidx.compose.ui.tooling
    )
}