package me.colingrimes.tweaky.tweak.implementation.recipe;

import me.colingrimes.tweaky.Tweaky;
import me.colingrimes.tweaky.tweak.type.RecipeTweak;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapelessRecipe;

import javax.annotation.Nonnull;

public class RecipeGrassBlock extends RecipeTweak {

	public RecipeGrassBlock(@Nonnull Tweaky plugin) {
		super(plugin, "recipe_grass_block");
	}

	@Nonnull
	@Override
	protected Recipe recipe(@Nonnull NamespacedKey key) {
		return new ShapelessRecipe(key, new ItemStack(Material.GRASS_BLOCK))
				.addIngredient(Material.DIRT)
				.addIngredient(Material.BONE_MEAL);
	}
}
