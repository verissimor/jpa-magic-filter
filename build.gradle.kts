import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.nio.file.Files
import java.nio.file.Path
import java.util.regex.Pattern

plugins {
  id("org.springframework.boot") version "2.6.1"
  id("io.spring.dependency-management") version "1.0.11.RELEASE"
  kotlin("jvm") version "1.6.0"
  kotlin("plugin.spring") version "1.6.0"
  kotlin("plugin.jpa") version "1.6.10"

  kotlin("kapt") version "1.6.0"

  id("org.jlleitschuh.gradle.ktlint") version "10.2.0"
  `java-library`
  `maven-publish`
}

group = "com.verissimor.lib.jpamagicfilter"
version = "1.0.0-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

configurations {
  compileOnly {
    extendsFrom(configurations.annotationProcessor.get())
  }
}

repositories {
  maven { url = uri("https://repo.spring.io/milestone") }
  mavenCentral()
}

dependencies {

  // spring
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("org.springframework.boot:spring-boot-starter-web")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

  kapt("org.springframework.boot:spring-boot-configuration-processor")

  testImplementation("org.springframework.boot:spring-boot-starter-test")
  testImplementation("org.springframework.security:spring-security-test")

  // test service
  testImplementation("com.h2database:h2")
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
}

publishing {
  publications {
    create<MavenPublication>("maven") {
      groupId = project.group.toString()
      artifactId = rootProject.name
      version = project.version.toString()

      from(components["java"])
    }
  }
}

// FIXME workaround for https://github.com/jfrog/build-info/issues/198
val fixPom = tasks.register("fixPom") {
  doLast {
    val file = Path.of("$buildDir/publications/maven/pom-default.xml")
    var content = Files.readString(file)
    val pattern = Pattern.compile(
      "(<dependencyManagement>.+?<dependencies>)(.+?)(</dependencies>.+?</dependencyManagement>)",
      Pattern.DOTALL
    )
    var matcher = pattern.matcher(content)

    if (matcher.find()) {
      val firstDependencies = matcher.group(2)
      content = matcher.replaceFirst("")

      matcher = pattern.matcher(content)
      if (!matcher.find()) {
        throw GradleException("Didn't find second <dependencyManagement> tag, maybe https://github.com/jfrog/build-info/issues/198 has been fixed?")
      }
      content = matcher.replaceFirst("$1$2$firstDependencies$3")
    }

    Files.writeString(file, content)
  }
}
tasks.findByName("generatePomFileForMavenPublication")?.finalizedBy(fixPom)
