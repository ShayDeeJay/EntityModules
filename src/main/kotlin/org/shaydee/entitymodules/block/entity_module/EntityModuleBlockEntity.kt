package org.shaydee.entitymodules.block.entity_module

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.targeting.TargetingConditions
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB
import org.shaydee.entitymodules.block.SyncedBlockEntity
import org.shaydee.entitymodules.registry.EMRegistries
import kotlin.math.roundToInt

class EntityModuleBlockEntity(
    pos: BlockPos,
    blockState: BlockState,
) : SyncedBlockEntity(EMRegistries.ENTITY_MODULE_BE, pos, blockState) {

    var north = 0
    var south = 0
    var east = 0
    var west = 0
    var up = 0
    var down = 0
    var xSize = 0
    var ySize = 0
    var zSize = 0

    fun tick(level: Level, pos: BlockPos, state: BlockState) {

        for (entity in getEntities(LivingEntity::class.java, level, getInflate(pos))) {
            println(entity?.name ?: "wd")
        }
    }

    fun <T : LivingEntity> getEntities(
        entityClazz: Class<T>,
        level: Level,
        deflate: AABB,
    ): List<LivingEntity?> =
        level.getNearbyEntities(
            entityClazz,
            TargetingConditions.DEFAULT,
            null,
            deflate
        ).toList()

    fun adjustDirection(direction: Direction){
        when(direction){
            Direction.NORTH -> north++
            Direction.SOUTH -> south++
            Direction.EAST -> east++
            Direction.WEST -> west++
            Direction.UP -> up++
            Direction.DOWN -> down++
        }
    }


    fun adjustSize(x: Int, y: Int, z: Int){
        xSize += x
        ySize += y
        zSize += z
    }

    fun getInflate(pos: BlockPos): AABB {
        val pos1 = pos
            .above(up)
            .below(down)
            .north(north)
            .south(south)
            .east(east)
            .west(west)
        return AABB(pos1).inflate(xSize.toDouble(), ySize.toDouble(), zSize.toDouble())
    }

}