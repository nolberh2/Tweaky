package me.colingrimes.tweaky.tweak.implementation.convenience;

import me.colingrimes.tweaky.Tweaky;
import me.colingrimes.tweaky.event.PlayerInteractBlockEvent;
import me.colingrimes.tweaky.tweak.event.TweakHandler;
import me.colingrimes.tweaky.tweak.properties.TweakProperties;
import me.colingrimes.tweaky.tweak.type.DefaultTweak;
import me.colingrimes.tweaky.util.bukkit.Sounds;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.ShulkerBox;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public class ShulkerBoxInsertTweak extends DefaultTweak {

	public ShulkerBoxInsertTweak(@Nonnull Tweaky plugin) {
		super(plugin, "shulker_box_insert");
	}

	@Override
	protected void configureProperties(@Nonnull TweakProperties properties) {
		properties.getGuard()
				.interactable()
				.sneaking()
				.leftClick()
				.item(Material.SHULKER_BOX)
				.block(Material.CHEST);
	}

	@TweakHandler(ignoreCancelled = true)
	public void onPlayerInteract(@Nonnull PlayerInteractBlockEvent event) {
		ItemStack item = event.getItem();
		if (!(item.getItemMeta() instanceof BlockStateMeta meta) || !(meta.getBlockState() instanceof ShulkerBox shulker)) {
			return;
		}

		Block block = event.getBlock();
		if (!(block.getState() instanceof Chest chest)) {
			return;
		}

		ItemStack[] items = Arrays.stream(shulker.getInventory().getContents()).filter(Objects::nonNull).toArray(ItemStack[]::new);
		if (items.length == 0) {
			return;
		}

		Map<Integer, ItemStack> leftovers = chest.getInventory().addItem(items);
		shulker.getInventory().setContents(leftovers.values().toArray(new ItemStack[0]));
		meta.setBlockState(shulker);
		item.setItemMeta(meta);
		Sounds.play(block, Sound.ITEM_BUNDLE_INSERT);
	}
}
