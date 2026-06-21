package org.shaydee.entitymodules.block.entity_module

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.DispenserBlock.TRIGGERED
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import org.shaydee.entitymodules.helpers.EMHelpers.getEntityModule
import org.shaydee.entitymodules.item.Function
import org.shaydee.entitymodules.item.Module
import org.shaydee.entitymodules.registry.EMRegistries
import org.shaydee.shaydeeapi.data.MultiSound
import org.shaydee.shaydeeapi.helpers.BlockHelpers
import org.shaydee.shaydeeapi.helpers.SoundHelpers

class EntityModuleBlock(
    properties: Properties = Properties.of().strength(0.3F).noOcclusion(),
) : BaseEntityBlock(properties) {

    companion object {
        val SHAPE: VoxelShape = box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0)
        val INVISIBLE: BooleanProperty = BooleanProperty.create("invisible")
    }

    init {
        this.registerDefaultState(
            this.stateDefinition.any()
                .setValue(INVISIBLE, false)
                .setValue(TRIGGERED, false)
        )
    }

    override fun isSignalSource(state: BlockState) = true

    override fun getRenderShape(blockState: BlockState): RenderShape =
        if(blockState.getValue(INVISIBLE)) RenderShape.INVISIBLE else RenderShape.MODEL

    override fun codec(): MapCodec<out BaseEntityBlock?> =
        simpleCodec { EntityModuleBlock() }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        builder.add(TRIGGERED).add(INVISIBLE)
    }

    override fun getSignal(
        state: BlockState,
        blockGetter: BlockGetter,
        pos: BlockPos,
        direction: Direction,
    ) = if(state.getValue(TRIGGERED)) 15 else 0

    override fun newBlockEntity(
        bPos: BlockPos,
        bState: BlockState,
    ) = EntityModuleBlockEntity(bPos, bState)

    override fun getShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext,
    ) = SHAPE

    override fun <T : BlockEntity?> getTicker(
        level: Level,
        state: BlockState,
        blockEntityType: BlockEntityType<T?>,
    ) = createTickerHelper(blockEntityType, EMRegistries.ENTITY_MODULE_BE) {
        bLevel, bPos, bState, bEntity -> bEntity.tick(bLevel, bPos, bState)
    }

    override fun onRemove(
        state: BlockState,
        level: Level,
        blockPos: BlockPos,
        blockState: BlockState,
        piston: Boolean,
    ) {
        if (state.block !== blockState.block) {
            val getModule = level.getEntityModule(blockPos) ?: return
            getModule.dropsAllInventory(level)
        }
        super.onRemove(state, level, blockPos, blockState, piston)
    }

    override fun useItemOn(
        stack: ItemStack,
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hitResult: BlockHitResult,
    ): ItemInteractionResult {
        val getModuleBlock = level.getEntityModule(pos) ?: return ItemInteractionResult.FAIL
        val insertModule = stack.item is Module
        val removeModule = stack.isEmpty && !getModuleBlock.getModule().isEmpty
        val insertFunction = stack.item is Function
        val removeFunction = stack.isEmpty && !getModuleBlock.getFunction().isEmpty

        fun doByType(isType: Boolean, slotId: Int) {
            val handler = getModuleBlock.inputItemHandler
            val insert = MultiSound(SoundEvents.VAULT_INSERT_ITEM)
            val remove = MultiSound(SoundEvents.VAULT_EJECT_ITEM)

            BlockHelpers.swapItemsWithHand(handler, slotId, player, hand)
            SoundHelpers.multiSound(level, pos.center, SoundSource.BLOCKS, if(isType) insert else remove)
        }

        if(insertFunction || removeFunction) {
            doByType(insertFunction, 1)
            return ItemInteractionResult.SUCCESS
        }

        if(insertModule || removeModule) {
            doByType(insertModule, 0)
            return ItemInteractionResult.SUCCESS
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult)
    }

}