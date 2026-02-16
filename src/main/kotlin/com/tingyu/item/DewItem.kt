package com.tingyu.command

import com.tingyu.item.ItemData
import com.tingyu.item.util.AddPDC.addPDC
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import taboolib.platform.util.ItemBuilder
import taboolib.platform.util.buildItem

class DewItem private constructor(
    val data: ItemData,
    private val builder: ItemBuilder.() -> Unit = {}
) {
    var maxStackSize: Int? = 64


    val itemStack: ItemStack by lazy {

        buildItem(data.material) {
            name = data.displayName
            this.lore.addAll(data.lore)
            addPDC(data.id)
            builder(this)
        }.also { item ->
            val meta = item.itemMeta
            if (meta != null) {
                meta.setMaxStackSize(maxStackSize)
                item.itemMeta = meta
            }
        }
    }

    /**
     * 链式设置最大堆叠数 (1.21+)
     * 用法: DewItem.reg(...).maxStack(99)
     */
    fun maxStack(size: Int): DewItem {
        this.maxStackSize = size
        return this
    }

    fun getItem(amount: Int = 1): ItemStack {
        return itemStack.clone().apply { this.amount = amount }
    }

    companion object {
        private val REGISTRY = HashMap<String, DewItem>()

        const val R0 = "§8稀有度:零"
        const val R1 = "§f稀有度:★"
        const val R2 = "§a稀有度:★★"
        const val R3 = "§9稀有度:★★★"
        const val R4 = "§5稀有度:★★★★"
        const val R5 = "§e稀有度:★★★★★"
        const val R6 = "§4稀有度:★★★★★★"
        const val R6_LIMITED = "§c稀有度:★★★★★★ [唯一]"        // 对应 pl.lore.rare6_limited
        const val RS = "§b稀有度:特殊"
        // 限制职业常量
        const val LIMIT_WARRIOR = "§6限制职业:[战]"
        const val LIMIT_ARCHER = "§6限制职业:[弓]"
        const val LIMIT_ALCHEMIST = "§6限制职业:[丹]"
        const val RECOMMEND_ALL = "§6推荐职业:[战] [弓] [丹]"

        // 限制等级常量
        const val LIMIT_LVL_0  = "§6限制等级:§e无"            // 对应 pl.lore.limit_lvl_0
        const val LIMIT_LVL_10 = "§6限制等级:§e10"              // 对应 pl.lore.limit_lvl_10
        const val LIMIT_LVL_20 = "§6限制等级:§e20"              // 对应 pl.lore.limit_lvl_20
        const val LIMIT_LVL_30 = "§6限制等级:§e30"              // 对应 pl.lore.limit_lvl_30
        const val LIMIT_LVL_40 = "§6限制等级:§e40"              // 对应 pl.lore.limit_lvl_40
        const val LIMIT_LVL_50 = "§6限制等级:§e50"              // 对应 pl.lore.limit_lvl_50



        val APPLE = reg(Material.APPLE, "§f苹果", "apple", R0, "§7§o普通的苹果")
        val GOLD_APPLE = register(
            ItemData(
                material = Material.GOLDEN_APPLE,
                displayName = "§a金苹果",
                id = "gold_apple",
                lore = listOf(R2, "§7§o据要有很好牙口才能吃下去")
            )
        )

        fun reg(
            mat: Material,
            name: String,
            id: String,
            vararg lore: String,
            builder: ItemBuilder.() -> Unit = {}
        ): DewItem {
            return register(DewItem(ItemData(mat, name, id, lore.toList()), builder))
        }

        fun register(item: DewItem): DewItem {
            REGISTRY[item.data.id.lowercase()] = item
            return item
        }

        fun register(data: ItemData): DewItem {
            return register(DewItem(data))
        }

        fun fromId(id: String): DewItem? = REGISTRY[id.lowercase()]

        fun values(): Collection<DewItem> = REGISTRY.values
    }
}