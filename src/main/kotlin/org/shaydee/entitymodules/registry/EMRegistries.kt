package org.shaydee.entitymodules.registry

// THIS LINE IS REQUIRED FOR USING PROPERTY DELEGATES
import com.mojang.serialization.Codec
import net.minecraft.core.BlockPos
import net.minecraft.core.component.DataComponentType
import net.minecraft.core.registries.Registries
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.entity.BlockEntityType.*
import net.neoforged.neoforge.registries.DeferredBlock
import net.neoforged.neoforge.registries.DeferredHolder
import net.neoforged.neoforge.registries.DeferredRegister
import org.shaydee.entitymodules.EntityModules
import org.shaydee.entitymodules.block.entity_module.EntityModuleBlock
import org.shaydee.entitymodules.block.entity_module.EntityModuleBlockEntity
import org.shaydee.entitymodules.item.EntityModuleController
import thedarkcolour.kotlinforforge.neoforge.forge.getValue
import java.util.function.Supplier
import java.util.function.UnaryOperator

object EMRegistries {

    val ITEM_REGISTRY: DeferredRegister.Items = DeferredRegister.createItems(EntityModules.ID)
    val BLOCK_REGISTRY: DeferredRegister.Blocks = DeferredRegister.createBlocks(EntityModules.ID)
    val BLOCK_ENTITY_REGISTRY: DeferredRegister<BlockEntityType<*>?> = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, EntityModules.ID)
    val CREATIVE_MODE_TAB: DeferredRegister<CreativeModeTab?> = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, EntityModules.ID)
    val COMPONENTS: DeferredRegister<DataComponentType<*>?> = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, EntityModules.ID)

    val ENTITY_MODULE_BLOCKPOS: DataComponentType<BlockPos> by register("entity_module_blockpos"){
        builder -> builder
            .persistent(BlockPos.CODEC)
            .networkSynchronized(BlockPos.STREAM_CODEC)
            .cacheEncoding()
    }

    val ENTITY_MODULE_BLOCK: EntityModuleBlock by registerBlockWithItem("entity_module", blockSupplier = ::EntityModuleBlock)

    val ENTITY_MODULE_CONTROLLER: Item by ITEM_REGISTRY
        .register("entity_module_controller"){ -> EntityModuleController() }

    val ENTITY_MODULE_BE: BlockEntityType<EntityModuleBlockEntity> by BLOCK_ENTITY_REGISTRY
        .register("entity_module_entity") { -> Builder.of(
            BlockEntitySupplier { pos, state -> EntityModuleBlockEntity(pos = pos, blockState = state) },
            ENTITY_MODULE_BLOCK
        ).build(null)
    }

    fun <T : Block> registerBlockWithItem(
        name: String,
        itemProperties: Item.Properties = Item.Properties(),
        blockSupplier: () -> T,
    ):  DeferredBlock<T> {

        val block = BLOCK_REGISTRY.register(name, blockSupplier)
        ITEM_REGISTRY.register(name) { -> BlockItem(block.get(), itemProperties) }

        return block
    }

    private fun <T> register(
        name: String,
        op: (DataComponentType.Builder<T>) -> DataComponentType.Builder<T>
    ): DeferredHolder<DataComponentType<*>?, DataComponentType<T>> {
        return COMPONENTS.register(name) {
            -> op(DataComponentType.builder()).build()
        }
    }
}
