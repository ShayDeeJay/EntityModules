package org.shaydee.entitymodules.block.entity_module

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Direction.Axis
import net.minecraft.core.Direction.AxisDirection
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents.*
import net.minecraft.sounds.SoundSource.BLOCKS
import net.minecraft.world.InteractionHand
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.ItemInteractionResult.FAIL
import net.minecraft.world.ItemInteractionResult.SUCCESS
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
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import org.shaydee.entitymodules.helpers.Helpers
import org.shaydee.entitymodules.item.EntityModuleController.Companion.getCurrentMode
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

    override fun useItemOn(
        itemStack: ItemStack,
        state: BlockState,
        level: Level,
        blockPos: BlockPos,
        player: Player,
        hand: InteractionHand,
        blockHitResult: BlockHitResult,
    ): ItemInteractionResult {

        val entity: EntityModuleBlockEntity = Helpers.getEntityModule(level, blockPos) ?: return FAIL
        val direction = blockHitResult.direction
        val axis = direction.axis

        fun getSound(soundEvent: SoundEvent, pitch: Float = 2F) {
            level.playSound(null, blockPos, soundEvent, BLOCKS, 1F, pitch)
            level.playSound(null, blockPos, UI_BUTTON_CLICK.value(), BLOCKS, 1F, pitch)
        }

        fun inflateOrReduce(inflate: Boolean) {
            val first = if(inflate) 1 else -1
            val sizeDelta = when (axis) {
                Axis.X -> Triple(first, 0, 0)
                Axis.Y -> Triple(0, first, 0)
                Axis.Z -> Triple(0, 0, first)
            }

            entity.adjustSize(sizeDelta.first, sizeDelta.second, sizeDelta.third)

            if (direction.axis == axis && direction.axisDirection == AxisDirection.NEGATIVE) {
                entity.adjustDirection(if(inflate) direction.opposite else direction)
            }
            getSound(SOUL_ESCAPE.value())
        }

        when(getCurrentMode(itemStack)) {
            1 -> {
                entity.adjustDirection(direction.opposite)
                getSound(SLIME_SQUISH)
            }
            2 -> inflateOrReduce(true)
            3 -> inflateOrReduce(false)
            4 -> {
                entity.resetBounding()
                getSound(BEACON_DEACTIVATE)
            }

        }

        return SUCCESS
    }
}