package org.shaydee.entitymodules.item.controller

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.util.FastColor
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.CustomModelData
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import org.shaydee.entitymodules.block.entity_module.EntityModuleBlock
import org.shaydee.entitymodules.helpers.EMHelpers.getEntityModule
import org.shaydee.entitymodules.helpers.EMHelpers.withContext
import org.shaydee.entitymodules.registry.EMRegistries
import org.shaydee.shaydeeapi.helpers.TextHelpers

class EntityModuleController(
    properties: Properties = Properties().component(DataComponents.CUSTOM_MODEL_DATA, CustomModelData(1))
) : Item(properties) {

    companion object {
        fun getCurrentMode(stack: ItemStack): Int = stack.get(DataComponents.CUSTOM_MODEL_DATA)?.value ?: 1

        fun getSound(level: Level, blockPos: BlockPos, soundEvent: SoundEvent, pitch: Float = 2F) {
            level.playSound(null, blockPos, soundEvent, SoundSource.BLOCKS, 1F, pitch)
            level.playSound(null, blockPos, SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.BLOCKS, 1F, pitch)
        }

        fun modeText(stack: ItemStack): MutableComponent {
            val element = "info.mode".withContext(FastColor.ARGB32.color(145, 142, 142))
            val getIndex = getCurrentMode(stack)
            val mode = Modes.entries[getIndex-1]
            val text = "modes.${mode.mode}".withContext(mode.colour)
            return element.copy().append(text)
        }
    }

    override fun appendHoverText(
        stack: ItemStack,
        tooltipContext: TooltipContext,
        components: MutableList<Component?>,
        flag: TooltipFlag,
    ) {
        components.add(modeText(stack))
        val modulePos = stack.get(EMRegistries.ENTITY_MODULE_BLOCKPOS) ?: return
        val posString = "info.pos".withContext(FastColor.ARGB32.color(145, 142, 142))
        val string = " X=${modulePos.x} Y=${modulePos.y} Z=${modulePos.z}"
        val element = TextHelpers.withStyleComponent(string, FastColor.ARGB32.color(43, 255, 71))
        components.add(posString.copy().append(element))
    }

    override fun useOn(context: UseOnContext): InteractionResult {
        val level = context.level
        val pos = context.clickedPos
        val player = context.player ?: return InteractionResult.FAIL
        val bEntity = level.getEntityModule(pos) ?: return InteractionResult.FAIL

        if(player.isShiftKeyDown && context.itemInHand.get(EMRegistries.ENTITY_MODULE_BLOCKPOS) == pos){
            val current = bEntity.blockState.getValue(EntityModuleBlock.INVISIBLE)
            level.setBlockAndUpdate(pos, bEntity.blockState.setValue(EntityModuleBlock.INVISIBLE, !current))
            return InteractionResult.SUCCESS
        }

        context.itemInHand.set(EMRegistries.ENTITY_MODULE_BLOCKPOS, pos)
        level.playSound(null, pos, SoundEvents.RESPAWN_ANCHOR_CHARGE, SoundSource.BLOCKS, 1F, 2F)
        level.playSound(null, pos, SoundEvents.EVOKER_PREPARE_ATTACK, SoundSource.BLOCKS, 0.2F, 4F)
        return InteractionResult.SUCCESS
    }

    override fun use(
        level: Level,
        player: Player,
        interactionHand: InteractionHand,
    ): InteractionResultHolder<ItemStack> {
        val stack = player.getItemInHand(interactionHand)

        Modes.entries.forEach { return it.doOnToggle(stack, player) }

        return InteractionResultHolder.fail(stack)
    }

    fun useItemOnC(
        itemStack: ItemStack,
        level: Level,
        blockPos: BlockPos,
        player: Player,
        withScroll: Double = 0.0
    ): ItemInteractionResult {
        if(itemStack.item !is EntityModuleController) return ItemInteractionResult.FAIL

        val entity = level.getEntityModule(blockPos) ?: return ItemInteractionResult.FAIL
        val direction = player.nearestViewDirection.opposite
        val axis = direction.axis
        val newDirection = if (withScroll > 0) direction.opposite else direction

        fun inflateOrReduce(inflate: Double) {
            val sizeDelta = when (axis) {
                Direction.Axis.X -> Triple(inflate.toInt(), 0, 0)
                Direction.Axis.Y -> Triple(0, inflate.toInt(), 0)
                Direction.Axis.Z -> Triple(0, 0, inflate.toInt())
            }

            entity.adjustSize(sizeDelta.first, sizeDelta.second, sizeDelta.third)

            if (direction.axis == axis && direction.axisDirection == Direction.AxisDirection.NEGATIVE) {
                entity.adjustDirection(newDirection)
            }
            getSound(level, player.blockPosition(), SoundEvents.SOUL_ESCAPE.value())
        }

        when(getCurrentMode(itemStack)) {
            1 -> {
                entity.adjustDirection(newDirection)
                level.playSound(null, BlockPos.containing(entity.getInflate().center), SoundEvents.DYE_USE, SoundSource.BLOCKS, 1F, 2F)
            }
            2 -> inflateOrReduce(withScroll)
        }

        return ItemInteractionResult.SUCCESS
    }
}