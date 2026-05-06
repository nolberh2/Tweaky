package me.colingrimes.tweaky.tweak.type;

import me.colingrimes.tweaky.Tweaky;
import me.colingrimes.tweaky.tweak.Tweak;
import me.colingrimes.tweaky.util.bukkit.NBT;
import me.colingrimes.tweaky.util.bukkit.Players;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a {@link Tweak} that can be toggleable via a command.
 */
public abstract class ToggleTweak extends DefaultTweak implements CommandExecutor {

	private final Set<UUID> toggleOn = new HashSet<>();
	private final String TOGGLE_KEY;
	private final String command;
	private final boolean defToggle;

	/**
	 * Constructs a toggle tweak.
	 *
	 * @param plugin the plugin
	 * @param id the id of the tweak
	 * @param command the command used to toggle the tweak
	 * @param defToggle the default value of the toggle
	 */
	public ToggleTweak(@Nonnull Tweaky plugin, @Nonnull String id, @Nonnull String command, boolean defToggle) {
		super(plugin, id);
		this.TOGGLE_KEY = id + "_toggle";
		this.command = command;
		this.defToggle = defToggle;
	}

	@Override
	public final void enable() {
		super.enable();
		plugin.getCommandManager().register(command, this);
		Players.forEach(this::checkToggle);
	}

	@Override
	public final void disable() {
		super.disable();
		plugin.getCommandManager().unregister(command);
		toggleOn.clear();
	}

	@Override
	public boolean onCommand(@Nonnull CommandSender sender, @Nonnull Command command, @Nonnull String label, @Nonnull String[] args) {
		// All toggle tweaks can be manually activated on a player via admin command.
		if (sender.hasPermission("tweaky.admin") && args.length >= 1) {
			Player target = Players.get(args[0]).orElse(null);
			if (target == null) {
				msg.ADMIN_FAILURE_INVALID_PLAYER.replace("{player}", args[0]).send(sender);
				return true;
			}

			boolean on = !toggleOn.contains(target.getUniqueId());
			if (args.length >= 2) {
				switch (args[1].toLowerCase()) {
					case "on" -> on = true;
					case "off" -> on = false;
				}
			}

			if (on) {
				toggleOn.add(target.getUniqueId());
				NBT.setTag(target, TOGGLE_KEY, true);
				activate(target);
				msg.ADMIN_SUCCESS_TOGGLE_ON_PLAYER.replace("{tweak}", id).replace("{player}", target.getName()).send(sender);
			} else {
				toggleOn.remove(target.getUniqueId());
				NBT.setTag(target, TOGGLE_KEY, false);
				deactivate(target);
				msg.ADMIN_SUCCESS_TOGGLE_OFF_PLAYER.replace("{tweak}", id).replace("{player}", target.getName()).send(sender);
			}

			return true;
		}

		if (!(sender instanceof Player player) || !hasPermission(player)) {
			return true;
		}

		UUID uuid = player.getUniqueId();
		if (toggleOn.contains(uuid)) {
			toggleOn.remove(uuid);
			NBT.setTag(player, TOGGLE_KEY, false);
			Players.sound(player, Sound.BLOCK_NOTE_BLOCK_BASS);
			deactivate(player);
		} else {
			toggleOn.add(uuid);
			NBT.setTag(player, TOGGLE_KEY, true);
			Players.sound(player, Sound.BLOCK_NOTE_BLOCK_PLING);
			activate(player);
		}

		return true;
	}

	/**
	 * Checks the toggle value of the player and adds it to the set.
	 * <p>
	 * If no toggle value is present, it will use the default toggle value given.
	 *
	 * @param player the player
	 */
	public void checkToggle(@Nonnull Player player) {
		if (!NBT.hasTag(player, TOGGLE_KEY, Boolean.class)) {
			NBT.setTag(player, TOGGLE_KEY, defToggle);
		}

		boolean toggle = NBT.getTag(player, TOGGLE_KEY, Boolean.class).orElse(defToggle);
		if (toggle) {
			toggleOn.add(player.getUniqueId());
		}
	}

	/**
	 * Removes the toggle data from the player.
	 *
	 * @param player the player
	 */
	public void removeToggle(@Nonnull Player player) {
		toggleOn.remove(player.getUniqueId());
	}

	/**
	 * Checks if the player has this tweak toggled on.
	 *
	 * @param entity the player
	 * @return true if the tweak is toggled on for the player
	 */
	public boolean isToggled(@Nonnull Entity entity) {
		return entity instanceof Player player && toggleOn.contains(player.getUniqueId());
	}

	/**
	 * Activates the tweak if the player has it toggled on and has permission to use it.
	 *
	 * @param player the player
	 */
	public void activate(@Nonnull Player player) {
		if (isToggled(player) && hasPermission(player)) {
			onActivate(player);
		}
	}

	/**
	 * Deactivates the tweak if the player has it toggled off and has permission to use it.
	 *
	 * @param player the player
	 */
	public void deactivate(@Nonnull Player player) {
		if (!isToggled(player) && hasPermission(player)) {
			onDeactivate(player);
		}
	}

	/**
	 * Method that should be called when the toggle is activated.
	 *
	 * @param player the player
	 */
	protected abstract void onActivate(@Nonnull Player player);

	/**
	 * Method that should be called when the toggle is deactivated.
	 *
	 * @param player the player
	 */
	protected abstract void onDeactivate(@Nonnull Player player);
}
