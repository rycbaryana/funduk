plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    js {
        browser {
        }
        binaries.executable()
    }
    sourceSets {
        val jsMain by getting {
            dependencies {
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
//        val jsTest by getting {
//            dependencies {
//                implementation(kotlin("test-js"))
//            }
//        }
    }
}


val browserDist by configurations.creating {
    isCanBeConsumed = true
    isCanBeResolved = false
}

artifacts {
    add(browserDist.name, tasks.named("jsBrowserDistribution").map { it.outputs.files.files.single() })
}