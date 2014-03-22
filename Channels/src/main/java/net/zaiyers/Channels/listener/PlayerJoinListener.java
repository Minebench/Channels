package net.zaiyers.Channels.listener;

import java.io.IOException;
import java.util.UUID;

import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.zaiyers.Channels.Channel;
import net.zaiyers.Channels.Channels;
import net.zaiyers.Channels.Chatter;

public class PlayerJoinListener implements Listener {
	
	@EventHandler
	public void onPlayerJoin(PostLoginEvent event) {		
		try {
			Chatter chatter = new Chatter(event.getPlayer());
			
			for (UUID channelUUID: chatter.getSubscriptions()) {
				// channel exists
				if (Channels.getInstance().getChannel(channelUUID) != null) {
					// I'm allowed to join
					if (chatter.hasPermission(Channels.getInstance().getChannel(channelUUID), "subscribe")) {
						Channels.getInstance().getChannel(channelUUID).subscribe(chatter);
					} else if (channelUUID.equals(Channels.getConfig().getDefaultChannelUUID())) {
						Channels.getInstance().getLogger().warning("Chatter '"+chatter.getName()+"' is not allowed to join the default channel");
					}
				} else {
					//TODO: remove debugging
					System.out.println("channel "+channelUUID+" is unknown");
				}
			}
			
			// check for autojoin channels
			for (Channel channel: Channels.getInstance().getChannels().values()) {
				if (channel.doAutojoin() && chatter.hasPermission(channel, "subscribe")) {
					// joining twice doesn't matter, caught elsewhere
					Channels.getInstance().getChannel(channel.getUUID()).subscribe(chatter);
				}
			}
			
			Channels.getInstance().addChatter(chatter);			
		} catch (IOException e) {
			Channels.getInstance().getLogger().severe("Unable to load Chatter '"+event.getPlayer().getName()+"'");
			e.printStackTrace();
		}
	}
}
