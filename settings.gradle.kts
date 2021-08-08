@file:Suppress("UnstableApiUsage")

rootProject.name = "eksx"

dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}

include(":eksx")
project(":eksx").projectDir = File("code")
include(":examples:fabric8")