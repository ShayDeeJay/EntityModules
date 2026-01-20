package org.shaydee.entitymodules.block.entity_module

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.world.phys.AABB
import org.shaydee.entitymodules.client.RenderBounding
import org.shaydee.entitymodules.item.EntityModuleController
import org.shaydee.entitymodules.registry.EMRegistries
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
        val player = mc.player ?: return
        if(player.mainHandItem.item is EntityModuleController){
            val level = mc.level

            if (level == null) return
            val blockPos = animatable.blockPos

            poseStack.pushPose()
            poseStack.translate((-blockPos.x).toDouble(), (-blockPos.y).toDouble(), (-blockPos.z).toDouble())
            poseStack.pushPose()
            RenderBounding.renderLines(
                poseStack,
                animatable.getInflate(),
                Color(52, 164, 235),
                bufferSource
            )
            poseStack.popPose()
            poseStack.popPose()
        }

    }

    override fun shouldRenderOffScreen(blockEntity: EntityModuleBlockEntity) = true

    override fun getRenderBoundingBox(blockEntity: EntityModuleBlockEntity): AABB = blockEntity.getInflate()
}