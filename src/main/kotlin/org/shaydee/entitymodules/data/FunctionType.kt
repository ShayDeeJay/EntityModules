package org.shaydee.entitymodules.data

import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.ExperienceOrb
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import org.shaydee.entitymodules.block.entity_module.EntityModuleBlockEntity
import org.shaydee.entitymodules.block.entity_module.EntityModuleBlockEntity.Companion.getEntities
import org.shaydee.shaydeeapi.helpers.BlockHelpers.externalOutputInventory

enum class FunctionType{

    TELEPORT {
        override fun execute(module: EntityModuleBlockEntity, level: ServerLevel, eMType : EMType) {
            getEntities(level, module.getInflate(), eMType.clazz).forEach {
                it?.let {
                    val cPos = module.blockPos.center
                    val x = cPos.x
                    val y = cPos.y + 0.5
                    val z = cPos.z

                    if(it.blockPosition() == module.blockPos.above() || !it.isAlive) return

                    if(it !is Player) {
                        it.unRide()
                        val entity: Entity = it.type.create(level) ?: return@forEach
                        entity.restoreFrom(it)
                        entity.moveTo(x, y, z)
                        entity.yHeadRot = it.yHeadRot
                        it.setRemoved(Entity.RemovalReason.CHANGED_DIMENSION)
                        level.addDuringTeleport(entity)
                    } else {
                        it.teleportTo(x, y, z)
                    }
                }
            }
        }
    },
    INVENTORY {
        override fun execute(module: EntityModuleBlockEntity, level: ServerLevel, eMType: EMType) {
            if(EMType.ITEM.clazz.isAssignableFrom(eMType.clazz)) {
                getEntities(level, module.getInflate(), eMType.clazz).forEach {
                    externalOutputInventory(module.blockPos, Direction.UP.serializedName, level, it as ItemEntity)
                }
            }

            if(EMType.EXPERIENCE.clazz.isAssignableFrom(eMType.clazz)) {
                getEntities(level, module.getInflate(), eMType.clazz).forEach { xp ->
                    module.tempExpStore += (xp as ExperienceOrb).value
                    xp.remove(Entity.RemovalReason.CHANGED_DIMENSION)
                }

                repeat(module.tempExpStore / 10) {
                    val itemEntity = ItemEntity(level, .0, .0, .0, ItemStack(Items.EXPERIENCE_BOTTLE))
                    if(externalOutputInventory(module.blockPos, Direction.UP.serializedName, level, itemEntity)){
                        module.tempExpStore -= 10
                    }
                }
            }
        }
    };

    abstract fun execute(module: EntityModuleBlockEntity, level: ServerLevel, eMType : EMType)

}

