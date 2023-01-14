import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Base64

plugins {
  id("org.springframework.boot") version "2.7.3"
  id("io.spring.dependency-management") version "1.0.13.RELEASE"
  kotlin("jvm") version "1.6.21"
  kotlin("plugin.spring") version "1.6.21"
  kotlin("plugin.jpa") version "1.6.21"

  kotlin("kapt") version "1.6.21"

  id("org.jlleitschuh.gradle.ktlint") version "10.3.0"
  `java-library`
  `maven-publish`
  id("io.github.gradle-nexus.publish-plugin") version "1.1.0"
  signing
  jacoco
}

group = "io.github.verissimor.lib"
version = System.getenv("RELEASE_VERSION") ?: "1.0.6-SNAPSHOT"

java {
  sourceCompatibility = JavaVersion.VERSION_1_8
  targetCompatibility = JavaVersion.VERSION_1_8
  withSourcesJar()
  withJavadocJar()
}

configurations {
  compileOnly {
    extendsFrom(configurations.annotationProcessor.get())
  }
}

repositories {
  mavenCentral()
}

dependencies {

  // spring
  compileOnly("org.springframework.boot:spring-boot-starter-data-jpa")
  compileOnly("org.springframework.boot:spring-boot-starter-web")
  compileOnly("org.springframework.boot:spring-boot-starter-data-r2dbc")
  compileOnly("org.springframework.boot:spring-boot-starter-webflux")

  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

  kapt("org.springframework.boot:spring-boot-configuration-processor")

  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.springframework.security:spring-security-test")

  // test service
  testImplementation("com.h2database:h2")
  testImplementation("org.springframework.boot:spring-boot-starter-data-jpa")
  testImplementation("org.springframework.boot:spring-boot-starter-web")
  testImplementation("org.springframework.data:spring-data-relational:2.4.2")
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    freeCompilerArgs = listOf("-Xjsr305=strict")
    jvmTarget = "1.8"
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
  enabled = false
}

tasks.getByName<Jar>("jar") {
  enabled = true
  archiveClassifier.set("")
}

publishing {
  publications {
    create<MavenPublication>("mavenJava") {
      from(components["java"])

      pom {
        name.set("Jpa Magic Filter")
        description.set("This library handles conversion between spring rest Request Params and JPA Specification")
        url.set("https://github.com/verissimor/jpa-magic-filter")
        licenses {
          license {
            name.set("The Apache License, Version 2.0")
            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
          }
        }
        developers {
          developer {
            id.set("verissimo")
            name.set("Verissimo Joao Ribeiro")
            email.set("verissimo.jribeiro@gmail.com")
          }
        }
        scm {
          connection.set("git@github.com:verissimor/jpa-magic-filter.git")
          developerConnection.set("git@github.com/verissimor/jpa-magic-filter.git")
          url.set("https://github.com/verissimor/jpa-magic-filter")
        }
      }
    }
  }
}

nexusPublishing {
  repositories {
    create("myNexus") {
      nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
      snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
      username.set(System.getenv("NEXUS_USERNAME") ?: project.properties["myNexusUsername"].toString())
      password.set(System.getenv("NEXUS_PASSWORD") ?: project.properties["myNexusPassword"].toString())
    }
  }
}

signing {
  val signingKeyBase64 = System.getenv("ORG_GRADLE_PROJECT_signingKey") ?: project.properties["signatory.signingKey"].toString()
  val signingPassword = System.getenv("ORG_GRADLE_PROJECT_signingPassword") ?: project.properties["signatory.signingPassword"].toString()
  val signingKey = String(Base64.getDecoder().decode(signingKeyBase64)).trim()
  useInMemoryPgpKeys(signingKey, signingPassword)
  sign(publishing.publications["mavenJava"])
}

tasks.withType<Sign> {
  enabled = project.version.toString().endsWith("SNAPSHOT").not()
}

tasks.jacocoTestReport {
  reports {
    csv.required.set(true)
  }
}
