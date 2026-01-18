package org.shaydee.entitymodules.datagen

import net.minecraft.data.PackOutput
import net.minecraft.world.level.block.Block
import net.neoforged.neoforge.client.model.generators.BlockStateProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper
import org.shaydee.entitymodules.EntityModules
import org.shaydee.entitymodules.registry.EMRegistries.ENTITY_MODULE_BLOCK

class ModBlockStateProvider(
    output: PackOutput,
    fileHelper: ExistingFileHelper,
) : BlockStateProvider(output, EntityModules.ID, fileHelper) {

    override fun registerStatesAndModels() = blockWithItem(ENTITY_MODULE_BLOCK)

    private fun blockWithItem(blockRegistryObject: Block) =
        simpleBlockWithItem(blockRegistryObject, cubeAll(blockRegistryObject))

}
