package net.zaiyers.Channels.listener;

import java.util.UUID;

import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.zaiyers.Channels.Channels;
import net.zaiyers.Channels.Chatter;

public class PlayerQuitListener implements Listener {
	@EventHandler
	public void onPlayerQuit(PlayerDisconnectEvent event) {
		Chatter chatter = Channels.getInstance().getChatter(event.getPlayer().getUUID());
		
		// save configuration
		chatter.save();
		
		// unsubscribe from channels
		for (UUID channelUUID: chatter.getSubscriptions()) {
			Channels.getInstance().getChannel(channelUUID).unsubscribe(chatter);
		}
		
		// remove chatter
		Channels.getInstance().removeChatter(chatter);
	}
}
