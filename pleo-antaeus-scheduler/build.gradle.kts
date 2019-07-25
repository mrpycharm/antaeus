plugins {
    kotlin("jvm")
}

kotlinProject()

dependencies {
    implementation(project(":pleo-antaeus-core"))

    compile(project(":pleo-antaeus-app"))
    compile("co.metalab.asyncawait:asyncawait:1.0.1-beta1")
}