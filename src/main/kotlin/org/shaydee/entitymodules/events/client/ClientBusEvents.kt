package org.shaydee.entitymodules.events.client

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.event.IModBusEvent
import net.neoforged.neoforge.client.event.EntityRenderersEvent.RegisterRenderers
import org.shaydee.entitymodules.EntityModules
import org.shaydee.entitymodules.block.entity_module.EntityModuleRenderer
import org.shaydee.entitymodules.registry.EMRegistries

@EventBusSubscriber(modid = EntityModules.ID, bus = EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
object ClientBusEvents : IModBusEvent {

    @SubscribeEvent
    fun registerBER(event: RegisterRenderers) {
        //Block entities
        event.registerBlockEntityRenderer(EMRegistries.ENTITY_MODULE_BE) { EntityModuleRenderer(it) }
    }

}