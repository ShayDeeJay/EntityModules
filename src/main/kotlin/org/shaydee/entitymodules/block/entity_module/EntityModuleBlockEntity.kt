package org.shaydee.entitymodules.block.entity_module

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.animal.Animal
import net.minecraft.world.entity.monster.Enemy
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.DispenserBlock.TRIGGERED
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.energy.ComponentEnergyStorage
import org.shaydee.entitymodules.registry.EMRegistries
import org.shaydee.shaydeeapi.Helpers
import org.shaydee.shaydeeapi.block.AbstractBEInventory

class EntityModuleBlockEntity(
    pos: BlockPos,
    blockState: BlockState,
) : AbstractBEInventory(EMRegistries.ENTITY_MODULE_BE, pos, blockState, 1) {

    var north = 0
    var south = 0
    var east = 0
    var west = 0
    var up = 0
    var down = 0
    var xSize = 1
    var ySize = 1
    var zSize = 1

    enum class EMType(val clazz: Class<*>){
        MONSTER(Enemy::class.java),
        PASSIVE(Animal::class.java),
        PLAYER(Player::class.java)
    }

    fun tick(level: Level, pos: BlockPos, state: BlockState) {
        if(level is ServerLevel){
            teleportEntity(level, EMType.MONSTER.clazz)
//            outputEntityDetect(level, state, clazz)
        }
    }

    private fun teleportEntity(level: ServerLevel, clazz: Class<*>){
        val center = this.blockPos.center
        getEntities(clazz).forEach {
            it?.teleportTo(
                level,
                center.x,
                center.y - it.bbHeight - 0.5,
                center.z,
                mutableSetOf(),
                it.yRot,
                it.xRot
            )
        }
    }

    private fun outputEntityDetect(level: Level, state: BlockState, clazz: Class<out Entity>) =
        level.setBlockAndUpdate(blockPos, state.setValue(TRIGGERED, !getEntities(clazz).isEmpty()))

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

    fun getEntities(clazz: Class<*>): List<Entity?> {
        val getLevel = this.level ?: return listOf()

        return getLevel.getEntities(null, getInflate()).filter { clazz.isInstance(it) }
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

        return Helpers.getInflate(pos1, xSize, ySize, zSize)
    }

    override fun setInputSlots(): Int = 10

    override fun setOutputSlots(): Int = 0

    override fun getMaxSlotSizeInput(): Int = 1

    override fun getMaxSlotSizeOutput(): Int = 1

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