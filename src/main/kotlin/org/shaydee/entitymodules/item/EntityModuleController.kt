package org.shaydee.entitymodules.item

import net.minecraft.core.component.DataComponents.*
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.util.FastColor.ARGB32.*
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.CustomModelData
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.SoundType
import org.shaydee.entitymodules.helpers.Helpers

class EntityModuleController(
    properties: Properties = Properties().component(CUSTOM_MODEL_DATA, CustomModelData(1))
) : Item(properties) {

    companion object {
        fun getCurrentMode(stack: ItemStack): Int = stack.get(CUSTOM_MODEL_DATA)?.value ?: 1

        val MODES = mapOf(
            1 to Helpers.withStyleComponent("Position", color(181, 43, 255)),
            2 to Helpers.withStyleComponent("Inflate", color(43, 255, 71)),
            3 to Helpers.withStyleComponent("Shrink", color(43, 216, 255)),
            4 to Helpers.withStyleComponent("Reset", color(255, 75, 43))
        )
    }

    override fun getHighlightTip(
        item: ItemStack,
        displayName: Component,
    ): Component {
        return super.getHighlightTip(item, displayName).copy().append(" - ").append(modeText(item))
    }

    override fun appendHoverText(
        stack: ItemStack,
        tooltipContext: TooltipContext,
        components: MutableList<Component?>,
        flag: TooltipFlag,
    ) {
        components.add(modeText(stack))
    }

    private fun modeText(stack: ItemStack): MutableComponent {
        val element = Helpers.withStyleComponent("Mode: ", color(145, 142, 142))
        val getIndex = getCurrentMode(stack)
        val mode = MODES[getIndex]
        val text = mode ?: Component.empty()
        return element.copy().append(text)
    }

    override fun use(
        level: Level,
        player: Player,
        interactionHand: InteractionHand,
    ): InteractionResultHolder<ItemStack?> {
        val stack = player.getItemInHand(interactionHand)
        val getIndex = getCurrentMode(stack)
        val maxEntries = MODES.size
        val index = if(getIndex == maxEntries) 1 else getIndex+1

        if(player.isShiftKeyDown){
            stack.set(CUSTOM_MODEL_DATA, CustomModelData(index))
            player.displayClientMessage(modeText(stack), true)
            player.playSound(SoundType.PINK_PETALS.placeSound, 1f, 2f)
            player.playSound(SoundType.SCULK.placeSound, 1f, 2f)

            return InteractionResultHolder.consume(stack)
        }

        return InteractionResultHolder.fail(stack)
    }

}