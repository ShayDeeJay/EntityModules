package org.shaydee.entitymodules.registry

// THIS LINE IS REQUIRED FOR USING PROPERTY DELEGATES
import net.minecraft.core.BlockPos
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.entity.BlockEntityType.BlockEntitySupplier
import net.minecraft.world.level.block.entity.BlockEntityType.Builder
import net.neoforged.neoforge.registries.DeferredBlock
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredItem
import net.neoforged.neoforge.registries.DeferredRegister
import org.shaydee.entitymodules.EntityModules
import org.shaydee.entitymodules.block.entity_module.EntityModuleBlock
import org.shaydee.entitymodules.block.entity_module.EntityModuleBlockEntity
import org.shaydee.entitymodules.data.EMType
import org.shaydee.entitymodules.data.FunctionType
import org.shaydee.entitymodules.item.Function
import org.shaydee.entitymodules.item.LinkingTool
import org.shaydee.entitymodules.item.Module
import org.shaydee.entitymodules.item.controller.EntityModuleController
import thedarkcolour.kotlinforforge.neoforge.forge.getValue

object EMRegistries {

    val ITEM_REGISTRY: DeferredRegister.Items =
        DeferredRegister.createItems(EntityModules.ID)

    val BLOCK_REGISTRY: DeferredRegister.Blocks =
        DeferredRegister.createBlocks(EntityModules.ID)

    val BLOCK_ENTITY_REGISTRY: DeferredRegister<BlockEntityType<*>?> =
        DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, EntityModules.ID)

    val CREATIVE_MODE_TAB: DeferredRegister<CreativeModeTab?> =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, EntityModules.ID)

    val COMPONENTS: DeferredRegister<DataComponentType<*>?> =
        DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, EntityModules.ID)

    //Items
    val ENTITY_MODULE_CONTROLLER: Item by regItem("entity_module_controller", ::EntityModuleController)

    val LINKING_TOOL: Item by regItem("linking_tool", ::LinkingTool)

    val regModules = buildList {
        EMType.entries.forEach {
            add(registerModule(it))
        }
    }

    val regFunctions = buildList {
        FunctionType.entries.forEach {
            add(registerFunction(it))
        }
    }

    //Components
    val ENTITY_MODULE_BLOCKPOS: DataComponentType<BlockPos> by register("entity_module_blockpos"){ it
        .persistent(BlockPos.CODEC)
        .networkSynchronized(BlockPos.STREAM_CODEC)
        .cacheEncoding()
    }

    //Blocks
    val ENTITY_MODULE_BLOCK: EntityModuleBlock by registerBlockWithItem("entity_module", blockSupplier = ::EntityModuleBlock)

    //Block Entities
    val ENTITY_MODULE_BE: BlockEntityType<EntityModuleBlockEntity> by BLOCK_ENTITY_REGISTRY
        .register("entity_module_entity") { -> Builder.of(
            BlockEntitySupplier { pos, state -> EntityModuleBlockEntity(pos = pos, blockState = state) },
            ENTITY_MODULE_BLOCK
        ).build(null)
    }

    fun regItem(type: String, item: () -> Item): DeferredItem<Item>{
        return ITEM_REGISTRY.register(type){ -> item() }
    }

    fun registerFunction(fType: FunctionType): DeferredItem<Item> {
        val type = "${fType.name.lowercase()}_function"
        return regItem(type){ Function(fType) }
    }

    fun registerModule(eMType: EMType): DeferredItem<Item> {
        val type = "${eMType.name.lowercase()}_module"
        return regItem(type){ Module(eMType) }
    }

    fun <T : Block> registerBlockWithItem(
        name: String,
        itemProperties: Item.Properties = Item.Properties(),
        blockSupplier: () -> T,
    ): DeferredBlock<T> {
        val block = BLOCK_REGISTRY.register(name, blockSupplier)
        ITEM_REGISTRY.register(name) { -> BlockItem(block.get(), itemProperties) }
        return block
    }

    private fun <T> register(
        name: String,
        op: (DataComponentType.Builder<T>) -> DataComponentType.Builder<T>
    ): DeferredHolder<DataComponentType<*>?, DataComponentType<T>> {
        return COMPONENTS.register(name) { -> op(DataComponentType.builder()).build() }
    }
}
