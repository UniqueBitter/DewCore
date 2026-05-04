package com.tingyu.player

import com.tingyu.item.equip.EquipManager
import com.tingyu.player.job.JobStatTable
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player

object LevelManager {

    const val MAX_LEVEL = 100

    /** 升到下一级所需经验：随等级平方增长 */
    fun expToNextLevel(level: Int): Long {
        if (level >= MAX_LEVEL) return Long.MAX_VALUE
        return level * 150L + level * level * 8L
    }

    /**
     * 给予经验，自动处理连续升级。
     * 经验溢出时延续到下一级，直至不再满足升级条件。
     */
    fun gainExp(player: Player, amount: Long) {
        if (amount <= 0) return
        val profile = PlayerManager.get(player)
        if (profile.level >= MAX_LEVEL) return

        profile.exp += amount

        var leveled = false
        while (profile.level < MAX_LEVEL) {
            val needed = expToNextLevel(profile.level)
            if (profile.exp < needed) break
            profile.exp -= needed
            profile.level++
            leveled = true
        }

        if (leveled) onLevelUp(player, profile)
    }

    /**
     * 根据当前 job + level 重写 baseStats（覆盖旧成长值）。
     * 在上线、升级、换职业时调用。
     */
    fun rebuildBaseStats(profile: PlayerProfile) {
        profile.baseStats.clear()
        JobStatTable.compute(profile.job, profile.level)
            .forEach { (type, value) -> profile.setBase(type, value) }
    }

    private fun onLevelUp(player: Player, profile: PlayerProfile) {
        rebuildBaseStats(profile)
        EquipManager.recalculate(player)
        val maxHp = player.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value ?: 20.0
        player.health = maxHp
        player.sendMessage("§6✦ §e你升级了！当前等级: §a${profile.level} §e级")
        player.sendMessage("  §7下一级需要: §f${expToNextLevel(profile.level)} §7经验")
    }
}
