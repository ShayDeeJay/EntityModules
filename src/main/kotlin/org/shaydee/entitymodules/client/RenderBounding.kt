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
import net.minecraft.world.phys.Vec3
import org.joml.Matrix3f
import org.joml.Matrix4f
import java.awt.Color

object RenderBounding {

    fun renderLines(matrix: PoseStack, aabb: AABB, color: Color, buffer: MultiBufferSource, type: RenderType = RenderType.lines()) {
        val x = aabb.minX.toFloat()
        val y = aabb.minY.toFloat()
        val z = aabb.minZ.toFloat()
        val dx = aabb.maxX.toFloat()
        val dy = aabb.maxY.toFloat()
        val dz = aabb.maxZ.toFloat()
        val builder = buffer.getBuffer(type)

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

        val nX = 1.0f
        val nY = 0.0f
        addVertexes(x,  y,  z, nX, nY, nY)
        addVertexes(dx, y,  z, nX, nY, nY)
        addVertexes(x,  y,  z, nY, nX, nY)
        addVertexes(x,  dy, z, nY, nX, nY)

        addVertexes(x,  y,  z, nY, nY, nX)
        addVertexes(x,  y,  dz, nY, nY, nX)
        addVertexes(dx, y,  z, nY, nX, nY)
        addVertexes(dx, dy, z, nY, nX, nY)

        addVertexes(dx, dy, z, -nX, nY, nY)
        addVertexes(x,  dy, z, -nX, nY, nY)
        addVertexes(x,  dy, z, nY, nY, nX)
        addVertexes(x,  dy, dz, nY, nY, nX)

        addVertexes(x,  dy, dz, nY, -nX, nY)
        addVertexes(x,  y,  dz, nY, -nX, nY)
        addVertexes(x,  y,  dz, nX, nY, nY)
        addVertexes(dx, y,  dz, nX, nY, nY)

        addVertexes(dx, y,  dz, nY, nY, -nX)
        addVertexes(dx, y,  z, nY, nY, -nX)
        addVertexes(x,  dy, dz, nX, nY, nY)
        addVertexes(dx, dy, dz, nX, nY, nY)

        addVertexes(dx, y,  dz, nY, nX, nY)
        addVertexes(dx, dy, dz, nY, nX, nY)
        addVertexes(dx, dy, z, nY, nY, nX)
        addVertexes(dx, dy, dz, nY, nY, nX)

        matrix.popPose()
    }


}