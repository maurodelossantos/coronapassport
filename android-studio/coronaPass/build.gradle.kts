buildscript {
    val kotlin_version by extra("1.4.32")
    repositories {
        gradlePluginPortal()
        jcenter()
        maven {
            url = uri("https://jitpack.io")
            url = uri("https://plugins.gradle.org/m2/")
        }
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.10")
        classpath("com.android.tools.build:gradle:4.1.3")
        //classpath("com.github.kenglxn.QRGen:android:2.6.0")
        classpath("com.google.zxing:core:3.4.0")
        classpath("com.journeyapps:zxing-android-embedded:3.6.0")
        classpath(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
        classpath("androidx.appcompat:appcompat:1.2.0")
        classpath("junit:junit:4.12")
        classpath("androidx.test.ext:junit:1.1.2")
        classpath("androidx.test.espresso:espresso-core:3.3.0")
        //classpath("org.web3j:core:4.1.0-android")
        //classpath("org.web3j:web3j-gradle-plugin:4.8.4")
        //classpath("org.web3j:core:4.6.0")
        classpath("org.ethereum:geth:1.5.2")
        classpath("org.web3j:web3j-gradle-plugin:4.8.4")
        classpath("io.reactivex.rxjava2:rxjava:2.2.2")
        classpath("com.android.support:design:27.0.2")
        classpath("com.google.gms:google-services:4.3.5")
        classpath("org.jetbrains.kotlin:kotlin-android-extensions:$kotlin_version")
    }
}

group = "es.udc.tfg.delossantos.coronapass"
version = "1.0-SNAPSHOT"
apply(plugin = "org.web3j")
apply(plugin = "com.google.gms.google-services")
apply(plugin = "kotlin-android-extensions")

repositories {
    mavenCentral()
    jcenter()
    google()
    maven { url = uri("https://jitpack.io")
            url = uri("https://plugins.gradle.org/m2/")}
}

