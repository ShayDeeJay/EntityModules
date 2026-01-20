package org.shaydee.entitymodules.block.entity_module

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.targeting.TargetingConditions
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.DispenserBlock.TRIGGERED
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB
import org.shaydee.entitymodules.block.SyncedBlockEntity
import org.shaydee.entitymodules.registry.EMRegistries

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
    var xSize = 1
    var ySize = 1
    var zSize = 1

    fun tick(level: Level, pos: BlockPos, state: BlockState) {
        println(north)
        println(south)
        println(east)
        println(west)
        println(up)
        println(down)

        level.setBlockAndUpdate(blockPos, state.setValue(TRIGGERED, !getEntities().isEmpty()))
    }

    fun resetBounding(){
        resetSize()
        resetOffset()
    }

    fun resetSize(){
        xSize = 1
        ySize = 1
        zSize = 1
    }

    fun resetOffset(){
        north = 0
        south = 0
        east = 0
        west = 0
        up = 0
        down = 0
    }

    fun getEntities(): List<LivingEntity?> {
        val getLevel = this.level ?: return listOf()

        return getLevel.getNearbyEntities(
            Player::class.java,
            TargetingConditions.DEFAULT,
            null,
            getInflate()
        ).toList()
    }

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

    fun getInflate(): AABB {
        val pos1 = this.worldPosition
            .above(up)
            .below(down)
            .north(north)
            .south(south)
            .east(east)
            .west(west)
        val aabb = AABB(pos1)

        return aabb
            .setMinX(aabb.minX + this.xSize)
            .setMaxX(aabb.maxX - this.xSize)
            .setMinY(aabb.minY + this.ySize)
            .setMaxY(aabb.maxY - this.ySize)
            .setMinZ(aabb.minZ + this.zSize)
            .setMaxZ(aabb.maxZ - this.zSize)
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        tag.putInt("north", north)
        tag.putInt("south", south)
        tag.putInt("east", east)
        tag.putInt("west", west)
        tag.putInt("up", up)
        tag.putInt("down", down)

        tag.putInt("xSize", xSize)
        tag.putInt("ySize", ySize)
        tag.putInt("zSize", zSize)
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)
        north = tag.getInt("north")
        south = tag.getInt("south")
        east = tag.getInt("east")
        west = tag.getInt("west")
        up = tag.getInt("up")
        down = tag.getInt("down")

        xSize = tag.getInt("xSize")
        ySize = tag.getInt("ySize")
        zSize = tag.getInt("zSize")
    }

}