package org.shaydee.entitymodules.data

import net.minecraft.world.entity.ExperienceOrb
import net.minecraft.world.entity.NeutralMob
import net.minecraft.world.entity.animal.Animal
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.entity.player.Player

enum class EMType(val clazz: Class<*>){
    HOSTILE_MOB(Enemy::class.java),
    PASSIVE_MOB(Animal::class.java),
    NEUTRAL_MOB(NeutralMob::class.java),
    PLAYER(Player::class.java),
    ITEM(ItemEntity::class.java),
    EXPERIENCE(ExperienceOrb::class.java)
}