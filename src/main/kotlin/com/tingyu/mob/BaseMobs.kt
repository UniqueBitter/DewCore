package com.tingyu.mob

import com.tingyu.command.DewItem
import com.tingyu.player.stat.StatType
import org.bukkit.entity.EntityType
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake

/**
 * 盘灵古域基础怪物注册表
 *
 * 一阶凡妖  (Lv  1-20) : 荒野山猪 / 枯骨游魂 / 腐尸游僵
 * 二阶妖兽  (Lv 20-40) : 幽洞毒蛛 / 赤焰猪灵 / 古域山魈
 * 三阶凶兽  (Lv 40-60) : 铁甲劫兽 / 幻翼夜煞
 * 精　　英             : 盘灵卫灵
 */
object BaseMobs {

    // ====================== 一阶凡妖 ======================

    /** 荒野山猪 — 憨厚的肉盾，无脑冲撞 */
    lateinit var WILD_BOAR: MobProfile

    /** 枯骨游魂 — 飘荡在古域边缘的亡者骸骨，善用远程 */
    lateinit var BONE_GHOST: MobProfile

    /** 腐尸游僵 — 被灵气污染的尸体，皮糙肉厚 */
    lateinit var ROT_ZOMBIE: MobProfile

    // ====================== 二阶妖兽 ======================

    /** 幽洞毒蛛 — 洞穴深处的毒液蜘蛛，擅长施加负面效果 */
    lateinit var VENOM_SPIDER: MobProfile

    /** 赤焰猪灵 — 沾染火焰之气的猪灵，结伴行动极为危险 */
    lateinit var FIRE_PIGLIN: MobProfile

    /** 古域山魈 — 盘踞山岭的掠夺者，擅长远程偷袭 */
    lateinit var MOUNTAIN_DEMON: MobProfile

    // ====================== 三阶凶兽 ======================

    /** 铁甲劫兽 — 身披天然铁甲的巨兽，防御惊人 */
    lateinit var IRON_RAVAGER: MobProfile

    /** 幻翼夜煞 — 夜间飞翔的凶灵，速度极快 */
    lateinit var PHANTOM_DEMON: MobProfile

    // ====================== 精英 ======================

    /** 盘灵卫灵 — 古域深处的守护者，需手动召唤 */
    lateinit var PANLING_WARDEN: MobProfile

    // ====================== 测试 ======================

    /** 试炼木桩 — 用于测试掉落/属性/lore，不会攻击 */
    lateinit var TEST_DUMMY: MobProfile

    // ====================== 初始化 ======================

    @Awake(LifeCycle.ENABLE)
    fun init() {

        // ── 一阶凡妖 ──────────────────────────────────────────

        WILD_BOAR = MobRegistry.register("荒野山猪", EntityType.PIG) {
            name("§e荒野山猪")
            base(StatType.HP,     30.0)
            base(StatType.ATTACK,  3.0)
            xp(15)
            drop(DewItem.COOKED_PORKCHOP, 1..2)
        }

        BONE_GHOST = MobRegistry.register("枯骨游魂", EntityType.SKELETON) {
            name("§7枯骨游魂")
            base(StatType.HP,     25.0)
            base(StatType.ATTACK,  4.0)
            base(StatType.MOVE_SPEED, 0.01)
            xp(18)
        }

        ROT_ZOMBIE = MobRegistry.register("腐尸游僵", EntityType.ZOMBIE) {
            name("§a腐尸游僵")
            base(StatType.HP,     40.0)
            base(StatType.ATTACK,  4.0)
            base(StatType.ARMOR,   1.0)
            xp(20)
        }

        // ── 二阶妖兽 ──────────────────────────────────────────

        VENOM_SPIDER = MobRegistry.register("幽洞毒蛛", EntityType.CAVE_SPIDER) {
            name("§2幽洞毒蛛")
            base(StatType.HP,         60.0)
            base(StatType.ATTACK,      8.0)
            base(StatType.EFFECT_HIT,  3.0)
            xp(45)
        }

        FIRE_PIGLIN = MobRegistry.register("赤焰猪灵", EntityType.ZOMBIFIED_PIGLIN) {
            name("§c赤焰猪灵")
            base(StatType.HP,     80.0)
            base(StatType.ATTACK, 12.0)
            base(StatType.MOVE_SPEED, 0.01)
            xp(55)
        }

        MOUNTAIN_DEMON = MobRegistry.register("古域山魈", EntityType.PILLAGER) {
            name("§6古域山魈")
            base(StatType.HP,     90.0)
            base(StatType.ATTACK, 10.0)
            base(StatType.MOVE_SPEED, 0.01)
            xp(60)
        }

        // ── 三阶凶兽 ──────────────────────────────────────────

        IRON_RAVAGER = MobRegistry.register("铁甲劫兽", EntityType.RAVAGER) {
            name("§8铁甲劫兽")
            base(StatType.HP,              200.0)
            base(StatType.ATTACK,           20.0)
            base(StatType.ARMOR,             6.0)
            base(StatType.ARMOR_TOUGHNESS,   3.0)
            base(StatType.KNOCKBACK_RESIST,  0.5)
            xp(130)
        }

        PHANTOM_DEMON = MobRegistry.register("幻翼夜煞", EntityType.PHANTOM) {
            name("§5幻翼夜煞")
            base(StatType.HP,         150.0)
            base(StatType.ATTACK,      16.0)
            base(StatType.MOVE_SPEED,   0.02)
            xp(100)
        }

        // ── 测试（手动召唤，不接管自然生成）────────────────────────

        TEST_DUMMY = MobRegistry.register("试炼木桩") {
            name("§a§l[测试] §f试炼木桩")
            base(StatType.HP,     100.0)
            base(StatType.ATTACK,   0.0)
            drop("warrior_chest_metal_1")
            drop("warrior_sword_fire_1")
            drop("test_chest_earth_6")
            drop("test_sword_fire_6")
            xp(0)
            spawn(EntityType.COW)
        }

        // ── 精英（entityType = null，需手动 MobManager.apply 召唤）────

        PANLING_WARDEN = MobRegistry.register("盘灵卫灵") {
            name("§b§l盘灵卫灵")
            base(StatType.HP,              2000.0)
            base(StatType.ATTACK,            45.0)
            base(StatType.ARMOR,             20.0)
            base(StatType.ARMOR_TOUGHNESS,    8.0)
            base(StatType.KNOCKBACK_RESIST,   1.0)
            xp(500)
            spawn(EntityType.WARDEN)
        }
    }
}
