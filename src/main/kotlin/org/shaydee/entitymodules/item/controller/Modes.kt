package org.shaydee.entitymodules.item.controller

import net.minecraft.core.component.DataComponents
import net.minecraft.sounds.SoundEvents
import net.minecraft.util.FastColor
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.component.CustomModelData
import net.minecraft.world.level.block.SoundType
import org.shaydee.entitymodules.block.entity_module.EntityModuleBlockEntity
import org.shaydee.entitymodules.helpers.EMHelpers.getEntityModule
import org.shaydee.entitymodules.item.controller.EntityModuleController.Companion.getCurrentMode
import org.shaydee.entitymodules.item.controller.EntityModuleController.Companion.getSound
import org.shaydee.entitymodules.item.controller.EntityModuleController.Companion.modeText
import org.shaydee.entitymodules.registry.EMRegistries

enum class Modes (
    val mode: String,
    val colour: Int
){
    POSITION("position", FastColor.ARGB32.color(181, 43, 255)),
    INFLATE("size", FastColor.ARGB32.color(43, 255, 71)),
    RESET("reset", FastColor.ARGB32.color(255, 75, 43));

    fun doOnToggle(stack: ItemStack, player: Player): InteractionResultHolder<ItemStack> {
        val getIndex = getCurrentMode(stack)
        val index = if(getIndex == entries.size) 1 else getIndex + 1
        val onReset = onReset(stack, player, getCurrentMode(stack))

        if(onReset) return InteractionResultHolder.consume(stack)

        stack.set(DataComponents.CUSTOM_MODEL_DATA, CustomModelData(index))
        player.displayClientMessage(modeText(stack), true)
        player.playSound(SoundType.PINK_PETALS.placeSound, 1f, 2f)
        player.playSound(SoundType.SCULK.placeSound, 1f, 2f)
        return InteractionResultHolder.consume(stack)
    }

    fun onReset(stack: ItemStack, player: Player, index: Int): Boolean {
        if(index == 3 && player.isShiftKeyDown) {
            val level = player.level()
            val getBoundEM = stack.get(EMRegistries.ENTITY_MODULE_BLOCKPOS) ?: return false
            val entity: EntityModuleBlockEntity = level.getEntityModule(getBoundEM) ?: return false
            entity.resetBounding()
            entity.externalBoundingPos = EntityModuleBlockEntity.Companion.FAKE_POS
            getSound(level, player.blockPosition(), SoundEvents.BEACON_DEACTIVATE)
            return true
        }

        return false
    }
}