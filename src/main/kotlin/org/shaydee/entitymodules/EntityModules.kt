package org.shaydee.entitymodules

import net.minecraft.network.chat.Component
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.CreativeModeTabs
import net.minecraft.world.item.ItemStack
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.registries.DeferredHolder
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.shaydee.entitymodules.registry.EMRegistries
import org.shaydee.entitymodules.registry.EMRegistries.CREATIVE_MODE_TAB
import org.shaydee.entitymodules.registry.EMRegistries.ENTITY_MODULE_BLOCK
import org.shaydee.entitymodules.registry.EMRegistries.ENTITY_MODULE_CONTROLLER
import thedarkcolour.kotlinforforge.neoforge.forge.MOD_BUS

@Mod(EntityModules.ID)
class EntityModules {

    companion object{
        const val ID = "entitymodules"
    }

    val LOGGER: Logger = LogManager.getLogger(ID)

    init {
        EMRegistries.ITEM_REGISTRY.register(MOD_BUS)
        EMRegistries.BLOCK_REGISTRY.register(MOD_BUS)
        EMRegistries.BLOCK_ENTITY_REGISTRY.register(MOD_BUS)
        EMRegistries.COMPONENTS.register(MOD_BUS)

        CREATIVE_MODE_TAB.register(MOD_BUS)
    }

    val CREATIVE_TAB: DeferredHolder<CreativeModeTab?, CreativeModeTab?> =
        CREATIVE_MODE_TAB.register("entity_module") {
            -> CreativeModeTab.builder()
                .icon { ItemStack(ENTITY_MODULE_BLOCK.asItem()) }
                .title(Component.translatable("creative_tab.entity_module_tab"))
                .displayItems { parameters, outPut ->
                    outPut.accept{ ENTITY_MODULE_BLOCK.asItem() }
                    outPut.accept { ENTITY_MODULE_CONTROLLER }
                }
                .withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
                .build()
        }
}
