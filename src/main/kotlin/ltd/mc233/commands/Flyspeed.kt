package ltd.mc233.commands

import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand

@CommandHeader("flyspeed", permission = "panling.admin.fly")
object Flyspeed {
    @CommandBody(permission = "panling.admin.fly")
    val flyspeed = mainCommand {
        // 定义可选的整数参数
        dynamic(optional = true) {
            suggestion<Player> { sender, context ->
                listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "10")
            }
            execute<Player> { sender, context, argument ->
                if (argument.isEmpty()) {
                    sender.sendMessage("§a当前飞行速度: ${(sender.flySpeed * 10).toInt()}")
                } else {
                    try {
                        val speed = argument.toInt()
                        val clampedSpeed = speed.coerceIn(1, 10)
                        sender.flySpeed = clampedSpeed / 10.0f
                        sender.sendMessage("§a飞行速度已设置为: $clampedSpeed")
                    } catch (e: NumberFormatException) {
                        sender.sendMessage("§c请输入有效的数字！使用格式: /flyspeed <1-10>")
                    }
                }
            }
        }
        // 无参数时的执行
        execute<Player> { sender, context, argument ->
            sender.sendMessage("§a当前飞行速度: ${(sender.flySpeed * 10).toInt()}")
        }
    }
}