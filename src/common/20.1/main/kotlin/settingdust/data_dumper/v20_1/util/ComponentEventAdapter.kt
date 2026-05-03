package settingdust.data_dumper.v20_1.util

import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.HoverEvent
import settingdust.data_dumper.util.ComponentEventAdapter
import settingdust.data_dumper.util.MinecraftVersion

class ComponentEventAdapter : ComponentEventAdapter {
    init {
        MinecraftVersion.V1201.requireCurrent()
    }

    override fun showText(text: Component): HoverEvent {
        return HoverEvent(HoverEvent.Action.SHOW_TEXT, text)
    }

    override fun copyToClipboard(text: String): ClickEvent {
        return ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, text)
    }
}
