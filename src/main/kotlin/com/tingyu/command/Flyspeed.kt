package com.tingyu.command

import org.bukkit.entity.Player
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand

@CommandHeader("flyspeed", permission = "panling.admin.fly")
object Flyspeed {
    @CommandBody(permission = "panling.admin.fly")
    val flyspeed = mainCommand {
        dynamic(optional = true) {
            execute<Player> { sender, context, argument ->
                if (argument.isEmpty()) {
                    sender.sendMessage("§a当前飞行速度: ${fmtSpeed(sender.flySpeed)}")
                } else {
                    val speed = argument.toFloatOrNull()
                    if (speed == null) {
                        sender.sendMessage("§c请输入有效的数字")
                        return@execute
                    }
                    try {
                        sender.flySpeed = speed / 10.0f
                        sender.sendMessage("§a飞行速度已设置为: ${fmtSpeed(speed)}")
                    } catch (e: IllegalArgumentException) {
                        sender.sendMessage("§c数值超出范围: $speed")
                    }
                }
            }
        }
        execute<Player> { sender, context, argument ->
            sender.sendMessage("§a当前飞行速度: ${fmtSpeed(sender.flySpeed)}")
        }
    }

    private fun fmtSpeed(v: Float): String =
        if (v == v.toInt().toFloat()) v.toInt().toString() else "%.1f".format(v)
}