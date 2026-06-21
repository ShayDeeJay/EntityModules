package org.shaydee.entitymodules.helpers

import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.Level
import org.shaydee.entitymodules.EntityModules
import org.shaydee.entitymodules.block.entity_module.EntityModuleBlockEntity
import org.shaydee.shaydeeapi.Helpers
import org.shaydee.shaydeeapi.helpers.TextHelpers.withStyle


object EMHelpers{

    fun Level.getEntityModule(pos: BlockPos): EntityModuleBlockEntity? =
        this.getBlockEntity(pos) as? EntityModuleBlockEntity

    fun String.res(): ResourceLocation = Helpers.res(this, EntityModules.ID)

    fun String.withContext(colour: Int = -1) = "entitymodules.${this}".withStyle(colour)

}