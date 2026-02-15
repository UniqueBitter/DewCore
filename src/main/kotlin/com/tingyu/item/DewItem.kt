package com.tingyu.item

import com.tingyu.item.util.AddPDC.addPDC
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import taboolib.platform.util.buildItem

class DewItem private constructor(
    val data: ItemData,
    private val builder: taboolib.platform.util.ItemBuilder.() -> Unit = {}
) {

    val itemStack: ItemStack by lazy {
        buildItem(data.material) {
            name = data.displayName
            this.lore.addAll(data.lore)
            addPDC(data.id)
            builder(this)
        }
    }

    fun getItem(amount: Int = 1): ItemStack {
        return itemStack.clone().apply { this.amount = amount }
    }

    companion object {
        private val REGISTRY = HashMap<String, DewItem>()

        const val R0 = "§8稀有度:零" // 可以直接拿去商店卖钱，有些甚至可以卖到高价
        const val R1 = "§f稀有度:★" // 随处可见的一般物品，没什么特别的
        const val R2 = "§a稀有度:★★" // 便宜好取得的道具或装备，商店都可以买得到
        const val R3 = "§9稀有度:★★★" // 稍微有些价值，可在商店购买或从敌人身上取得
        const val R4 = "§5稀有度:★★★★" // 贵重的装备或道具，要经过一番努力才有可能获得
        const val R5 = "§e稀有度:★★★★★" // 珍稀的物品，往往在一些特殊地点才会产出
        const val R6 = "§4稀有度:★★★★★★" // 传说中的道具，有缘者才可获得
        const val RS = "§b稀有度:特殊" // 任务用道具或特殊功能物品，请好好保管

        // --- 静态注册示例 ---
        val APPLE = reg(Material.APPLE, "§f苹果", "apple",R0, "§7§o普通的苹果")

        val GOLD_APPLE = register(ItemData(
            material = Material.GOLDEN_APPLE,
            displayName = "§a金苹果",
            id = "gold_apple",
            lore = listOf(R2,"§7§o据要有很好牙口才能吃下去")
        ))

        /**
         * 内部注册简写工具
         */
        fun reg(
            mat: Material,
            name: String,
            id: String,
            vararg lore: String,
            builder: taboolib.platform.util.ItemBuilder.() -> Unit = {}
        ): DewItem {
            return register(DewItem(ItemData(mat, name, id, lore.toList()), builder))
        }

        /**
         * 基础注册方法 (核心)
         */
        fun register(item: DewItem): DewItem {
            REGISTRY[item.data.id.lowercase()] = item
            return item
        }

        /**
         * 兼容旧的 ItemData 注册方式
         * 修复点：删除了重复的同名函数，只保留这一个
         */
        fun register(data: ItemData): DewItem {
            return register(DewItem(data))
        }

        fun fromId(id: String): DewItem? = REGISTRY[id.lowercase()]

        fun values(): Collection<DewItem> = REGISTRY.values
    }
}