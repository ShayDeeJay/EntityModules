package org.shaydee.entitymodules.item

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction.Axis
import net.minecraft.core.Direction.AxisDirection
import net.minecraft.core.component.DataComponents.CUSTOM_MODEL_DATA
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents.*
import net.minecraft.sounds.SoundSource.BLOCKS
import net.minecraft.util.FastColor.ARGB32.color
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.ItemInteractionResult.FAIL
import net.minecraft.world.ItemInteractionResult.SUCCESS
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.item.component.CustomModelData
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.SoundType
import org.shaydee.entitymodules.block.entity_module.EntityModuleBlockEntity
import org.shaydee.entitymodules.helpers.EMHelpers
import org.shaydee.entitymodules.registry.EMRegistries
import org.shaydee.shaydeeapi.helpers.TextHelpers

class EntityModuleController(
    properties: Properties = Properties().component(CUSTOM_MODEL_DATA, CustomModelData(1))
) : Item(properties) {

    companion object {
        fun getCurrentMode(stack: ItemStack): Int = stack.get(CUSTOM_MODEL_DATA)?.value ?: 1
    }

    enum class MODES (
        val mode: String,
        val colour: Int
    ){
        POSITION("position", color(181, 43, 255)),
        INFLATE("size", color(43, 255, 71)),
        RESET("reset", color(255, 75, 43)),
    }

    override fun getHighlightTip(
        item: ItemStack,
        displayName: Component,
    ): Component {
        return super.getHighlightTip(item, displayName).copy().append(" - ").append(modeText(item))
    }

    fun getSound(level: Level, blockPos: BlockPos, soundEvent: SoundEvent, pitch: Float = 2F) {
        level.playSound(null, blockPos, soundEvent, BLOCKS, 1F, pitch)
        level.playSound(null, blockPos, UI_BUTTON_CLICK.value(), BLOCKS, 1F, pitch)
    }

    override fun appendHoverText(
        stack: ItemStack,
        tooltipContext: TooltipContext,
        components: MutableList<Component?>,
        flag: TooltipFlag,
    ) {
        components.add(modeText(stack))
        val getBoundEM = stack.get(EMRegistries.ENTITY_MODULE_BLOCKPOS) ?: return
        val posString = TextHelpers.withStyleComponentTrans("entitymodules.info.pos", color(145, 142, 142))
        val string = " x=" + getBoundEM.x + " y=" + getBoundEM.y + " z=" + getBoundEM.z
        val element = TextHelpers.withStyleComponent(string, color(43, 255, 71))
        components.add(posString.copy().append(element))
    }

    private fun modeText(stack: ItemStack): MutableComponent {
        val element = TextHelpers.withStyleComponentTrans("entitymodules.info.mode", color(145, 142, 142))
        val getIndex = getCurrentMode(stack)
        val mode = MODES.entries[getIndex-1]
        val text = TextHelpers.withStyleComponentTrans("entitymodules.modes." + mode.mode, mode.colour)
        return element.copy().append(text)
    }

    override fun useOn(context: UseOnContext): InteractionResult {
        val level = context.level;
        val pos = context.clickedPos
        val isEntity = level.getBlockEntity(pos) is EntityModuleBlockEntity
        if(isEntity) {
            context.itemInHand.set(EMRegistries.ENTITY_MODULE_BLOCKPOS, pos)
            level.playSound(null, pos, RESPAWN_ANCHOR_CHARGE, BLOCKS, 1F, 2F)
            level.playSound(null, pos, EVOKER_PREPARE_ATTACK, BLOCKS, 0.2F, 4F)

            return InteractionResult.SUCCESS;
        }

        return super.useOn(context)
    }

    override fun use(
        level: Level,
        player: Player,
        interactionHand: InteractionHand,
    ): InteractionResultHolder<ItemStack?> {
        val stack = player.getItemInHand(interactionHand)
        val fail = InteractionResultHolder.fail<ItemStack>(stack)
        val getIndex = getCurrentMode(stack)
        val maxEntries = MODES.entries.size
        val index = if(getIndex == maxEntries) 1 else getIndex + 1
        val getBoundEM = stack.get(EMRegistries.ENTITY_MODULE_BLOCKPOS) ?: return fail

        if(player.isShiftKeyDown){
            stack.set(CUSTOM_MODEL_DATA, CustomModelData(index))
            player.displayClientMessage(modeText(stack), true)
            player.playSound(SoundType.PINK_PETALS.placeSound, 1f, 2f)
            player.playSound(SoundType.SCULK.placeSound, 1f, 2f)
            return InteractionResultHolder.consume(stack)
        } else {
            val mode = getCurrentMode(stack)
            val entity: EntityModuleBlockEntity = EMHelpers.getEntityModule(level, getBoundEM) ?: return fail
            if(mode == 3){
                entity.resetBounding()
                getSound(level, player.blockPosition(), BEACON_DEACTIVATE)
                return InteractionResultHolder.consume(stack)
            }
        }

        return fail
    }

    fun useItemOnC(
        itemStack: ItemStack,
        level: Level,
        blockPos: BlockPos,
        player: Player,
        withScroll: Double = 0.0
    ): ItemInteractionResult {
        if(itemStack.item !is EntityModuleController) return FAIL

        val entity: EntityModuleBlockEntity = EMHelpers.getEntityModule(level, blockPos) ?: return FAIL
        val direction = player.nearestViewDirection.opposite
        val axis = direction.axis
        val newDirection = if (withScroll > 0) direction.opposite else direction

        fun inflateOrReduce(inflate: Double) {
            val sizeDelta = when (axis) {
                Axis.X -> Triple(inflate.toInt(), 0, 0)
                Axis.Y -> Triple(0, inflate.toInt(), 0)
                Axis.Z -> Triple(0, 0, inflate.toInt())
            }

            entity.adjustSize(sizeDelta.first, sizeDelta.second, sizeDelta.third)

            if (direction.axis == axis && direction.axisDirection == AxisDirection.NEGATIVE) {
                entity.adjustDirection(newDirection)
            }
            getSound(level, player.blockPosition(), SOUL_ESCAPE.value())
        }

        when(getCurrentMode(itemStack)) {
            1 -> {
                entity.adjustDirection(newDirection)
                getSound(level, player.blockPosition(), SLIME_SQUISH)
            }
            2 -> inflateOrReduce(withScroll)
        }

        return SUCCESS
    }
}