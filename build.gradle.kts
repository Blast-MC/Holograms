plugins {
    java
    `maven-publish`
    id("io.freefair.lombok") version "8.0.1"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("io.papermc.paperweight.userdev") version "1.3.7"
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
    apply(plugin = "com.github.johnrengelman.shadow")
    apply(plugin = "io.freefair.lombok")
    apply(plugin = "maven-publish")

    dependencies {
        compileOnly("io.papermc.paper:paper-api:1.19.4-R0.1-SNAPSHOT")
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    }
}

dependencies {
    implementation(project(":HologramsAPI"))
    implementation("gg.projecteden:commands-api:1.0.0-SNAPSHOT")
    implementation("org.reflections:reflections:0.9.11")
    implementation("de.tr7zw:item-nbt-api:2.11.2")
    paperweightDevBundle("io.papermc.paper", "1.19.4-R0.1-SNAPSHOT")
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
        options.compilerArgs.add("-parameters")
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name()

        filesMatching("plugin.yml") {
            expand("version" to project.version)
        }
    }

    shadowJar {
        relocate("org.reflections", "holograms.org.reflections")
        relocate("de.tr7zw", "holograms.de.tr7zw")
    }

}
