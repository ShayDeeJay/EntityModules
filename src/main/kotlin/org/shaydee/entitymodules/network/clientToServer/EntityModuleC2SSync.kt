package org.shaydee.entitymodules.network.clientToServer

import net.minecraft.core.BlockPos
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.level.ServerLevel
import net.neoforged.neoforge.network.handling.IPayloadContext
import org.shaydee.entitymodules.EntityModules
import org.shaydee.entitymodules.block.entity_module.EntityModuleBlockEntity
import org.shaydee.entitymodules.item.controller.EntityModuleController
import org.shaydee.entitymodules.registry.EMRegistries
import org.shaydee.shaydeeapi.Helpers

class EntityModuleC2SSync(
    val blockPos: BlockPos,
    val scrollDelta: Double
) : CustomPacketPayload {

    companion object {
        @JvmField
        val TYPE = CustomPacketPayload.Type<EntityModuleC2SSync>(
            Helpers.res(EntityModules.ID, "entity_module_sync")
        )

        @JvmField
        val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, EntityModuleC2SSync> =
            CustomPacketPayload.codec(EntityModuleC2SSync::write, ::EntityModuleC2SSync)
    }

    constructor(buf: FriendlyByteBuf) : this(
        blockPos = buf.readBlockPos(),
        scrollDelta = buf.readDouble()
    )

    private fun write(buf: FriendlyByteBuf) {
        buf.writeBlockPos(blockPos)
        buf.writeDouble(scrollDelta)
    }

    fun handle(ctx: IPayloadContext) {
        ctx.enqueueWork {
            val player = ctx.player()
            val level = player.level() as? ServerLevel ?: return@enqueueWork

            val be = level.getBlockEntity(blockPos) as? EntityModuleBlockEntity
                ?: return@enqueueWork

            val mainHandItem = player.mainHandItem
            val item = mainHandItem.item
            if (item !is EntityModuleController) return@enqueueWork

            val boundPos = mainHandItem.get(EMRegistries.ENTITY_MODULE_BLOCKPOS)
                ?: return@enqueueWork

            item.useItemOnC(mainHandItem, level, boundPos, player, scrollDelta)

            be.setChanged()
            level.sendBlockUpdated(blockPos, be.blockState, be.blockState, 3)
        }
    }

    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> = TYPE
}