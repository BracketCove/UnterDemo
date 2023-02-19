import org.jetbrains.kotlin.scripting.definitions.StandardScriptDefinition.platform

plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

kotlin {
    android()
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
                implementation ("dev.gitlive:firebase-auth:1.6.2")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            dependencies {
                val stream_version = "5.12.0"
                implementation("io.getstream:stream-chat-android-client:$stream_version")

                implementation("com.google.firebase:firebase-auth-ktx:21.1.0")
                implementation("com.google.firebase:firebase-storage-ktx:20.1.0")
                implementation("com.github.bumptech.glide:glide:4.14.2")

            }
        }
        val androidTest by getting
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }
    }
}

android {
    namespace = "com.bracketcove"
    compileSdk = 33
    defaultConfig {
        minSdk = 26
        targetSdk = 33
    }
}