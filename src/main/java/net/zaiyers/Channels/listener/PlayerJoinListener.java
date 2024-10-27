package net.zaiyers.Channels.listener;


import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.zaiyers.Channels.Channels;

public class PlayerJoinListener {
	
	@Subscribe
	public void onPlayerJoin(PostLoginEvent event) {
		if (Channels.getInstance().getChatter(event.getPlayer()) == null) {
			event.getPlayer().sendMessage(Component.text("Error while loading your chat data. You wont be able to chat :( Please contact an admin!")
					.color(NamedTextColor.RED));
			Channels.getInstance().getLogger().severe("Unable to load Chatter '" + event.getPlayer().getUsername() + "'/" + event.getPlayer().getUniqueId());
		}
	}
}
