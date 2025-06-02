
plugins {
    kotlin("jvm") version "2.1.20"
    application
}

group = "com.eltonkola"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
    implementation ("org.jetbrains:markdown:0.1.45")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(20)
}

application {
    mainClass.set("BlokKt")
}

tasks {
    withType<Jar> {

        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        manifest {
            attributes["Main-Class"] = application.mainClass
        }
        // here zip stuff found in runtimeClasspath:
        from(configurations.runtimeClasspath.get().map {
            if (it.isDirectory) {
                it
            } else {
                zipTree(it)
            }
        })

    }
}

