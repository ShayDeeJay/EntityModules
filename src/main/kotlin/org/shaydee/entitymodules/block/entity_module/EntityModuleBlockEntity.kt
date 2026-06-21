package org.shaydee.entitymodules.block.entity_module

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.DispenserBlock.TRIGGERED
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB
import org.shaydee.entitymodules.data.EMType
import org.shaydee.entitymodules.data.FunctionType
import org.shaydee.entitymodules.item.Function
import org.shaydee.entitymodules.item.Module
import org.shaydee.entitymodules.registry.EMRegistries
import org.shaydee.shaydeeapi.Helpers
import org.shaydee.shaydeeapi.Helpers.sLevel
import org.shaydee.shaydeeapi.block.AbstractBEInventory
import org.shaydee.shaydeeapi.helpers.BlockHelpers

class EntityModuleBlockEntity(
    pos: BlockPos,
    blockState: BlockState,
    var tempExpStore: Int = 0,
    var externalBoundingPos: BlockPos = FAKE_POS,
    private var north: Int = 0,
    private var south: Int = 0,
    private var east: Int = 0,
    private var west: Int = 0,
    private var up: Int = 0,
    private var down: Int = 0,
    private var xSize: Int = 1,
    private var ySize: Int = 1,
    private var zSize: Int = 1,
) : AbstractBEInventory(EMRegistries.ENTITY_MODULE_BE, pos, blockState, 1) {

    override fun setInputSlots(): Int = 10

    override fun setOutputSlots(): Int = 0

    override fun getMaxSlotSizeInput(): Int = 1

    override fun getMaxSlotSizeOutput(): Int = 1

    fun getModule(): ItemStack = inputItemHandler.getStackInSlot(0)

    fun getFunction(): ItemStack = inputItemHandler.getStackInSlot(1)

    fun getExternalBounding() = getLevel()?.getBlockEntity(externalBoundingPos) as? EntityModuleBlockEntity

    private fun detectRedstone(level: ServerLevel, pos: BlockPos, inflate: AABB, eMType: EMType, ) {
        val blockState = level.getBlockState(pos)
        level.setBlockAndUpdate(pos, blockState.setValue(TRIGGERED, !getEntities(level, inflate, eMType.clazz).isEmpty()))
    }

    fun resetBounding(){
        resetSize()
        resetOffset()
    }

    fun getEMType(): EMType? {
        val item = getModule().item as? Module
        return item?.getType
    }

    fun getFunctionType(): FunctionType? {
        val item = getFunction().item as? Function
        return item?.getType
    }

    fun tick(level: Level, pos: BlockPos, state: BlockState) {
        val getLevel = level.sLevel() ?: return
        getEMType()?.let { eMType ->
            detectRedstone(getLevel, pos, getInflate(), eMType)
            getFunctionType()?.execute(this, getLevel, eMType)
        }
    }

    fun resetSize(){
        xSize = 1
        ySize = 1
        zSize = 1
    }

    fun adjustSize(x: Int, y: Int, z: Int){
        xSize += x
        ySize += y
        zSize += z
    }

    fun resetOffset(){
        north = 0
        south = 0
        east = 0
        west = 0
        up = 0
        down = 0
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

    fun getInflate(): AABB {
        getExternalBounding()?.let {
            return it.getInflate()
        }

        val pos1 = worldPosition
            .above(up)
            .below(down)
            .north(north)
            .south(south)
            .east(east)
            .west(west)

        return Helpers.getInflate(pos1, xSize, ySize, zSize)
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

        tag.putInt("tempExpStore", tempExpStore)
        externalBoundingPos?.let { BlockHelpers.saveBlockPosNBT(tag, it) }
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

        tempExpStore = tag.getInt("tempExpStore")
        externalBoundingPos = BlockHelpers.loadBlockPosNBT(tag)
    }

    companion object {
        val FAKE_POS = BlockPos(0, -100, 0)

        fun getEntities(level: Level, inflate: AABB, clazz: Class<*>): List<Entity?> {
            return level.getEntities(null, inflate).filter { clazz.isInstance(it) }
        }
    }

}