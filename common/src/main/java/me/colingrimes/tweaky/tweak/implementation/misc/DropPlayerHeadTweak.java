package me.colingrimes.tweaky.tweak.implementation.misc;

import me.colingrimes.tweaky.Tweaky;
import me.colingrimes.tweaky.menu.tweak.util.TweakItem;
import me.colingrimes.tweaky.tweak.type.ConfigurableTweak;
import me.colingrimes.tweaky.tweak.type.DefaultTweak;
import me.colingrimes.tweaky.util.misc.Random;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import javax.annotation.Nonnull;

public class DropPlayerHeadTweak extends DefaultTweak implements ConfigurableTweak {

	public DropPlayerHeadTweak(@Nonnull Tweaky plugin) {
		super(plugin, "drops_player_head");
	}

	@Nonnull
	@Override
	public TweakItem getGuiItem() {
		return menus.TWEAK_DROPS_PLAYER_HEAD.get().placeholder("{chance}", settings.TWEAK_DROPS_PLAYER_HEAD_CHANCE.get());
	}

	@EventHandler
	public void onDeath(@Nonnull PlayerDeathEvent event) {
		if (!(event.getDamageSource().getCausingEntity() instanceof Player) || !Random.chance(settings.TWEAK_DROPS_PLAYER_HEAD_CHANCE.get())) {
			return;
		}

		ItemStack item = new ItemStack(Material.PLAYER_HEAD);
		if (item.getItemMeta() instanceof SkullMeta skull) {
			skull.setOwningPlayer(event.getEntity());
			item.setItemMeta(skull);
		}

		Entity entity = event.getEntity();
		entity.getWorld().dropItemNaturally(entity.getLocation(), item);
	}
}
