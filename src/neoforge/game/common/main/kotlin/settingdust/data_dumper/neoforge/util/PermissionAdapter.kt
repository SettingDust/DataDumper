package settingdust.data_dumper.neoforge.util

import net.minecraft.commands.CommandSourceStack
import net.minecraft.world.entity.player.Player
import settingdust.data_dumper.util.ServiceLoaderUtil

/**
 * SPI interface for abstracting permission checks across NeoForge MC versions.
 * The method name may differ between versions
 * (e.g. `hasPermissions` vs `hasPermission` for Entity,
 * `hasPermission` vs `hasPermissionLevel` for CommandSourceStack).
 */
interface PermissionAdapter {
    companion object : PermissionAdapter by ServiceLoaderUtil.findService()

    /**
     * Checks if the command source has the given permission level.
     * Abstracts over version-specific `CommandSourceStack.hasPermission(Int)` / `hasPermissionLevel(Int)`.
     */
    fun CommandSourceStack.checkPermission(level: Int): Boolean

    /**
     * Checks if the entity has the given permission level.
     * Abstracts over version-specific `Entity.hasPermission(Int)` / `Entity.hasPermissions(Int)`.
     */
    fun Player.checkPermission(level: Int): Boolean
}
