package com.tingyu.mob

import com.tingyu.command.DewItem
import com.tingyu.player.stat.StatLayer
import com.tingyu.player.stat.StatType
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.inventory.ItemStack

/**
 * 定义一种怪物的属性。
 * 与 PlayerProfile 不同，怪物属性在注册时写死，不需要存档。
 *
 * @param entityType 绑定的原版实体类型，非 null 时会在自然生成时自动应用；
 *                   为 null 则只能通过 MobManager.apply() 手动应用。
 */
class MobProfile(val id: String, val entityType: EntityType? = null) {

    var displayName: String = ""
    var xpReward: Int = 0

    private val drops: MutableList<MobDrop> = mutableListOf()

    fun drops(): List<MobDrop> = drops

    /**
     * 手动召唤时使用的实体类型。
     * 默认与 entityType 相同；entityType = null 的精英需通过 .spawn(EntityType.XXX) 单独指定。
     */
    var spawnType: EntityType? = entityType

    private val stats: MutableMap<StatType, StatLayer> = mutableMapOf()

    fun buildLayer(type: StatType): StatLayer =
        stats.getOrDefault(type, StatLayer(finalPercent = 1.0))

    /** 设置某属性的完整四层 */
    fun set(
        type: StatType,
        base: Double = 0.0,
        percent: Double = 0.0,
        finalPercent: Double = 1.0,
        finalFlat: Double = 0.0
    ): MobProfile {
        stats[type] = StatLayer(base, percent, finalPercent, finalFlat)
        return this
    }

    /**
     * 设置基础值。
     * HP 特殊处理：公式为 (20 + base)，写多少就是最终多少血（自动减去 +20 偏移）。
     */
    fun base(type: StatType, value: Double): MobProfile {
        val stored = if (type == StatType.HP) value - 20.0 else value
        return set(type, base = stored)
    }

    /** 设置游戏内显示名称（链式调用） */
    fun name(value: String): MobProfile {
        displayName = value
        return this
    }

    /** 设置击杀经验奖励（链式调用） */
    fun xp(value: Int): MobProfile {
        xpReward = value
        return this
    }

    /** 添加掉落物（ItemStack，立即捕获） */
    fun drop(item: ItemStack, amount: IntRange = 1..1, chance: Double = 1.0): MobProfile {
        drops += MobDrop({ item.clone() }, amount, chance)
        return this
    }

    /** 添加掉落物（原版材料） */
    fun drop(material: Material, amount: IntRange = 1..1, chance: Double = 1.0): MobProfile {
        drops += MobDrop({ ItemStack(material) }, amount, chance)
        return this
    }

    /** 添加掉落物（DewItem，立即捕获引用） */
    fun drop(dewItem: DewItem, amount: IntRange = 1..1, chance: Double = 1.0): MobProfile {
        drops += MobDrop({ dewItem.getItem() }, amount, chance)
        return this
    }

    /** 添加掉落物（物品 ID，懒加载——死亡时才从注册表查找，解决初始化顺序问题） */
    fun drop(itemId: String, amount: IntRange = 1..1, chance: Double = 1.0): MobProfile {
        drops += MobDrop({ DewItem.fromId(itemId)?.getItem() }, amount, chance)
        return this
    }

    /** 指定手动召唤时的实体类型（entityType = null 的精英使用） */
    fun spawn(type: EntityType): MobProfile {
        spawnType = type
        return this
    }
}
