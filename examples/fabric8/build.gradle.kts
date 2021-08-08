plugins {
    id("conventions-kotlin")
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation(project(":eksx"))
    implementation(Libs.fabric8KubernetesClient)
}
