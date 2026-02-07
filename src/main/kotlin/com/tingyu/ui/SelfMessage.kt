package com.tingyu.ui

import com.tingyu.player.PlayerManager
import com.tingyu.player.job.Job
import com.tingyu.player.race.Race
import org.bukkit.Statistic
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import taboolib.library.xseries.XMaterial
import taboolib.module.ui.openMenu
import taboolib.module.ui.type.Basic
import taboolib.module.ui.type.Chest
import taboolib.platform.util.buildItem

object SelfMessage {
    fun openMessage(player: Player) {
        val dew = PlayerManager.get(player)
        val killCount = player.getStatistic(Statistic.MOB_KILLS)
        val deathCount = player.getStatistic(Statistic.DEATHS)

        player.openMenu<Chest>("§b 个人信息") {
            map(
                "####U####",
                "#       #",
                "#A B C  #",
                "#       #",
                "#########"
            )
            set('#', buildItem(XMaterial.BLACK_STAINED_GLASS_PANE) { name = " " }){
                isCancelled = true
            }
            set('U', buildItem(XMaterial.ARROW) { name = "§c返回" }) {
                isCancelled = true
            }
            set('A', buildItem(XMaterial.PLAYER_HEAD) {
                name = "§6个人属性"
                skullOwner = player.name
                lore.add("§7------------------------------")
                lore.add(when (dew.race) {
                    Race.SHEN -> "§b种族: 神族"
                    Race.YAO -> "§a种族: 妖族"
                    Race.XIAN -> "§3种族: 仙族"
                    Race.ZHANSHEN -> "§c种族: 战神族"
                    Race.REN -> "§6种族: 人族"
                    Race.NONE -> "§7种族: 无"
                })
                lore.add("§b等级: ${player.level}")
                lore.add(when (dew.job) {
                    Job.ZHANSHI -> "§c职业: 战士"
                    Job.YOUXIA -> "§a职业: 弓箭手"
                    Job.LIANDAN -> "§6职业: 炼丹师"
                    Job.NONE -> "§7职业: 无"
                })
                lore.add("§7------------------------------")
            }){
                isCancelled = true
            }

            set('B', buildItem(XMaterial.DIAMOND_SWORD) {
                name = "§a世界痕迹"
                lore.add("§7------------------------------")
                lore.add("§c怪物击杀数: $killCount")
                lore.add("§4死亡次数: $deathCount")
                hideAll()
            }){
                isCancelled = true
                if (clickEvent().isLeftClick){

                }
            }

            set('C', buildItem(XMaterial.IRON_INGOT) {
                name = "§b个人资产"
                lore.add("§7------------------------------")
                lore.add("§6铜币: ${dew.copper}")
                lore.add("§6元素: ${dew.element}")
            }){
                isCancelled = true
            }
        }
    }
}