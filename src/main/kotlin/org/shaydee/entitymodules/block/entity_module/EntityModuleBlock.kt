package org.shaydee.entitymodules.block.entity_module

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.InteractionHand
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape
import org.shaydee.entitymodules.registry.EMRegistries

class EntityModuleBlock(properties: Properties = Properties.of().noOcclusion()) : BaseEntityBlock(properties) {

    companion object {
        val SHAPE: VoxelShape = box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0)
    }

    override fun getRenderShape(blockState: BlockState): RenderShape =
        RenderShape.MODEL

    override fun codec(): MapCodec<out BaseEntityBlock?> =
        simpleCodec { EntityModuleBlock() }

    override fun newBlockEntity(
        bPos: BlockPos,
        bState: BlockState,
    ): BlockEntity? = EntityModuleBlockEntity(bPos, bState)

    override fun getShape(
        state: BlockState,
        level: BlockGetter,
        pos: BlockPos,
        context: CollisionContext,
    ): VoxelShape = SHAPE

    override fun <T : BlockEntity?> getTicker(
        level: Level,
        state: BlockState,
        blockEntityType: BlockEntityType<T?>,
    ): BlockEntityTicker<T?>? =
        createTickerHelper(blockEntityType, EMRegistries.ENTITY_MODULE_BE)
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
        val entity = level.getBlockEntity(blockPos)
        if(entity !is EntityModuleBlockEntity) return ItemInteractionResult.FAIL

        if(itemStack.isEmpty){ entity.adjustDirection(blockHitResult.direction.opposite) }

        val redstone = itemStack.`is`(Items.REDSTONE)
        val glowstone = itemStack.`is`(Items.GLOWSTONE_DUST)

        var zSize = entity.zSize
        if(redstone) zSize++ else if(glowstone) zSize-- else zSize

        entity.adjustSize(zSize, zSize,zSize)

        return ItemInteractionResult.CONSUME
    }
}