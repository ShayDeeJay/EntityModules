package org.shaydee.entitymodules.events.client

import com.ibm.icu.text.PluralRules
import org.shaydee.entitymodules.network.clientToServer.EntityModuleC2SSync
import net.minecraft.client.Minecraft
import net.minecraft.core.component.DataComponents.CUSTOM_MODEL_DATA
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.InputEvent
import net.neoforged.neoforge.network.PacketDistributor
import org.shaydee.entitymodules.EntityModules
import org.shaydee.entitymodules.block.entity_module.EntityModuleBlockEntity
import org.shaydee.entitymodules.item.controller.EntityModuleController
import org.shaydee.entitymodules.registry.EMRegistries

@EventBusSubscriber(modid = EntityModules.ID, value = [Dist.CLIENT])
object ClientEvents {

    @SubscribeEvent
    fun onMouseScroll(event: InputEvent.MouseScrollingEvent) {
        val mc = Minecraft.getInstance()
        val player = mc.player ?: return
        val mainHandItem = player.mainHandItem
        val item = mainHandItem.item
        val level = mc.level ?: return
        if(item !is EntityModuleController || mainHandItem.get(CUSTOM_MODEL_DATA)?.value == 3) return

        val getBoundEM = mainHandItem.get(EMRegistries.ENTITY_MODULE_BLOCKPOS) ?: return
        val blockEntity = level.getBlockEntity(getBoundEM)
        if(blockEntity !is EntityModuleBlockEntity) return
        PacketDistributor.sendToServer(EntityModuleC2SSync(getBoundEM, event.scrollDeltaY))
        event.isCanceled = true;
    }

}