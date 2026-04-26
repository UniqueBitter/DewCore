# 玩家属性系统文档

## 概述

玩家属性系统由四层结构驱动，每种属性都有独立的计算公式，计算结果自动写入 Minecraft 原版 Attribute。

---

## 核心概念

### 属性四层结构 `StatLayer`

每种属性的值由四个来源叠加：

| 字段 | 含义 | 示例 |
|------|------|------|
| `base` | 基础加成（加在公式基础值上） | 升级获得的生命+50 |
| `percent` | 百分比加成（0.10 = +10%） | 装备"进攻属性+10%" |
| `finalPercent` | 最终百分比乘数（基础层固定 1.0，装备贡献 delta） | 装备"最终生命+5%" → 0.05 |
| `finalFlat` | 最终固定加成 | 装备"生命+30" |

**标准计算公式：**
```
最终值 = (基础值 + base) × (1 + percent) × finalPercent + finalFlat
```

### 属性类型 `StatType`

| 枚举值 | 显示名 | 映射到原版 Attribute |
|--------|--------|----------------------|
| `HP` | 生命值 | `GENERIC_MAX_HEALTH` |
| `ARMOR` | 护甲值 | `GENERIC_ARMOR` |
| `ARMOR_TOUGHNESS` | 护甲韧性 | `GENERIC_ARMOR_TOUGHNESS` |
| `MOVE_SPEED` | 移动速度 | `GENERIC_MOVEMENT_SPEED` |
| `KNOCKBACK_RESIST` | 击退抗性 | `GENERIC_KNOCKBACK_RESISTANCE` |
| `JOB_POWER` | 职业属性 | 无（战斗计算用） |
| `ATTACK` | 进攻属性 | 无（战斗计算用） |
| `ATTACK_SPEED` | 攻击速度 | 无（战斗计算用） |
| `ARROW_SPEED` | 箭矢速度 | 无（战斗计算用） |
| `SKILL_EFFECT` | 技能效果 | 无（战斗计算用） |
| `KNOCKBACK` | 击退距离 | 无（战斗计算用） |
| `EFFECT_HIT` | 效果命中 | 无（战斗计算用） |
| `EFFECT_RESIST` | 效果抵抗 | 无（战斗计算用） |
| `RECOVERY` | 回复效率 | 无（战斗计算用） |

### 各属性计算公式

```
职业属性   = (base × (1+%)) × 最终% + 最终
进攻属性   = (base × (1+%)) × 最终% + 最终
生命值     = ((20+base) × (1+%)) × 最终% + 最终
护甲韧性   = (base × (1+%)) × 最终% + 最终 + 4
回复效率   = √((base × (1+%)) × 最终% + 最终) + 100
移动速度   = ((base+0.1) × (1+%)) × 最终% + 最终
攻击速度   = √(((1+base) × (1+%)) × 最终% + 最终 + 1) - 1
箭矢速度1  = √((3+base) × (1+%)) × 最终% + 最终
箭矢速度2  = (1.5+base) × √(1+%) × 最终% + 最终
```

---

## 玩家档案 `PlayerProfile`

存储玩家的职业、种族、等级和各属性基础值，通过 PDC 自动持久化。

### 字段

| 字段 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| `uuid` | UUID | — | 玩家唯一 ID |
| `job` | Job | `NONE` | 职业（WARRIOR / RANGER / ALCHEMIST） |
| `race` | Race | `NONE` | 种族 |
| `level` | Int | 1 | 等级 |
| `jobPromotion` | Int | 0 | 转职次数 |
| `baseStats` | Map | 空 | 各属性的基础值 |

### API

```kotlin
val profile = PlayerManager.get(player)

// 读取基础值
val hpBase = profile.getBase(StatType.HP)

// 设置基础值（修改后需调用 refresh）
profile.setBase(StatType.HP, 100.0)

// 构建该属性的基础 StatLayer（finalPercent 固定 1.0）
val layer = profile.buildLayer(StatType.HP)
```

---

## 玩家管理 `PlayerManager`

负责加载/存档/缓存玩家档案，并在加入时自动应用属性。

### 事件触发时机

| 事件 | 行为 |
|------|------|
| `PlayerJoinEvent` | 从 PDC 加载档案 → 应用属性到原版 Attribute |
| `PlayerQuitEvent` | 保存档案到 PDC → 清除内存缓存 |
| `WorldSaveEvent` | 保存所有在线玩家档案 |

### API

```kotlin
// 获取玩家档案
val profile = PlayerManager.get(player)
val profile = PlayerManager.get(uuid)  // 离线/已加载的玩家

// 修改属性后刷新（重新计算并写入原版 Attribute）
PlayerManager.refresh(player)

// 立即保存（通常不需要手动调用）
PlayerManager.saveNow(player)

// 计算某属性最终值（可传入装备层）
val finalHp = PlayerManager.computeStat(player, StatType.HP)
val finalHp = PlayerManager.computeStat(player, StatType.HP, listOf(equipLayer1, equipLayer2))
```

---

## 属性应用 `StatApplier`

将计算结果写入 Minecraft 原版 Attribute，通常由 `PlayerManager` 自动调用。

### 手动调用

```kotlin
// 只用玩家档案属性
StatApplier.apply(player)

// 叠加装备层后应用（装备系统完成后使用）
val equipLayers = mapOf(
    StatType.HP    to StatLayer(base = 50.0),
    StatType.ARMOR to StatLayer(percent = 0.20)
)
StatApplier.apply(player, equipLayers)
```

---

## 使用示例

### 给玩家设置初始属性

```kotlin
@SubscribeEvent
fun onJoin(event: PlayerJoinEvent) {
    val profile = PlayerManager.get(event.player)
    if (profile.level == 1 && profile.getBase(StatType.HP) == 0.0) {
        // 新玩家初始属性
        profile.setBase(StatType.HP, 30.0)       // 生命 = (20+30)×1×1 = 50
        profile.setBase(StatType.MOVE_SPEED, 0.0) // 速度 = (0+0.1)×1×1 = 0.1（原版默认）
        PlayerManager.refresh(event.player)
    }
}
```

### 升级时增加属性

```kotlin
fun onLevelUp(player: Player) {
    val profile = PlayerManager.get(player)
    profile.level += 1
    profile.setBase(StatType.HP, profile.getBase(StatType.HP) + 10.0)
    profile.setBase(StatType.ATTACK, profile.getBase(StatType.ATTACK) + 2.0)
    PlayerManager.refresh(player)
}
```

### 读取非原版属性（战斗计算）

```kotlin
// 原版属性直接读 Attribute
val maxHp = player.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value ?: 20.0

// 非原版属性通过 computeStat 计算
val attack = PlayerManager.computeStat(player, StatType.ATTACK)
val jobPower = PlayerManager.computeStat(player, StatType.JOB_POWER)

// 战士平A伤害 = 职业属性 + 进攻属性
val damage = jobPower + attack
```

### 计算击退

```kotlin
// 攻击方击退距离，防御方击退抗性
val knockback = PlayerManager.computeStat(attacker, StatType.KNOCKBACK)
val resist = MobManager.getStat(target, StatType.KNOCKBACK_RESIST)

val x = maxOf(knockback, resist) - minOf(knockback, resist)
val e = Math.E
val y = 1.0 + Math.log(x / (x + e)) / Math.log(1.0 / e)  // y% 即为调整系数
val distance = 1.0 * (y / 100.0 + 1.0) + 0.8
```
