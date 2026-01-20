package org.shaydee.entitymodules.datagen

import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.data.recipes.RecipeProvider.has
import net.minecraft.data.recipes.ShapedRecipeBuilder
import net.minecraft.world.flag.FeatureFlagSet
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.level.ItemLike
import net.neoforged.neoforge.common.Tags
import org.shaydee.entitymodules.registry.EMRegistries
import java.util.List
import java.util.concurrent.CompletableFuture

class RecipeProvider(
    pOutput: PackOutput,
    pRegistries: CompletableFuture<HolderLookup.Provider?>
) : RecipeProvider(pOutput, pRegistries) {

    override fun buildRecipes(recipeOutput: RecipeOutput) {
        entityModuleRecipe(recipeOutput, EMRegistries.ENTITY_MODULE_BLOCK.asItem())
        entityModuleControllerRecipe(recipeOutput, EMRegistries.ENTITY_MODULE_CONTROLLER)
    }

    fun entityModuleRecipe(output: RecipeOutput, result: Item) {
        val redStone = Items.REDSTONE
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, result, 4)
            .define('M', Items.STONE)
            .define('Z', redStone)
            .define('X', Items.COMPARATOR)
            .pattern("ZMZ")
            .pattern("MXM")
            .pattern("ZMZ")
            .unlockedBy("redstone", has(redStone))
            .save(output)
    }


    fun entityModuleControllerRecipe(output: RecipeOutput, result: Item) {
        val item = EMRegistries.ENTITY_MODULE_BLOCK.asItem()
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, result, 4)
            .define('W', Items.SAND)
            .define('M', Items.REPEATER)
            .define('X', item)
            .pattern(" W ")
            .pattern("MXM")
            .pattern(" W ")
            .unlockedBy("entity_module_block", has(item))
            .save(output)
    }
}