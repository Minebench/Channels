package net.zaiyers.Channels.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import net.zaiyers.Channels.Channels;
import net.zaiyers.Channels.Chatter;

public class PlayerQuitListener {

	@Subscribe
	public void onPlayerQuit(DisconnectEvent event) {
		Chatter chatter = Channels.getInstance().getChatter(event.getPlayer());

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
