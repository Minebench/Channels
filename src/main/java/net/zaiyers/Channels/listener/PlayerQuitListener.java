package net.zaiyers.Channels.listener;

import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.zaiyers.Channels.Channels;
import net.zaiyers.Channels.Chatter;

public class PlayerQuitListener implements Listener {
	@EventHandler
	public void onPlayerQuit(PlayerDisconnectEvent event) {
		Chatter chatter = Channels.getInstance().getChatter(event.getPlayer().getUniqueId());

		if (chatter != null) {
			// unsubscribe from channels
			for (String channelUUID : chatter.getSubscriptions()) {
				if (Channels.getInstance().getChannel(channelUUID) != null) {
					Channels.getInstance().getChannel(channelUUID).unsubscribe(chatter);
				} else {
					// channel has been removed
					chatter.unsubscribe(channelUUID);
				}
			}

			// save configuration
			chatter.save();
		}
		
		// remove chatter
		Channels.getInstance().removeChatter(event.getPlayer().getUniqueId());
	}
}
