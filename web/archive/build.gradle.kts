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
                implementation(jsLibs.emotion)
                implementation(jsLibs.csstype)
                implementation(libs.ktor.serialization.json)
                implementation(libs.ktor.client.js)

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:${libs.versions.kotlinx.coroutines.get()}")
                implementation(projects.web.common)
                implementation(projects.shared)
            }
        }
    }
}


val browserDist by configurations.creating {
    isCanBeConsumed = true
    isCanBeResolved = false
}

artifacts {
    add(browserDist.name, tasks.named("jsBrowserDistribution").map { it.outputs.files.files.single() })
}