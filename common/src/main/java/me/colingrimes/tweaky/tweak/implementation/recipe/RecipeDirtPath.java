package me.colingrimes.tweaky.tweak.implementation.recipe;

import me.colingrimes.tweaky.Tweaky;
import me.colingrimes.tweaky.tweak.type.RecipeTweak;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

import javax.annotation.Nonnull;

public class RecipeDirtPath extends RecipeTweak {

	public RecipeDirtPath(@Nonnull Tweaky plugin) {
		super(plugin, "recipe_dirt_path");
	}

	@Nonnull
	@Override
	protected Recipe recipe(@Nonnull NamespacedKey key) {
		return new ShapedRecipe(key, new ItemStack(Material.DIRT_PATH, 3))
				.shape("DDD")
				.setIngredient('D', Material.DIRT);
	}
}
