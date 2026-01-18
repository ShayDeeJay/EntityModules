package org.shaydee.entitymodules.block

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.Connection
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

open class SyncedBlockEntity(
    type: BlockEntityType<*>,
    pos: BlockPos,
    blockState: BlockState,
) : BlockEntity(type, pos, blockState) {
    var privateTicks : Int = 0

    override fun getUpdatePacket(): ClientboundBlockEntityDataPacket? {
        return ClientboundBlockEntityDataPacket.create(this)
    }

    override fun getUpdateTag(registries: HolderLookup.Provider): CompoundTag {
        val tag = CompoundTag()
        this.saveAdditional(tag, registries)
        return tag
    }

    fun updateBlock() {
        val level = getLevel()
        if (level != null) {
            val state = level.getBlockState(worldPosition)
            level.sendBlockUpdated(worldPosition, state, state, 3)
            setChanged()
        }
    }

    override fun onDataPacket(
        net: Connection,
        pkt: ClientboundBlockEntityDataPacket,
        lookupProvider: HolderLookup.Provider,
    ) {
        super.onDataPacket(net, pkt, lookupProvider)
        handleUpdateTag(pkt.tag, lookupProvider)
    }

    override fun saveAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.saveAdditional(tag, registries)
        tag.putInt("private_ticks", privateTicks)
    }

    override fun loadAdditional(tag: CompoundTag, registries: HolderLookup.Provider) {
        super.loadAdditional(tag, registries)
        this.privateTicks = tag.getInt("private_ticks")
    }

}