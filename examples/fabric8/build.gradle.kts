plugins {
    id("conventions-kotlin")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("dev.mrbergin:eksx:0.0.1")
    implementation(Libs.fabric8KubernetesClient)
}
