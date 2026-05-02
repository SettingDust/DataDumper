plugins {
    java
}

subprojects {
    apply(plugin = "maven-publish")
    apply(plugin = rootProject.catalog.plugins.idea.ext.get().pluginId)

    apply(plugin = rootProject.catalog.plugins.kotlin.jvm.get().pluginId)
    apply(plugin = rootProject.catalog.plugins.kotlin.plugin.serialization.get().pluginId)

    apply(plugin = rootProject.catalog.plugins.fabric.loom.get().pluginId)

    base { archivesName.set("${rootProject.base.archivesName.get()}-${project.name}") }
}
