import com.google.protobuf.gradle.*

plugins {
    `java-library`

    `maven-publish`

    // Protobug plugin
    id("com.google.protobuf") version "0.9.4"
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(libs.junit.jupiter)

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // gRPC dependencies
    implementation("io.grpc:grpc-stub:1.24.0")
    implementation("io.grpc:grpc-netty:1.24.0")
    implementation("io.grpc:grpc-protobuf:1.24.0")
    implementation("com.google.protobuf:protobuf-java:3.8.0")
    implementation("javax.annotation:javax.annotation-api:1.3.2")
}

protobuf {
    protoc {
        // Download pre-built protoc binary from Maven repository.
        artifact = "com.google.protobuf:protoc:3.8.0"
    }

    plugins {
        id("grpc") {
            // Download pre-built protoc-gen-grpc-java binary from Maven repository.
            artifact = "io.grpc:protoc-gen-grpc-java:1.24.0"
        }
    }

    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.plugins {
                id("grpc") { }
            }
        }
    }
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(11)
    }
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.github.kennhung"
            artifactId = "gobgp-grpc-java"
            version = "3.29.0"

            from(components["java"])
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/kennhung/gobgp-grpc-java")

            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
