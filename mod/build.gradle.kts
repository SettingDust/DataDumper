plugins {
    alias(catalog.plugins.fabric.loom)

    alias(catalog.plugins.kotlin.jvm)
    alias(catalog.plugins.kotlin.plugin.serialization)

    alias(catalog.plugins.explosion)
}

val id: String by rootProject.properties
val name: String by rootProject.properties
val author: String by rootProject.properties
val description: String by rootProject.properties

base.archivesName = name

kotlin { jvmToolchain(17) }

loom {
    mixin {
        defaultRefmapName = "$id.refmap.json"

        add("main", "$id.refmap.json")
    }

    accessWidenerPath = file("src/main/resources/$id.accesswidener")

    mods { register(id) { sourceSet(sourceSets["main"]) } }

    runs {
        configureEach { ideConfigGenerated(true) }
        named("client") { name("Fabric Client") }
        named("server") { name("Fabric Server") }
    }
}

dependencies {
    minecraft(catalog.minecraft)
    mappings(variantOf(catalog.yarn) { classifier("v2") })

    modImplementation(catalog.fabric.loader)
    modImplementation(catalog.fabric.api)
    modImplementation(catalog.fabric.kotlin)

    modImplementation(catalog.modmenu)

    modImplementation(catalog.fabric.permissions)
}

java {
    // Loom will automatically attach sourcesJar to a RemapSourcesJar task and to the "build" task
    // if it is present.
    // If you remove this line, sources will not be generated.
    withSourcesJar()
}

val metadata =
    mapOf(
        "group" to group,
        "author" to author,
        "id" to id,
        "name" to name,
        "version" to version,
        "description" to description,
        "source" to "https://github.com/SettingDust/DataDumper",
        "minecraft" to "~1.20",
        "fabric_loader" to ">=0.12",
        "fabric_kotlin" to ">=1.10",
        "modmenu" to "*",
    )

tasks {
    withType<ProcessResources> {
        inputs.properties(metadata)
        filesMatching(listOf("fabric.mod.json", "*.mixins.json")) { expand(metadata) }
    }

    jar { from("LICENSE") }

    ideaSyncTask { enabled = true }
}
