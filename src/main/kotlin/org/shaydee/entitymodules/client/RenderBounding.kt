package org.shaydee.entitymodules.client

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import net.minecraft.world.phys.AABB
import java.awt.Color

object RenderBounding {

    fun renderLines(matrix: PoseStack, aabb: AABB, color: Color, buffer: MultiBufferSource) {
        val x = aabb.minX.toFloat()
        val y = aabb.minY.toFloat()
        val z = aabb.minZ.toFloat()
        val dx = aabb.maxX.toFloat()
        val dy = aabb.maxY.toFloat()
        val dz = aabb.maxZ.toFloat()
        val builder = buffer.getBuffer(RenderType.lines())

        matrix.pushPose()
        val matrix4f = matrix.last().pose()
        val matrix3f = matrix.last()
        val colorRGB = color.rgb

        fun addVertexes(x: Float, y: Float, z: Float, nX: Float, nY: Float, nZ: Float): VertexConsumer {
            return builder
                .addVertex(matrix4f, x, y, z)
                .setColor(colorRGB)
                .setNormal(matrix3f, nX, nY, nZ)
        }

        addVertexes(x,  y,  z,  1.0f,  0.0f,  0.0f)
        addVertexes(dx, y,  z,  1.0f,  0.0f,  0.0f)
        addVertexes(x,  y,  z,  0.0f,  1.0f,  0.0f)
        addVertexes(x,  dy, z,  0.0f,  1.0f,  0.0f)

        addVertexes(x,  y,  z,  0.0f,  0.0f,  1.0f)
        addVertexes(x,  y,  dz, 0.0f,  0.0f,  1.0f)
        addVertexes(dx, y,  z,  0.0f,  1.0f,  0.0f)
        addVertexes(dx, dy, z,  0.0f,  1.0f,  0.0f)

        addVertexes(dx, dy, z, -1.0f,  0.0f,  0.0f)
        addVertexes(x,  dy, z, -1.0f,  0.0f,  0.0f)
        addVertexes(x,  dy, z,  0.0f,  0.0f,  1.0f)
        addVertexes(x,  dy, dz, 0.0f,  0.0f,  1.0f)

        addVertexes(x,  dy, dz, 0.0f, -1.0f,  0.0f)
        addVertexes(x,  y,  dz, 0.0f, -1.0f,  0.0f)
        addVertexes(x,  y,  dz, 1.0f,  0.0f,  0.0f)
        addVertexes(dx, y,  dz, 1.0f,  0.0f,  0.0f)

        addVertexes(dx, y,  dz, 0.0f,  0.0f, -1.0f)
        addVertexes(dx, y,  z,  0.0f,  0.0f, -1.0f)
        addVertexes(x,  dy, dz, 1.0f,  0.0f,  0.0f)
        addVertexes(dx, dy, dz, 1.0f,  0.0f,  0.0f)

        addVertexes(dx, y,  dz, 0.0f,  1.0f,  0.0f)
        addVertexes(dx, dy, dz, 0.0f,  1.0f,  0.0f)
        addVertexes(dx, dy, z,  0.0f,  0.0f,  1.0f)
        addVertexes(dx, dy, dz, 0.0f,  0.0f,  1.0f)

        matrix.popPose()
    }


}