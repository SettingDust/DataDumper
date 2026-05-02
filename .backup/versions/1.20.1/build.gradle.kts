val id: String by rootProject.properties
val name: String by rootProject.properties
val author: String by rootProject.properties
val description: String by rootProject.properties

java {
    toolchain { languageVersion = JavaLanguageVersion.of(17) }

    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17

    withSourcesJar()
}

loom {
    mixin {
        defaultRefmapName = "$id-1.20.1.refmap.json"

        add("main", defaultRefmapName.get())
    }

    runs { configureEach { vmArgs("-Dmixin.debug.export=true", "-Dmixin.debug.verbose=true", "-Dmixin.debug.countInjections=true") } }

    //    accessWidenerPath = file("src/main/resources/$id.1.20.1.accesswidener")

    mods {
        register(id) {
            sourceSet(rootProject.sourceSets["main"])
        }
        register("$id-1_20_1") {
            sourceSet("main")
        }
    }

    runs {
        named("client") {
            ideConfigGenerated(true)
        }
    }
}

dependencies {
    minecraft(catalog.minecraft.fabric)
    mappings(variantOf(catalog.mapping.yarn) { classifier("v2") })

    implementation(project(":")) { isTransitive = false }

    modImplementation(catalog.fabric.loader)
    modImplementation(catalog.fabric.api)
    modImplementation(catalog.fabric.kotlin)

    modImplementation(catalog.modmenu)
}

val metadata =
    mapOf(
        "group" to rootProject.group,
        "author" to author,
        "id" to id,
        "name" to name,
        "version" to version,
        "description" to description,
        "source" to "https://github.com/SettingDust/DataDumper",
        "minecraft" to "~1.20.1",
        "fabric_loader" to ">=0.15",
        "fabric_kotlin" to ">=1.11",
        "modmenu" to "*",
    )

tasks {
    withType<ProcessResources> {
        inputs.properties(metadata)
        filesMatching(listOf("fabric.mod.json", "*.mixins.json")) { expand(metadata) }
    }

    ideaSyncTask { enabled = true }
}
