plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
    kotlin("android")
    id("com.google.gms.google-services")
}
group = "es.udc.tfg.delossantos.coronapass"
version = "1.0-SNAPSHOT"

repositories {
    gradlePluginPortal()
    google()
    jcenter()
    maven { url = uri("https://jitpack.io") }
    mavenCentral()
}
dependencies {
    implementation(project(":shared"))
    implementation("com.google.android.material:material:1.2.0")
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("androidx.constraintlayout:constraintlayout:1.1.3")
    implementation("com.github.kenglxn.QRGen:android:2.6.0")
    implementation("com.google.zxing:core:3.4.0")
    implementation("com.journeyapps:zxing-android-embedded:3.6.0")
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("androidx.appcompat:appcompat:1.2.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${rootProject.extra["kotlin_version"]}")
    implementation("androidx.navigation:navigation-fragment:2.2.2")
    implementation("androidx.navigation:navigation-ui:2.2.2")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.2.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.2.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.2.2")
    implementation("androidx.navigation:navigation-ui-ktx:2.2.2")
    implementation("com.google.firebase:firebase-database:19.2.1")
    implementation("com.google.firebase:firebase-auth-ktx:20.0.4")
    implementation("androidx.security:security-crypto:1.0.0-rc01")
    testImplementation("junit:junit:4.12")
    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
    //implementation("org.web3j:core:4.1.0-android")
    implementation("io.reactivex.rxjava2:rxjava:2.2.2")
    implementation("org.web3j:abi:4.8.4")
    implementation("org.web3j:crypto:4.8.4")
    implementation("org.web3j:core:4.8.4")
    implementation("com.google.android.material:material:1.3.0")
    implementation("com.google.gms:google-services:4.3.5")
    implementation(platform("com.google.firebase:firebase-bom:27.1.0"))
    implementation("com.google.firebase:firebase-analytics-ktx")
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("androidx.security:security-crypto:1.0.0")
    // For Identity Credential APIs
    implementation("androidx.security:security-identity-credential:1.0.0-alpha02")
    implementation("androidx.appcompat:appcompat:1.2.0")
}
android {
    compileSdkVersion(29)
    defaultConfig {
        applicationId = "es.udc.tfg.delossantos.coronapass.androidApp"
        minSdkVersion(24)
        targetSdkVersion(29)
        versionCode = 1
        versionName = "1.0"
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
