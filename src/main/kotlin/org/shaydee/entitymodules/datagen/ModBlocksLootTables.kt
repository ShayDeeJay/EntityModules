package org.shaydee.entitymodules.datagen

import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.loot.BlockLootSubProvider
import net.minecraft.data.loot.LootTableProvider
import net.minecraft.data.loot.LootTableProvider.SubProviderEntry
import net.minecraft.resources.ResourceKey
import net.minecraft.world.flag.FeatureFlags.REGISTRY
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import org.shaydee.entitymodules.registry.EMRegistries
import java.util.concurrent.CompletableFuture

class ModBlockLootTables(
    provider: HolderLookup.Provider,
) : BlockLootSubProvider(
    emptySet<Item>(),
    REGISTRY.allFlags(),
    provider
) {

    override fun getKnownBlocks(): Iterable<Block?> = EMRegistries.BLOCK_REGISTRY.entries.map{ x -> x.get()}.toList() as Iterable<Block?>

    override fun generate() = dropSelf(EMRegistries.ENTITY_MODULE_BLOCK)
}

object ModLootTableProvider {
    fun create(output: PackOutput, registries: CompletableFuture<HolderLookup.Provider?>): LootTableProvider {
        return LootTableProvider(
            output,
            mutableSetOf<ResourceKey<LootTable?>?>(),
            mutableListOf<SubProviderEntry?>(SubProviderEntry({ ModBlockLootTables(it) }, LootContextParamSets.BLOCK)),
            registries
        )
    }
}