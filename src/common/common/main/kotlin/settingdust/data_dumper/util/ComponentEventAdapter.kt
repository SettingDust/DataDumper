package settingdust.data_dumper.util

import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.HoverEvent

/**
 * SPI interface for abstracting [HoverEvent] and [ClickEvent] creation across MC versions.
 * The constructor parameters and factory methods may differ between versions
 * (e.g. direct constructors vs static factory methods).
 */
interface ComponentEventAdapter {
    companion object : ComponentEventAdapter by ServiceLoaderUtil.findService()

    /**
     * Creates a [HoverEvent] that shows the given text when hovered.
     * Abstracts over version-specific `HoverEvent` construction
     * (e.g. `HoverEvent(Action.SHOW_TEXT, text)` vs `HoverEvent.showText(text)`).
     *
     * @param text the component to display on hover
     * @return a HoverEvent configured to show the given text
     */
    fun showText(text: Component): HoverEvent

    /**
     * Creates a [ClickEvent] that copies the given text to the clipboard when clicked.
     * Abstracts over version-specific `ClickEvent` construction
     * (e.g. `ClickEvent(Action.COPY_TO_CLIPBOARD, text)` vs `ClickEvent.copyToClipboard(text)`).
     *
     * @param text the text to copy to clipboard
     * @return a ClickEvent configured to copy the given text
     */
    fun copyToClipboard(text: String): ClickEvent
}
