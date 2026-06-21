package org.shaydee.entitymodules.datagen

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder
import net.neoforged.neoforge.client.model.generators.ItemModelProvider
import net.neoforged.neoforge.common.data.ExistingFileHelper
import org.shaydee.entitymodules.EntityModules
import org.shaydee.entitymodules.helpers.EMHelpers.res
import org.shaydee.entitymodules.registry.EMRegistries
import org.shaydee.entitymodules.registry.EMRegistries.regFunctions
import org.shaydee.entitymodules.registry.EMRegistries.regModules

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
            .texture("layer0", "item/$path".res())
    }

    private fun registerSimpleItems() {
        regModules.forEach {
            this.createSimpleItemModel(it.get())
        }

        regFunctions.forEach {
            this.createSimpleItemModel(it.get())
        }

        listOf(
            EMRegistries.ENTITY_MODULE_CONTROLLER,
            EMRegistries.LINKING_TOOL
        ).forEach { this.createSimpleItemModel(it)  }
    }

    companion object {
        private val MODEL_DATA: ResourceLocation = ResourceLocation.withDefaultNamespace("custom_model_data")
    }

}