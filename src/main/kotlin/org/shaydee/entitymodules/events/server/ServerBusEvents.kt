package org.shaydee.entitymodules.events.server

import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.event.IModBusEvent
import net.neoforged.neoforge.data.event.GatherDataEvent
import org.shaydee.entitymodules.EntityModules
import org.shaydee.entitymodules.datagen.ModBlockStateProvider
import org.shaydee.entitymodules.datagen.ModLootTableProvider

@EventBusSubscriber(modid = EntityModules.ID, bus = EventBusSubscriber.Bus.MOD)
object ServerBusEvents : IModBusEvent {

    @SubscribeEvent
    fun gatherData(event: GatherDataEvent) {
        val generator = event.generator
        val packOutput = generator.packOutput
        val lookupProvider = event.lookupProvider
        val existingFileHelper = event.existingFileHelper

        generator.addProvider(event.includeServer(), ModLootTableProvider.create(packOutput, lookupProvider))
        generator.addProvider(event.includeClient(), ModBlockStateProvider(packOutput, existingFileHelper))
    }

}