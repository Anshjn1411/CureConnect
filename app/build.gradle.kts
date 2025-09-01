plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    alias(libs.plugins.google.gms.google.services)
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.0"

}
android {
    namespace = "com.project.cureconnect"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.project.cureconnect"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    // ✅ Add this block to split APK by architecture
    splits {
        abi {
            isEnable = true
            reset()
            include("armeabi-v7a", "arm64-v8a") // ✅ Keep only these
            isUniversalApk = false                // ⛔ Avoid large fat APK
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
        compose = true
    }

    configurations.all {
        resolutionStrategy {
            force("jakarta.activation:jakarta.activation-api:1.2.1")
            exclude(group = "jakarta.activation", module = "jakarta.activation-api")
            exclude(group = "javax.activation", module = "activation")
            // force("com.google.api.grpc:proto-google-common-protos:2.3.2") // Uncomment if needed
        }
    }

    packaging {
        resources {
            excludes += setOf(
                "META-INF/LICENSE.md",
                "META-INF/LICENSE-notice.md",
                "META-INF/DEPENDENCIES",
                "META-INF/NOTICE.md",
                "META-INF/NOTICE.txt",
                "META-INF/LICENSE.txt",
                "META-INF/services/javax.activation.DataContentHandler"
            )
        }
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.6.10"
    }
}


dependencies {

    implementation("com.google.accompanist:accompanist-flowlayout:0.32.0")
    implementation("androidx.core:core-splashscreen:1.0.1")

    implementation("androidx.appcompat:appcompat:1.6.1")
    // Jetpack‑Core SplashScreen latest pre‑release :contentReference[oaicite:1]{index=1}
    implementation("org.jitsi.react:jitsi-meet-sdk:10.1.2") {
        isTransitive = true
    }           // Latest available version on Maven :contentReference[oaicite:2]{index=2}
    implementation("androidx.datastore:datastore-preferences:1.1.4")   // Stable version (1.1.5 had issues; 1.2 is alpha) :contentReference[oaicite:3]{index=3}
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.0") // Upgraded to latest stable 1.9.0 :contentReference[oaicite:4]{index=4}

    implementation("com.razorpay:checkout:1.6.40")                    // Latest Razorpay v1.6.40 with auto‑update enabled :contentReference[oaicite:5]{index=5}
    implementation("com.itextpdf:itextpdf:5.5.13.4")                   // Latest maintenance release in the 5.x series :contentReference[oaicite:6]{index=6}
    implementation("com.twilio.sdk:twilio:10.9.1")                    // Twilio Java Helper Library at latest 10.9.1 :contentReference[oaicite:7]{index=7}
    implementation("com.google.android.gms:play-services-auth:20.6.0")// Auth SDK latest stable on Maven (20.6.0) :contentReference[oaicite:8]{index=8}

    implementation("androidx.compose.material:material-icons-extended:1.5.0‑rc01") // Jetpack Compose Material Icons extended is at 1.5.0‑rc01 :contentReference[oaicite:9]{index=9}

    implementation("com.google.accompanist:accompanist-permissions:0.32.0") // Still current
    implementation("com.guolindev.permissionx:permissionx:1.8.0")         // Still current

    implementation("com.sun.mail:android-mail:1.6.7")
    implementation("com.sun.mail:android-activation:1.6.7")

    implementation("com.google.firebase:firebase-auth-ktx:22.1.2")
    implementation(libs.firebase.firestore.ktx)

    implementation("androidx.compose.runtime:runtime-livedata:1.6.1")
    implementation("androidx.navigation:navigation-compose:2.8.5")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")

    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation("io.coil-kt:coil-gif:2.3.0")
    implementation("com.github.bumptech.glide:glide:4.15.1")
    implementation("com.github.bumptech.glide:compose:1.0.0-alpha.3")

    implementation("com.cloudinary:cloudinary-android:2.3.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")
    implementation("com.google.ai.client.generativeai:generativeai:0.2.0")
    implementation("androidx.exifinterface:exifinterface:1.3.7")



    // AndroidX Core Libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.databinding.compiler.common)


    implementation(libs.volley)
    implementation(libs.androidx.espresso.core)
    implementation(libs.play.services.location)
    implementation(libs.ui.tooling.preview)

    // Testing Dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    debugImplementation(libs.ui.tooling)
}
