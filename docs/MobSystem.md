# 怪物属性系统文档

## 概述

怪物使用与玩家相同的属性公式（StatType / StatLayer / StatCalculator），但属性在注册时写死，不需要存档。原版属性写入 `LivingEntity` 的 Attribute，非原版属性缓存在内存中供战斗计算使用。

---

## 核心组件

### `MobProfile` — 怪物属性定义

定义某种怪物的所有属性层。

```kotlin
MobProfile(id: String, entityType: EntityType? = null)
```

| 参数 | 说明 |
|------|------|
| `id` | 唯一字符串 ID，写入实体 PDC |
| `entityType` | 绑定的原版实体类型；非 null 时自然生成自动应用，null 时只能手动应用 |

**方法：**

```kotlin
// 设置基础值（最常用）
profile.base(StatType.HP, 80.0)

// 设置完整四层
profile.set(
    type        = StatType.HP,
    base        = 80.0,   // 基础加成
    percent     = 0.20,   // +20% 百分比
    finalPercent = 1.0,   // 最终乘数（默认 1.0）
    finalFlat   = 0.0     // 最终固定加成
)
```

---

### `MobRegistry` — 注册中心

统一管理所有怪物 Profile，支持按 ID 或 EntityType 查找。

#### 注册

```kotlin
// 绑定 EntityType：自然生成时自动应用
MobRegistry.register("zombie_warrior", EntityType.ZOMBIE) {
    base(StatType.HP, 80.0)
    base(StatType.ATTACK, 15.0)
    base(StatType.ARMOR, 6.0)
}

// 不绑定 EntityType：仅手动应用（Boss、特殊怪等）
MobRegistry.register("dungeon_boss") {
    base(StatType.HP, 500.0)
    base(StatType.ATTACK, 60.0)
    set(StatType.KNOCKBACK_RESIST, base = 0.0, finalFlat = 0.8)
}
```

> **注意：** 一个 `EntityType` 只能绑定一个 Profile。
> 同类型的分阶怪物（如三阶/五阶僵尸）请使用手动 apply 或按区域分配逻辑。

#### 查找

```kotlin
MobRegistry.fromId("zombie_warrior")         // 按 ID 查找
MobRegistry.fromEntityType(EntityType.ZOMBIE) // 按实体类型查找
MobRegistry.values()                          // 所有已注册 Profile
```

---

### `MobManager` — 运行时管理

负责监听生成/死亡事件，应用属性，缓存非原版属性值。

#### 事件触发时机

| 事件 | 行为 |
|------|------|
| `CreatureSpawnEvent` | 查 Registry → 有绑定则自动 apply |
| `EntityDeathEvent` | 清除该实体的内存缓存 |
| `ChunkUnloadEvent` | 清除该区块内所有实体的缓存 |

#### 属性存储位置

| 属性类型 | 存储位置 | 读取方式 |
|----------|----------|----------|
| HP / 护甲 / 速度 / 击退抗性 | 原版 `AttributeInstance` | `entity.getAttribute(Attribute.XXX)` |
| 职业属性 / 进攻 / 技能效果等 | 内存 `MobManager.cache` | `MobManager.getStat(entity, StatType.XXX)` |
| 怪物身份 ID | 实体 PDC `dew:mob_id` | `MobManager.getMobId(entity)` |

---

## API 参考

### 手动应用属性

```kotlin
val entity = world.spawnEntity(location, EntityType.ZOMBIE) as LivingEntity
val profile = MobRegistry.fromId("dungeon_boss") ?: return
MobManager.apply(entity, profile)
```

### 读取属性值

```kotlin
// 原版属性（生命上限、护甲等）
val maxHp   = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value ?: 20.0
val armor   = entity.getAttribute(Attribute.GENERIC_ARMOR)?.value ?: 0.0

// 非原版属性（进攻、技能等）
val attack     = MobManager.getStat(entity, StatType.ATTACK)
val jobPower   = MobManager.getStat(entity, StatType.JOB_POWER)
val effectHit  = MobManager.getStat(entity, StatType.EFFECT_HIT)
```

### 判断是否由 DewCore 管理

```kotlin
if (MobManager.isManaged(entity)) {
    // 该实体应用了 DewCore 属性系统
}
```

---

## 注册示例

建议在某个 `@Awake(LifeCycle.ENABLE)` 的 object 中统一注册：

```kotlin
object MobStats {
    @Awake(LifeCycle.ENABLE)
    fun init() {

        // ===== 普通怪物 =====

        MobRegistry.register("zombie_basic", EntityType.ZOMBIE) {
            base(StatType.HP, 30.0)          // 生命 = 20+30 = 50
            base(StatType.ATTACK, 5.0)       // 进攻属性 = 5
        }

        MobRegistry.register("skeleton_basic", EntityType.SKELETON) {
            base(StatType.HP, 20.0)          // 生命 = 40
            base(StatType.ATTACK, 4.0)
            base(StatType.ARROW_SPEED, 1.0)  // 箭矢速度1: √(3+1) = 2.0
        }

        MobRegistry.register("spider_basic", EntityType.SPIDER) {
            base(StatType.HP, 16.0)
            base(StatType.ATTACK, 3.0)
            base(StatType.MOVE_SPEED, 0.2)   // 速度 = 0.1+0.2 = 0.3
        }

        // ===== Boss（手动生成）=====

        MobRegistry.register("dungeon_boss_lv1") {
            set(StatType.HP,     base = 400.0, percent = 0.5)  // (20+400)×1.5 = 630
            base(StatType.ATTACK, 80.0)
            base(StatType.ARMOR, 15.0)
            base(StatType.ARMOR_TOUGHNESS, 6.0)                // 6+4 = 10 韧性
            set(StatType.KNOCKBACK_RESIST, finalFlat = 0.9)    // 击退抗性 = 0.9
            base(StatType.EFFECT_RESIST, 50.0)
        }
    }
}
```

---

## 战斗计算示例

### 伤害计算（玩家攻击怪物）

```kotlin
fun calcDamage(attacker: Player, target: LivingEntity): Double {
    val jobPower = PlayerManager.computeStat(attacker, StatType.JOB_POWER)
    val attack   = PlayerManager.computeStat(attacker, StatType.ATTACK)
    // 战士平A：职业属性 + 进攻属性
    return jobPower + attack
}
```

### 击退计算

```kotlin
fun calcKnockback(attacker: Player, target: LivingEntity): Double {
    val kb      = PlayerManager.computeStat(attacker, StatType.KNOCKBACK)
    val resist  = MobManager.getStat(target, StatType.KNOCKBACK_RESIST)
    val x       = maxOf(kb, resist) - minOf(kb, resist)
    val e       = Math.E
    val y       = 1.0 + Math.log(x / (x + e)) / Math.log(1.0 / e)
    return 1.0 * (y / 100.0 + 1.0) + 0.8
}
```

### 效果命中判断

```kotlin
fun rollEffectHit(attacker: Player, target: LivingEntity): Boolean {
    val hit     = PlayerManager.computeStat(attacker, StatType.EFFECT_HIT)
    val resist  = MobManager.getStat(target, StatType.EFFECT_RESIST)
    val x       = maxOf(hit, resist) - minOf(hit, resist)
    val e       = Math.E
    val y       = 1.0 + Math.log(x / (x + e)) / Math.log(1.0 / e)
    // 基础命中率 60%，受 y 调整后开平方
    val chance  = Math.sqrt(60.0 * (y / 100.0 + 1.0)) / 100.0
    return Math.random() < chance
}
```
