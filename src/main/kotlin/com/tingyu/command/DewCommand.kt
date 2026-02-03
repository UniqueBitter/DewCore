package com.tingyu.command

import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader

/**
 * 主命令
 */
@CommandHeader(name = "dew")
object DewCommand {
    @CommandBody
    val main = CommandBody {
        // TODO: 实现命令逻辑
    }
}
