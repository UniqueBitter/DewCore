package com.tingyu.command

import com.tingyu.item.ItemData
import com.tingyu.item.equip.Affix
import com.tingyu.item.equip.AffixCategory
import com.tingyu.item.equip.EquipData
import com.tingyu.item.equip.EquipLoreBuilder
import com.tingyu.item.equip.FiveElement
import com.tingyu.item.equip.SpiritSlot
import com.tingyu.item.equip.SpiritSlotType
import com.tingyu.item.util.AddPDC.addEquipPDC
import com.tingyu.item.util.AddPDC.addPDC
import com.tingyu.item.util.ForgeRecipe.hideAllCompat
import com.tingyu.player.job.Job
import org.bukkit.Material
import org.bukkit.inventory.EquipmentSlot
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
            data.equipData?.let { addEquipPDC(it) }
            builder(this)
        }.also { item ->
            val meta = item.itemMeta
            if (meta != null) {
                meta.setMaxStackSize(maxStackSize)
                item.itemMeta = meta
            }
        }
    }

    fun maxStack(size: Int): DewItem {
        this.maxStackSize = size
        return this
    }

    fun getItem(amount: Int = 1): ItemStack =
        itemStack.clone().apply { this.amount = amount }

    companion object {

        private val REGISTRY = HashMap<String, DewItem>()

        // ======================== 稀有度常量 ========================

        const val R0         = "§8稀有度:零"
        const val R1         = "§f稀有度:★"
        const val R2         = "§a稀有度:★★"
        const val R3         = "§9稀有度:★★★"
        const val R4         = "§5稀有度:★★★★"
        const val R5         = "§e稀有度:★★★★★"
        const val R6         = "§4稀有度:★★★★★★"
        const val R6_LIMITED = "§c稀有度:★★★★★★ [唯一]"
        const val RS         = "§b稀有度:特殊"

        // ======================== 职业/等级限制常量 ========================

        const val LIMIT_WARRIOR   = "§6限制职业:[战]"
        const val LIMIT_ARCHER    = "§6限制职业:[弓]"
        const val LIMIT_ALCHEMIST = "§6限制职业:[丹]"
        const val RECOMMEND_ALL   = "§6推荐职业:[战] [弓] [丹]"

        const val LIMIT_LVL_0  = "§6限制等级:§e无"
        const val LIMIT_LVL_10 = "§6限制等级:§e10"
        const val LIMIT_LVL_20 = "§6限制等级:§e20"
        const val LIMIT_LVL_30 = "§6限制等级:§e30"
        const val LIMIT_LVL_40 = "§6限制等级:§e40"
        const val LIMIT_LVL_50 = "§6限制等级:§e50"

        // ======================== 食物 ========================

        val APPLE = reg(Material.APPLE, "§f苹果", "apple", R0, "§7§o普通的苹果")
        val COOKED_PORKCHOP = reg(Material.COOKED_PORKCHOP, "§f熟猪排", "cooked_porkchop", R0, "§7§o香喷喷的，还冒着热气")
        val GOLD_APPLE = register(ItemData(
            material    = Material.GOLDEN_APPLE,
            displayName = "§a金苹果",
            id          = "gold_apple",
            lore        = listOf(R2, "§7§o据要有很好牙口才能吃下去")
        ))

        // ======================== 护甲 ========================

        val WARRIOR_CHEST_METAL_1: DewItem = run {
            val d = EquipData(
                job = Job.WARRIOR, tier = 1, slot = EquipmentSlot.CHEST, element = FiveElement.METAL,
                affixes = listOf(
                    Affix(AffixCategory.ZHUANG_JIA, "不侵", 1),
                    Affix(AffixCategory.SHEN_YUN,   "守御", 1),
                ),
                description  = "以玄金铸就的战铠，坚不可摧。",
                uniqueEffect = listOf("cd20s: 受到致命伤害后", "进攻属性增加 §e8%§8，持续 §f3s"),
            )
            register(ItemData(
                material    = Material.IRON_CHESTPLATE,
                displayName = "${FiveElement.METAL.color}金·战铠 §81阶",
                id          = "warrior_chest_metal_1",
                lore        = EquipLoreBuilder.build(d),
                equipData   = d
            )) { hideAllCompat() }
        }

        val RANGER_LEGS_WOOD_1: DewItem = run {
            val d = EquipData(Job.RANGER, 1, EquipmentSlot.LEGS, FiveElement.WOOD)
            register(ItemData(
                material    = Material.LEATHER_LEGGINGS,
                displayName = "${FiveElement.WOOD.color}木·疾风腿 §81阶",
                id          = "ranger_legs_wood_1",
                lore        = EquipLoreBuilder.build(d),
                equipData   = d
            )) { hideAllCompat() }
        }

        val ALCHEMIST_HELM_WATER_1: DewItem = run {
            val d = EquipData(Job.ALCHEMIST, 1, EquipmentSlot.HEAD, FiveElement.WATER)
            register(ItemData(
                material    = Material.LEATHER_HELMET,
                displayName = "${FiveElement.WATER.color}水·丹冠 §81阶",
                id          = "alchemist_helm_water_1",
                lore        = EquipLoreBuilder.build(d),
                equipData   = d
            )) { hideAllCompat() }
        }

        val UNIVERSAL_BOOTS_EARTH_1: DewItem = run {
            val d = EquipData(null, 1, EquipmentSlot.FEET, FiveElement.EARTH)
            register(ItemData(
                material    = Material.GOLDEN_BOOTS,
                displayName = "${FiveElement.EARTH.color}土·稳步靴 §81阶",
                id          = "universal_boots_earth_1",
                lore        = EquipLoreBuilder.build(d),
                equipData   = d
            )) { hideAllCompat() }
        }

        // ======================== 测试专用装备 ========================

        val TEST_CHEST_EARTH_6: DewItem = run {
            val d = EquipData(
                job = Job.WARRIOR, tier = 6, slot = EquipmentSlot.CHEST, element = FiveElement.EARTH,
                description = "测试专用，需战士职业。\n预期: 护甲+9  生命+36  进攻+2.76",
                tierDescription = "测试专用",
            )
            register(ItemData(
                material    = Material.NETHERITE_CHESTPLATE,
                displayName = "§a§l绝对防甲 §8[测试]",
                id          = "test_chest_earth_6",
                lore        = EquipLoreBuilder.build(d),
                equipData   = d
            )) { hideAllCompat() }
        }

        val TEST_SWORD_FIRE_6: DewItem = run {
            val d = EquipData(
                job = Job.WARRIOR, tier = 6, slot = EquipmentSlot.HAND, element = FiveElement.FIRE,
                description = "测试专用，需战士职业。\n预期: 进攻属性+31.2 → 写入 AttackDamage",
                tierDescription = "测试专用",
            )
            register(ItemData(
                material    = Material.NETHERITE_SWORD,
                displayName = "§c§l绝对攻刃 §8[测试]",
                id          = "test_sword_fire_6",
                lore        = EquipLoreBuilder.build(d),
                equipData   = d
            )) { hideAllCompat() }
        }

        // ======================== 武器 ========================

        val WARRIOR_SWORD_FIRE_1: DewItem = run {
            val d = EquipData(
                job = Job.WARRIOR, tier = 1, slot = EquipmentSlot.HAND, element = FiveElement.FIRE,
                tierDescription = "初阶锻造·凡品",
                affixes = listOf(Affix(AffixCategory.LING_QI, "烈刃", 1)),
                description  = "以赤铁铸就，浸透火灵之气，斩击时迸发焰芒。",
                branchEffect = listOf(
                    "§c[斩击] §7普通攻击附带 §e火焰溅射§7，伤害+8%",
                    "§c[突刺] §7冲刺攻击触发 §e穿透§7，无视10%护甲",
                ),
                uniqueEffect = listOf("cd12s: 连续命中3次后", "进攻属性增加 §e20%§8，持续 §f3s"),
                passiveEffect = listOf("§7命中时有 §e12% §7概率触发 §c灼烧§7，持续3s"),
                spiritSlots = listOf(
                    SpiritSlot(SpiritSlotType.ATTACK),
                    SpiritSlot(SpiritSlotType.SPECIAL),
                ),
            )
            register(ItemData(
                material    = Material.IRON_SWORD,
                displayName = "${FiveElement.FIRE.color}火·战刃 §81阶",
                id          = "warrior_sword_fire_1",
                lore        = EquipLoreBuilder.build(d),
                equipData   = d
            )) { hideAllCompat() }
        }

        // ======================== 注册方法 ========================

        fun reg(
            mat: Material,
            name: String,
            id: String,
            vararg lore: String,
            builder: ItemBuilder.() -> Unit = {}
        ): DewItem = register(DewItem(ItemData(mat, name, id, lore.toList()), builder))

        fun register(item: DewItem): DewItem {
            REGISTRY[item.data.id.lowercase()] = item
            return item
        }

        fun register(data: ItemData): DewItem = register(DewItem(data))

        fun register(data: ItemData, builder: ItemBuilder.() -> Unit): DewItem =
            register(DewItem(data, builder))

        fun fromId(id: String): DewItem? = REGISTRY[id.lowercase()]

        fun values(): Collection<DewItem> = REGISTRY.values
    }
}
