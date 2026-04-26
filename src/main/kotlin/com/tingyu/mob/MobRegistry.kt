package com.tingyu.mob

import org.bukkit.entity.EntityType

object MobRegistry {

    private val byId = HashMap<String, MobProfile>()
    private val byEntityType = HashMap<EntityType, MobProfile>()

    fun register(profile: MobProfile): MobProfile {
        byId[profile.id] = profile
        profile.entityType?.let { byEntityType[it] = profile }
        return profile
    }

    /**
     * DSL 注册，不绑定 EntityType（需手动调用 MobManager.apply）：
     *   MobRegistry.register("boss_zombie") { base(StatType.HP, 200.0) }
     *
     * DSL 注册并绑定 EntityType（自然生成时自动应用）：
     *   MobRegistry.register("zombie_warrior", EntityType.ZOMBIE) { base(StatType.HP, 80.0) }
     */
    fun register(
        id: String,
        entityType: EntityType? = null,
        builder: MobProfile.() -> Unit
    ): MobProfile = register(MobProfile(id, entityType).apply(builder))

    fun fromId(id: String): MobProfile? = byId[id]

    fun fromEntityType(type: EntityType): MobProfile? = byEntityType[type]

    fun values(): Collection<MobProfile> = byId.values
}
