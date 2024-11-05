plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    js(IR) {
        browser {}
    }

    jvm()

    sourceSets {
        commonMain.dependencies {
            implementation(jsLibs.react)
            implementation(jsLibs.reactDom)
            implementation(jsLibs.reactRouterDom)
            implementation(jsLibs.emotion)
            implementation(jsLibs.csstype)

            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:${libs.versions.kotlinx.coroutines.get()}")
            implementation(libs.ktor.client.contentNegotiation)
            implementation(libs.ktor.client.js)
            implementation(libs.ktor.serialization.json)
            implementation(projects.shared)
        }
    }
}