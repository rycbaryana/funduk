plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    js(IR) {
        browser {}
    }

    jvm()

    sourceSets {
        jsMain.dependencies {
            implementation(jsLibs.react)
            implementation(jsLibs.reactDom)
            implementation(jsLibs.emotion)
            implementation(jsLibs.csstype)
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:${libs.versions.kotlinx.coroutines.get()}")
            implementation(libs.ktor.serialization.json)
            implementation(libs.ktor.client.contentNegotiation)
            implementation(libs.ktor.client.js)
            implementation(libs.kotlinx.datetime)
            implementation(projects.shared)
        }
    }
}