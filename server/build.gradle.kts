plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    application
}

val ktorVersion = libs.versions.ktor.get()

group = "by.funduk"
version = "1.0.0"
application {
    mainClass.set("by.funduk.ApplicationKt")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=${extra["io.ktor.development"] ?: "false"}")
}

val browserDist by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
}

dependencies {
    implementation(projects.shared)
    implementation(libs.logback)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    testImplementation(libs.ktor.server.tests)
    testImplementation(libs.kotlin.test.junit)
    implementation("io.ktor:ktor-server-html-builder:$ktorVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-html:0.8.1")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")

    browserDist(
        project(
            mapOf(
                "path" to ":client",
                "configuration" to "browserDist"
            )
        )
    )
}

tasks.withType<Copy>().named("processResources") {
    from(browserDist)
}