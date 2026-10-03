plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation("com.android.tools.build:gradle:8.7.3")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.1.21")
    implementation("org.jetbrains.compose:compose-gradle-plugin:1.7.3")
}

gradlePlugin {
    plugins {
        register("androidApplicationConvention") {
            id = "niyeesho.android.application"
            implementationClass = "com.snehonir.niyeesho.buildlogic.AndroidApplicationConventionPlugin"
        }
        register("androidLibraryConvention") {
            id = "niyeesho.android.library"
            implementationClass = "com.snehonir.niyeesho.buildlogic.AndroidLibraryConventionPlugin"
        }
        register("kmpLibraryConvention") {
            id = "niyeesho.kmp.library"
            implementationClass = "com.snehonir.niyeesho.buildlogic.KmpLibraryConventionPlugin"
        }
        register("composeMultiplatformConvention") {
            id = "niyeesho.compose.multiplatform"
            implementationClass = "com.snehonir.niyeesho.buildlogic.ComposeMultiplatformConventionPlugin"
        }
    }
}
