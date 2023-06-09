publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }

    repositories {
        maven {
            name = "edenSnapshots"
            url = uri("https://sonatype.projecteden.gg/repository/maven-snapshots/")
            credentials(PasswordCredentials::class)
        }

        maven {
            name = "techSnapshots"
            url = uri("https://sonatype.blastmc.tech/repository/maven-snapshots/")
            credentials(PasswordCredentials::class)
        }
    }
}

tasks {
    java {
        withSourcesJar()
        withJavadocJar()
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }
}