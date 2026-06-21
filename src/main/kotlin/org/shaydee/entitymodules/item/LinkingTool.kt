package org.shaydee.entitymodules.item

import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import org.shaydee.entitymodules.block.entity_module.EntityModuleBlockEntity
import org.shaydee.entitymodules.helpers.EMHelpers.getEntityModule
import org.shaydee.entitymodules.helpers.EMHelpers.withContext
import org.shaydee.entitymodules.registry.EMRegistries

class LinkingTool : Item(Properties()) {
    val componentBPos = EMRegistries.ENTITY_MODULE_BLOCKPOS

    override fun use(
        level: Level,
        player: Player,
        interactionHand: InteractionHand,
    ): InteractionResultHolder<ItemStack> {
        val stack = player.getItemInHand(interactionHand)
        stack.remove { componentBPos }
        player.displayClientMessage("linking.reset".withContext(), true)
        return InteractionResultHolder.consume(stack)
    }

    override fun useOn(context: UseOnContext): InteractionResult {
        val level = context.level
        val pos = context.clickedPos
        val player = context.player ?: return InteractionResult.FAIL
        val stack = context.itemInHand
        val getStoredPos = stack.get(componentBPos)

        val bEntity = level.getEntityModule(pos) ?: return InteractionResult.FAIL

        if(getStoredPos == bEntity.blockPos) return InteractionResult.FAIL

        getStoredPos?.let {
            if(bEntity.getExternalBounding() != null) {
                bEntity.externalBoundingPos = EntityModuleBlockEntity.FAKE_POS
                player.displayClientMessage("linking.removed".withContext(), true)
                return InteractionResult.SUCCESS
            }

            bEntity.externalBoundingPos = it
            player.displayClientMessage("linking.link_set".withContext(), true)
            return InteractionResult.SUCCESS
        }

        stack.set(componentBPos, bEntity.blockPos)
        player.displayClientMessage("linking.link_copied".withContext(), true)
        return InteractionResult.SUCCESS;
    }

}