package settingdust.data_dumper.util

enum class MinecraftVersion(
    val versionPrefix: String,
) {
    V1201("1.20.1"),
    V1211("1.21"),
    V261("26.1");

    fun matches(versionName: String): Boolean = versionName.startsWith(versionPrefix)

    fun isCurrent(): Boolean = current == this

    fun requireCurrent() {
        check(isCurrent()) {
            "Expected Minecraft version $this, got $current ($currentVersionName)"
        }
    }

    companion object {
        val currentVersionName: String by lazy {
            MinecraftVersionNameProvider.currentVersionName()
        }

        val current: MinecraftVersion by lazy { of(currentVersionName) }

        fun of(versionName: String): MinecraftVersion = entries.firstOrNull { it.matches(versionName) }
            ?: error("Unsupported Minecraft version: $versionName")
    }
}

interface MinecraftVersionNameProvider {
    companion object : MinecraftVersionNameProvider by ServiceLoaderUtil.findService()

    fun currentVersionName(): String
}
