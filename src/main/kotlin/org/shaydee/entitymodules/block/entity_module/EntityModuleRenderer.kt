package org.shaydee.entitymodules.block.entity_module

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.world.phys.AABB
import org.shaydee.entitymodules.client.RenderBounding
import java.awt.Color

class EntityModuleRenderer(context : BlockEntityRendererProvider.Context) : BlockEntityRenderer<EntityModuleBlockEntity> {

    override fun render(
        animatable: EntityModuleBlockEntity,
        partialTicks: Float,
        poseStack: PoseStack,
        bufferSource: MultiBufferSource,
        light: Int,
        overlay: Int,
    ) {

        val mc = Minecraft.getInstance()
        val level = mc.level

        if (level == null) return
        val blockPos = animatable.blockPos
        val origin = blockPos
        val view = mc.gameRenderer.mainCamera.position

        poseStack.pushPose()
        poseStack.translate((-blockPos.x).toDouble(), (-blockPos.y).toDouble(), (-blockPos.z).toDouble())
        poseStack.pushPose()
        RenderBounding.renderLines(
            poseStack,
            animatable.getInflate(origin),
            Color(52, 164, 235),
            bufferSource
        )
        poseStack.popPose()
        poseStack.popPose()

    }

    override fun shouldRenderOffScreen(blockEntity: EntityModuleBlockEntity) = true

    override fun getRenderBoundingBox(blockEntity: EntityModuleBlockEntity): AABB = blockEntity.getInflate(blockEntity.blockPos)
}