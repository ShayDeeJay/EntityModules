package org.shaydee.entitymodules.block.entity_module

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
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
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import org.shaydee.entitymodules.helpers.EMHelpers
import org.shaydee.entitymodules.registry.EMRegistries

class EntityModuleBlock(properties: Properties = Properties.of().noOcclusion()) : BaseEntityBlock(properties) {

    companion object {
        val SHAPE: VoxelShape = box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0)
    }

    init {
        this.registerDefaultState(this.stateDefinition.any().setValue(TRIGGERED, false))
    }

    override fun getRenderShape(blockState: BlockState): RenderShape =
        RenderShape.MODEL

    override fun codec(): MapCodec<out BaseEntityBlock?> =
        simpleCodec { EntityModuleBlock() }

    override fun isSignalSource(state: BlockState) =
        true

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        builder.add(TRIGGERED)
    }

    override fun useWithoutItem(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hitResult: BlockHitResult,
    ): InteractionResult {
        val geEntity = EMHelpers.getEntityModule(level, pos) ?: return super.useWithoutItem(state, level, pos, player, hitResult)

        println(geEntity.inputItemHandler.slots)
        return InteractionResult.SUCCESS
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
    ) = createTickerHelper(blockEntityType, EMRegistries.ENTITY_MODULE_BE)
    { bLevel, bPos, bState, bEntity -> bEntity.tick(bLevel, bPos, bState) }

}