plugins {
    kotlin("jvm")
}

kotlinProject()

dependencies {
    implementation("io.javalin:javalin:2.6.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.9.8")
}