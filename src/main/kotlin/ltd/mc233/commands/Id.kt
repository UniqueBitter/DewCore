package ltd.mc233.commands


import net.kyori.adventure.text.TranslatableComponent
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.module.nms.itemTagReader

@CommandHeader("id", permission = "panling.admin.id")
object Id {
    @CommandBody(permission = "panling.admin.id")
    val id = mainCommand {
        execute<Player> { sender, context, argument ->
            val player = sender
            val item = player.inventory.itemInMainHand
            item.itemTagReader {
                val itemId = getString("id", "")

                // 获取物品名称信息
                val (displayText, copyText) = getItemNameInfo(item)

                val name = TextComponent("§a当前name：$displayText (点我复制当前物品name)").apply {
                    hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("§e点击复制"))
                    clickEvent = ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, copyText)
                }
                val id = TextComponent("§a当前id：$itemId (点我复制当前物品id)").apply {
                    hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("§e点击复制"))
                    clickEvent = ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, itemId)
                }
                val type = TextComponent("§a当前type：${item.type} (点我复制当前物品type)").apply {
                    hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("§e点击复制"))
                    clickEvent = ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "${item.type}")
                }
                sender.spigot().sendMessage(name)
                sender.spigot().sendMessage(id)
                sender.spigot().sendMessage(type)
            }
        }
    }

    // 获取物品名称信息的辅助函数
    private fun getItemNameInfo(item: org.bukkit.inventory.ItemStack): Pair<String, String> {
        val meta = item.itemMeta
        return if (meta != null && meta.hasDisplayName()) {
            when (val displayNameComponent = meta.displayName()) {
                is TranslatableComponent -> {
                    // 如果是翻译键
                    val translationKey = displayNameComponent.key()
                    val displayText = LegacyComponentSerializer.legacySection().serialize(displayNameComponent)
                    val copyText = "\"$translationKey\""
                    Pair(displayText, copyText)
                }
                else -> {
                    // 如果是普通文本
                    val displayText = LegacyComponentSerializer.legacySection().serialize(displayNameComponent!!)
                    Pair(displayText, displayText)
                }
            }
        } else {
            // 如果没有自定义名称，使用物品默认名称
            val defaultName = item.type.name.lowercase().replace('_', ' ')
            Pair(defaultName, defaultName)
        }
    }
}