package org.shaydee.entitymodules.network

import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import net.neoforged.neoforge.network.registration.PayloadRegistrar
import org.shaydee.entitymodules.EntityModules
import org.shaydee.entitymodules.network.clientToServer.EntityModuleC2SSync

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD, modid = EntityModules.ID)
object Network {

    @SubscribeEvent
    fun register(event: RegisterPayloadHandlersEvent) {
        val payloadRegistrar: PayloadRegistrar = event.registrar(EntityModules.ID).versioned("1.0.0").optional()

        payloadRegistrar.playToServer(
            EntityModuleC2SSync.TYPE,
            EntityModuleC2SSync.STREAM_CODEC,
            EntityModuleC2SSync::handle
        )
    }
}