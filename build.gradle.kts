plugins {
    java
    `maven-publish`
    id("io.freefair.lombok") version "8.11"
    id("com.gradleup.shadow") version "8.3.0"
    id("io.papermc.paperweight.userdev") version "2.0.0-beta.14"
}

allprojects {
    group = "${project.group}"
    version = "${project.version}"

    repositories {
        mavenLocal()
        maven { url = uri("https://repo.papermc.io/repository/maven-public/") }
        maven { url = uri("https://oss.sonatype.org/content/groups/public/") }
        maven { url = uri("https://repo.maven.apache.org/maven2/") }
        maven { url = uri("https://sonatype.projecteden.gg/repository/maven-public/") }
        maven { url = uri("https://repo.codemc.org/repository/maven-public/") }
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "com.gradleup.shadow")
    apply(plugin = "io.freefair.lombok")
    apply(plugin = "maven-publish")

    dependencies {
        compileOnly("io.papermc.paper:paper-api:1.21.4-R0.1-SNAPSHOT")
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    implementation(project(":HologramsAPI"))
    implementation("gg.projecteden:commands-api:1.0.0-SNAPSHOT")
    implementation("org.reflections:reflections:0.10.2")
    implementation("de.tr7zw:item-nbt-api:2.14.1")
    paperweight.paperDevBundle("1.21.4-R0.1-SNAPSHOT", "gg.projecteden.parchment")
}

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION

tasks {
    assemble {
        dependsOn(shadowJar)
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(21)
        options.compilerArgs.add("-parameters")
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name()

        filesMatching("plugin.yml") {
            expand("version" to project.version)
        }
    }

    shadowJar {
        archiveClassifier.set("")
        relocate("org.reflections", "holograms.org.reflections")
        relocate("de.tr7zw", "holograms.de.tr7zw")
    }

}
