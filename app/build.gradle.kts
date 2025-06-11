import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.protobuf)
}

android {
    namespace = "info.zverev.ilya.rinewsreader"
    compileSdk = 35

    defaultConfig {
        applicationId = "info.zverev.ilya.rinewsreader"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val keystoreFile = project.rootProject.file("secrets.properties")
        val secrets = Properties()
        secrets.load(keystoreFile.inputStream())

        buildConfigField("String", "NEWSAPI_KEY", secrets.getProperty("NEWSAPI_KEY"))
        buildConfigField("String", "GRPC_SERVER", secrets.getProperty("GRPC_SERVER"))
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
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }

    protobuf {
        protoc {
            artifact = libs.protoc.asProvider().get().toString()
        }
        plugins {
            create("grpc") {
                artifact = libs.protoc.gen.grpc.java.get().toString()
            }
            create("grpckt") {
                artifact = libs.protoc.gen.grpc.kotlin.get().toString() + ":jdk8@jar"
            }
        }
        generateProtoTasks {
            all().forEach {
                it.plugins {
                    create("java")
                    create("grpc")
                    create("grpckt")
                }
                it.builtins {
                    create("kotlin")
                }
            }
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.webkit)
    implementation(libs.human.readable)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)

    implementation(libs.grpc.kotlin.stub)
    implementation(libs.grpc.protobuf)
    implementation(libs.protobuf.kotlin)
    implementation(libs.protoc)
    implementation(libs.protoc.gen.grpc.kotlin)
    implementation(libs.protoc.gen.grpc.java)
    runtimeOnly(libs.grpc.okhttp)
    runtimeOnly(libs.grpc.api)
}