package org.shaydee.entitymodules.item

import net.minecraft.core.component.DataComponents.CUSTOM_MODEL_DATA
import net.minecraft.world.item.Item
import net.minecraft.world.item.Item.Properties
import net.minecraft.world.item.component.CustomModelData
import org.shaydee.entitymodules.block.entity_module.EntityModuleBlockEntity
import org.shaydee.entitymodules.data.EMType

class Module (val getType: EMType) : Item(Properties())