plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.sqldelight)
    alias(libs.plugins.buildkonfig)
}

kotlin {
    androidLibrary {
        namespace = "com.snehonir.niyeesho"
        compileSdk = libs.versions.compileSdk.get().toInt()
        minSdk = libs.versions.minSdk.get().toInt()
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64(),
        iosX64(),
    ).forEach { target ->
        target.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlin.stdlib)
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.viewmodel.compose)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.collections.immutable)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.logging)
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.navigation.compose)
            implementation(libs.sqldelight.runtime)
            implementation(libs.sqldelight.coroutines.extensions)
            implementation(libs.firebase.auth)
            implementation(libs.firebase.messaging)
            implementation(libs.coil.compose)
            implementation(libs.coil.network.ktor3)
            implementation(libs.multiplatform.settings)
            implementation(libs.multiplatform.settings.no.arg)
        }
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(libs.sqldelight.android.driver)
            implementation(libs.google.play.services.auth)
            implementation(libs.compose.ui.tooling)
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.splashscreen)
            implementation(platform("com.google.firebase:firebase-bom:${libs.versions.firebaseBom.get()}"))
            implementation(libs.firebase.messaging)
            implementation("com.google.zxing:core:3.5.3")
            implementation("androidx.camera:camera-camera2:1.4.1")
            implementation("androidx.camera:camera-lifecycle:1.4.1")
            implementation("androidx.camera:camera-view:1.4.1")
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.sqldelight.native.driver)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
            implementation(libs.mockk)
            implementation(libs.kotest.assertions.core)
        }
    }
}

sqldelight {
    databases {
        create("AppDatabase") {
            packageName.set("com.snehonir.niyeesho.database")
            srcDirs("src/commonMain/sqldelight")
        }
    }
}

buildkonfig {
    packageName = "com.snehonir.niyeesho"
    defaultConfigs {
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "BASE_URL",
            "https://www.googleapis.com/drive/v3/",
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "FCM_BASE_URL",
            "https://fcm.googleapis.com/v1/",
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "FCM_PROJECT_ID",
            providers.gradleProperty("fcmProjectId").orNull ?: "niye-esho",
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "APP_VERSION",
            providers.gradleProperty("appVersion").orNull ?: "1.0.0",
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "GOOGLE_WEB_CLIENT_ID",
            providers.gradleProperty("googleWebClientId").orNull ?: "",
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.BOOLEAN,
            "DEBUG",
            providers.gradleProperty("debugBuild").orNull ?: "true",
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.BOOLEAN,
            "USE_FAKE_DATA",
            providers.gradleProperty("useFakeData").orNull ?: "true",
        )
    }
}
