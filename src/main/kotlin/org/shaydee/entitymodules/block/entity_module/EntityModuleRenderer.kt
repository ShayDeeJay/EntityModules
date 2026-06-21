package org.shaydee.entitymodules.block.entity_module

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.Minecraft
import net.minecraft.client.player.LocalPlayer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.client.renderer.entity.ItemRenderer
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.core.Direction
import net.minecraft.util.RandomSource
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.phys.AABB
import org.shaydee.entitymodules.block.entity_module.EntityModuleBlock.Companion.INVISIBLE
import org.shaydee.entitymodules.item.controller.EntityModuleController
import org.shaydee.entitymodules.registry.EMRegistries
import org.shaydee.shaydeeapi.helpers.ColourHelpers
import org.shaydee.shaydeeapi.helpers.RenderHelpers

class EntityModuleRenderer(val context : BlockEntityRendererProvider.Context) : BlockEntityRenderer<EntityModuleBlockEntity> {

    override fun shouldRenderOffScreen(blockEntity: EntityModuleBlockEntity) = true

    override fun getRenderBoundingBox(blockEntity: EntityModuleBlockEntity): AABB {
        val blockBox = AABB(blockEntity.blockPos)
        val inflatedBox = blockEntity.getInflate()
        return blockBox.minmax(inflatedBox)
    }

    override fun render(
        bEntity: EntityModuleBlockEntity,
        partialTicks: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        light: Int,
        overlay: Int,
    ) {
        val mc = Minecraft.getInstance()
        val player = mc.player ?: return
        val itemRenderer = mc.itemRenderer
        val renderer = mc.blockRenderer
        val level = bEntity.level ?: return

        if(bEntity.blockState.getValue(INVISIBLE)){
            poseStack.pushPose()
            poseStack.translate(0.5, 0.5, 0.5)
            poseStack.scale(1.01f, 1.01f, 1.01f)
            poseStack.translate(-0.5, -0.5, -0.5)
            val state = level.getBlockState(bEntity.blockPos.below()) ?: return
            val source = bufferSource.getBuffer(RenderType.cutout())
            renderer.renderBatched(state, bEntity.blockPos, level, poseStack, source, false, RandomSource.create())
            poseStack.popPose()
        } else {
            renderModule(bEntity, poseStack, itemRenderer, light, bufferSource)
        }

        renderBoundingBox(player, bEntity, mc, poseStack, bufferSource)
    }

    private fun renderModule(
        bEntity: EntityModuleBlockEntity,
        poseStack: PoseStack,
        itemRenderer: ItemRenderer,
        light: Int,
        bufferSource: MultiBufferSource,
    ) {

        for (direction in Direction.entries) {
            poseStack.pushPose()

            val offset = direction.step().mul(0.4825f)
            poseStack.translate(0.5 + offset.x(), 0.5 + offset.y(), 0.5 + offset.z())

            poseStack.mulPose(direction.rotation)
            poseStack.mulPose(Axis.XP.rotationDegrees(90F))
            poseStack.mulPose(Axis.ZP.rotationDegrees(180F))
            val scale = 0.575F
            poseStack.scale(scale, scale, scale)

            itemRenderer.renderStatic(
                bEntity.getModule(),
                ItemDisplayContext.FIXED,
                120.coerceAtLeast(light),
                OverlayTexture.NO_OVERLAY,
                poseStack,
                bufferSource,
                bEntity.level,
                1
            )

            itemRenderer.renderStatic(
                bEntity.getFunction(),
                ItemDisplayContext.FIXED,
                120.coerceAtLeast(light),
                OverlayTexture.NO_OVERLAY,
                poseStack,
                bufferSource,
                bEntity.level,
                1
            )

            poseStack.popPose()
            
        }
    }

    private fun renderBoundingBox(
        player: LocalPlayer,
        bEntity: EntityModuleBlockEntity,
        mc: Minecraft,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
    ) {
        val stack = player.mainHandItem
        val item = stack.item
        if (item is EntityModuleController) {
            if (stack.get(EMRegistries.ENTITY_MODULE_BLOCKPOS) != bEntity.blockPos) return;

            mc.level ?: return

            val blockPos = bEntity.blockPos
            poseStack.pushPose()
            poseStack.translate((-blockPos.x).toDouble(), (-blockPos.y).toDouble(), (-blockPos.z).toDouble())

            val externalColour = ColourHelpers.negativeRed
            val ownerColour = ColourHelpers.aetherBlue

            bEntity.getExternalBounding()?.let {
                RenderHelpers.renderBoundingBox(
                    poseStack,
                    AABB(it.blockPos),
                    externalColour,
                    bufferSource,
                    RenderHelpers.LINES_NO_DEPTH
                )
            }

            RenderHelpers.renderBoundingBox(
                poseStack,
                AABB(bEntity.blockPos),
                ownerColour,
                bufferSource,
                RenderHelpers.LINES_NO_DEPTH
            )

            RenderHelpers.renderBoundingBox(
                poseStack,
                bEntity.getInflate(),
                if(bEntity.getExternalBounding() == null) ownerColour else externalColour,
                bufferSource
            )

            poseStack.popPose()
        }
    }

}