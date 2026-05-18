package me.colingrimes.tweaky.tweak.implementation.block;

import me.colingrimes.tweaky.Tweaky;
import me.colingrimes.tweaky.menu.tweak.util.TweakItem;
import me.colingrimes.tweaky.tweak.type.ConfigurableTweak;
import me.colingrimes.tweaky.tweak.type.DefaultTweak;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.EquipmentSlotGroup;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public class PathSpeedTweak extends DefaultTweak implements ConfigurableTweak {

	private final NamespacedKey MOVEMENT_SPEED_KEY;
	private final Set<Player> players = new HashSet<>();

	public PathSpeedTweak(@Nonnull Tweaky plugin) {
		super(plugin, "path_speed");
		this.MOVEMENT_SPEED_KEY = new NamespacedKey(plugin, "path_speed");
	}

	@Override
	protected void onDisable() {
		players.forEach(this::removeModifier);
		players.clear();
	}

	@Nonnull
	@Override
	public TweakItem getGuiItem() {
		return menus.TWEAK_PATH_SPEED.get().placeholder("{multiplier}", settings.TWEAK_PATH_SPEED_MULTIPLIER.get());
	}

	@EventHandler
	public void onPlayerMove(@Nonnull PlayerMoveEvent event) {
		if (event.getTo() == null) {
			return;
		}

		Location from = event.getFrom();
		Location to = event.getTo();
		if (from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY() && from.getBlockZ() == to.getBlockZ()) {
			return;
		}

		Player player = event.getPlayer();
		if (!hasPermission(player)) {
			return;
		}

		boolean isPath = to.getBlock().getType() == Material.DIRT_PATH;
		if (isPath) {
			addModifier(player);
			players.add(player);
		} else {
			removeModifier(player);
			players.remove(player);
		}
	}

	/**
	 * Adds the movement modifier to the player.
	 *
	 * @param player the player
	 */
	private void addModifier(@Nonnull Player player) {
		AttributeInstance movement = player.getAttribute(Attribute.MOVEMENT_SPEED);
		Optional<AttributeModifier> modifier = getModifier(movement);
		if (movement != null && modifier.isEmpty()) {
			movement.addModifier(new AttributeModifier(
					MOVEMENT_SPEED_KEY,
					settings.TWEAK_PATH_SPEED_MULTIPLIER.get() - 1,
					AttributeModifier.Operation.ADD_SCALAR,
					EquipmentSlotGroup.ANY
			));
		}
	}

	/**
	 * Removes the movement modifier from the player.
	 *
	 * @param player the player
	 */
	private void removeModifier(@Nonnull Player player) {
		AttributeInstance movement = player.getAttribute(Attribute.MOVEMENT_SPEED);
		Optional<AttributeModifier> modifier = getModifier(movement);
		if (movement != null && modifier.isPresent()) {
			movement.removeModifier(modifier.get());
		}
	}

	/**
	 * Gets the movement modifier from the attribute instance.
	 *
	 * @param attribute the attribute
	 * @return the movement modifier
	 */
	@Nonnull
	private Optional<AttributeModifier> getModifier(@Nullable AttributeInstance attribute) {
		if (attribute == null) {
			return Optional.empty();
		}
		return attribute.getModifiers().stream().filter(m -> m.getKey().equals(MOVEMENT_SPEED_KEY)).findFirst();
	}
}
