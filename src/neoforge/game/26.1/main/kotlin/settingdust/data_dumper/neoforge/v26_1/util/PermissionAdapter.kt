package settingdust.data_dumper.neoforge.v26_1.util

import net.minecraft.commands.CommandSourceStack
import net.minecraft.server.permissions.Permission
import net.minecraft.server.permissions.PermissionLevel
import net.minecraft.world.entity.player.Player
import settingdust.data_dumper.neoforge.util.PermissionAdapter
import settingdust.data_dumper.util.MinecraftVersion

class PermissionAdapter : PermissionAdapter {
    init {
        MinecraftVersion.V261.requireCurrent()
    }

    override fun CommandSourceStack.checkPermission(level: Int): Boolean =
        permissions().hasPermission(Permission.HasCommandLevel(PermissionLevel.byId(level)))

    override fun Player.checkPermission(level: Int): Boolean =
        permissions().hasPermission(Permission.HasCommandLevel(PermissionLevel.byId(level)))
}
