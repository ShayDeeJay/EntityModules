package org.shaydee.entitymodules.helpers

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.Level
import org.shaydee.entitymodules.EntityModules
import org.shaydee.entitymodules.block.entity_module.EntityModuleBlockEntity
import java.util.function.UnaryOperator


object Helpers{

    fun getEntityModule(level: Level, pos: BlockPos): EntityModuleBlockEntity? {
        val entity = level.getBlockEntity(pos)
        if (entity is EntityModuleBlockEntity) return entity

        return null
    }

    fun res(location: String?): ResourceLocation =
        ResourceLocation.fromNamespaceAndPath(EntityModules.ID, location ?: "")

    fun withStyleComponent(text: String, colour: Int): Component =
        Component.literal(text).withStyle { style: Style -> style.withColor(colour) }

}