package org.shaydee.entitymodules.datagen

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder
import net.neoforged.neoforge.client.model.generators.ItemModelProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper
import org.shaydee.entitymodules.EntityModules
import org.shaydee.entitymodules.helpers.EMHelpers
import org.shaydee.entitymodules.registry.EMRegistries
import java.util.function.Consumer

class ModItemModelProvider(
    output: PackOutput,
    existingFileHelper: ExistingFileHelper
) : ItemModelProvider(output, EntityModules.ID, existingFileHelper) {
    override fun registerModels() {
        registerSimpleItems()
    }

    private fun createSimpleItemModel(item: Item) {
        val id = BuiltInRegistries.ITEM.getKey(item)
        getWithParent(item, id.path)
    }

    private fun getWithParent(item: Item, path: String): ItemModelBuilder {
        return withExistingParent(path, ResourceLocation.withDefaultNamespace("item/generated"))
            .texture("layer0", EMHelpers.res("item/$path"))
    }

    private fun registerSimpleItems() {
        val simpleItems = listOf(
            EMRegistries.ENTITY_MODULE_CONTROLLER
        )

        simpleItems.forEach(Consumer { item -> this.createSimpleItemModel(item) })
    }

    companion object {
        private val MODEL_DATA: ResourceLocation = ResourceLocation.withDefaultNamespace("custom_model_data")
    }

}