plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.gms.google.services)


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
        compose = true
    }
    configurations.all {
        resolutionStrategy {
            force("jakarta.activation:jakarta.activation-api:1.2.1")
            exclude (group ="jakarta.activation", module= "jakarta.activation-api")
            exclude (group= "javax.activation", module= "activation")

                //force ("com.google.api.grpc:proto-google-common-protos:2.3.2'")

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
}

dependencies {

    implementation ("com.razorpay:checkout:1.6.40")


        //implementation ("com.google.cloud:google-cloud-translate:2.0.0")



    implementation ("com.itextpdf:itextpdf:5.5.13.3")
    //implementation ("com.github.barteksc:android-pdf-viewer:2.8.2")


    implementation("com.twilio.sdk:twilio:9.1.1")

    implementation ("com.google.android.gms:play-services-auth:20.7.0")
    implementation(libs.play.services.nearby)
    // Material Icons
    implementation ("androidx.compose.material:material-icons-extended:1.5.4")
    //Permissions handling
            implementation ("com.google.accompanist:accompanist-permissions:0.32.0")
    implementation("com.sun.mail:android-mail:1.6.7") {
        exclude (group= "javax.activation", module= "activation")
    }
    implementation("com.sun.mail:android-activation:1.6.7")



    implementation("com.github.ZEGOCLOUD:zego_uikit_prebuilt_call_android:+")
    implementation("com.guolindev.permissionx:permissionx:1.8.0")
    // Firebase
    implementation("com.google.firebase:firebase-auth-ktx:22.1.2")
    implementation(libs.firebase.firestore.ktx)

    // Jetpack Compose Essentials
    implementation("androidx.compose.runtime:runtime-livedata:1.6.1")
    implementation("androidx.navigation:navigation-compose:2.8.5") // Latest Navigation Compose
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")


    // Networking (Retrofit & OkHttp)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.10.0") // Latest OkHttp
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")

    // Image Loading (Coil & Glide)
    implementation("io.coil-kt:coil-compose:2.6.0") // Latest Coil Compose
    implementation("io.coil-kt:coil-gif:2.3.0")
    implementation("com.github.bumptech.glide:glide:4.15.1")
    implementation("com.github.bumptech.glide:compose:1.0.0-alpha.3")

    // Cloudinary (Image Uploading)
    implementation("com.cloudinary:cloudinary-android:2.3.0")

    // Kotlin Coroutines (Asynchronous Programming)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")

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
    implementation(libs.firebase.firestore)
    implementation(libs.volley)
    implementation(libs.androidx.espresso.core)

    // Testing Dependencies
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
