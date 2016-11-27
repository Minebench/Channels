package net.zaiyers.Channels.listener;

import java.io.IOException;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.zaiyers.Channels.Channel;
import net.zaiyers.Channels.Channels;
import net.zaiyers.Channels.Chatter;

public class PlayerJoinListener implements Listener {
	
	@EventHandler
	public void onPlayerJoin(PostLoginEvent event) {
		if (!Channels.getInstance().loadChatter(event.getPlayer().getUniqueId())) {
			event.getPlayer().sendMessage(ChatColor.RED + "Error while loading your chat data. You wont be able to chat :( Please contact an admin!");
			Channels.getInstance().getLogger().severe("Unable to load Chatter '" + event.getPlayer().getName() + "'/" + event.getPlayer().getUniqueId());
		}
	}
}
