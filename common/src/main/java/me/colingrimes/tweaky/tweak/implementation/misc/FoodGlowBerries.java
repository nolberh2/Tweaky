package me.colingrimes.tweaky.tweak.implementation.misc;

import me.colingrimes.tweaky.Tweaky;
import me.colingrimes.tweaky.tweak.event.TweakHandler;
import me.colingrimes.tweaky.tweak.type.ConfigurableTweak;
import me.colingrimes.tweaky.tweak.type.DefaultTweak;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import javax.annotation.Nonnull;

public class FoodGlowBerries extends DefaultTweak implements ConfigurableTweak {

	public FoodGlowBerries(@Nonnull Tweaky plugin) {
		super(plugin, "food_glow_berries");
	}

	@TweakHandler
	public void onPlayerItemConsume(@Nonnull PlayerItemConsumeEvent event) {
		if (event.getItem().getType() == Material.GLOW_BERRIES) {
			int time = settings.TWEAK_FOOD_GLOW_BERRIES_SECONDS.get() * 20;
			event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, time, 0));
		}
	}
}
