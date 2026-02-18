package org.shaydee.entitymodules.helpers

import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.Level
import org.shaydee.entitymodules.EntityModules
import org.shaydee.entitymodules.block.entity_module.EntityModuleBlockEntity
import org.shaydee.shaydeeapi.Helpers


object EMHelpers{

    fun getEntityModule(level: Level, pos: BlockPos): EntityModuleBlockEntity? =
        level.getBlockEntity(pos) as? EntityModuleBlockEntity

    fun res(location: String?): ResourceLocation = Helpers.res(location ?: "", EntityModules.ID)

}