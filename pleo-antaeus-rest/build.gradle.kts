plugins {
    kotlin("jvm")
}

kotlinProject()

dependencies {
    implementation(project(":pleo-antaeus-core"))
    implementation(project(":pleo-antaeus-models"))
    compile(project(":pleo-antaeus-app"))

    implementation("io.javalin:javalin:2.6.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.9.8")
}
