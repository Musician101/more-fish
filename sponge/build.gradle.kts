import org.spongepowered.gradle.plugin.config.PluginLoaders
import org.spongepowered.plugin.metadata.model.PluginDependency

plugins {
    id("org.spongepowered.gradle.plugin") version "2.1.1"
}

dependencies {
    api(project(":common"))
    api("com.github.musician101.musigui:sponge:1.2.2")
    //TODO temp dependency due to new project
    api("io.musician101:spongecmd:1.0-SNAPSHOT")
}

sponge {
    apiVersion("10.0.0")
    plugin("morefish") {
        loader {
            name(PluginLoaders.JAVA_PLAIN)
            version("1.0")
        }
        version("${project.version}")
        license("MIT")
        displayName("MoreFish")
        entrypoint("me.elsiff.morefish.sponge.SpongeMoreFish")
        description("Plugin based build submission and feature system.")
        contributor("elsiff") {
            description("Original Plugin Author")
        }
        contributor("Musician101") {
            description("Lead Developer")
        }
        dependency("spongeapi") {
            loadOrder(PluginDependency.LoadOrder.AFTER)
            optional(false)
        }
    }
}

tasks {
    shadowJar {
        dependencies {
            include(dependency(":common"))
            include(dependency("io.musician101:"))
        }

        relocate("io.musician101.bukkitier", "com.campmongoose.serversaturday.lib.io.musician101.bukkitier")
    }
}
