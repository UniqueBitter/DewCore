package ltd.mc233.commands

import ltd.mc233.util.ItemGet
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.chat.hover.content.Text
import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.expansion.createHelper
import taboolib.module.nms.itemTagReader

@CommandHeader("gugugu", aliases = ["gu"], permission = "panling.use")
object gugugu {
    @CommandBody(permission = "panling.use")
    val main = mainCommand {
        createHelper()
    }

    @CommandBody(permission = "panling.use.test")
    val test = subCommand {
        execute<Player> { sender, context, argument ->
            val player = sender
            val item = player.inventory.itemInMainHand
            if (ItemGet.checkItem(item,"panling:goldkey")){
                player.sendMessage("§a检测到金钥匙啦！")
            }else{
                player.sendMessage("§c未检测到")
            }
        }
    }

    @CommandBody(permission = "panling.use.test")
    val id = subCommand {
        execute<Player> { sender, context, argument ->
            val player = sender
            val item = player.inventory.itemInMainHand
            item.itemTagReader {
                val itemId = getString("id", "")
                val name = TextComponent("§a当前name：${item.displayName} (点我复制当前物品name)").apply {
                    hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("§e点击复制"))
                    clickEvent = ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, item.displayName)
                }
                val id = TextComponent("§a当前id：$itemId (点我复制当前物品id)").apply {
                    hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("§e点击复制"))
                    clickEvent = ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, itemId)
                }
                val type = TextComponent("§a当前type：$${item.type} (点我复制当前物品type)").apply {
                    hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, Text("§e点击复制"))
                    clickEvent = ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "${item.type}")
                }
                sender.spigot().sendMessage(name)
                sender.spigot().sendMessage(id)
                sender.spigot().sendMessage(type)
            }
        }
    }

}