package net.zaiyers.Channels.listener;

import com.google.common.collect.ImmutableMap;

import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.zaiyers.Channels.Channel;
import net.zaiyers.Channels.Channels;
import net.zaiyers.Channels.Chatter;

public class ServerSwitchListener implements Listener {
	@EventHandler
	public void onServerSwitch(ServerSwitchEvent e) {
		String serverName = e.getPlayer().getServer().getInfo().getName();
		
		if (Channels.getConfig().forceServerDefaultChannel(serverName)) {
			Channel channel = Channels.getInstance().getChannel(Channels.getConfig().getServerDefaultChannel(serverName));
			Chatter chatter = Channels.getInstance().getChatter(e.getPlayer().getUniqueId().toString());
			
			if (channel != null && !chatter.getChannel().equals(channel.getUUID())) {
				chatter.subscribe(channel.getUUID());
				chatter.setDefaultChannelUUID(channel.getUUID());
				Channels.notify(e.getPlayer(), "channels.chatter.default-channel-set", ImmutableMap.of("channel", channel.getName(), "channelColor", channel.getColor().toString()));
			} else {
				Channels.getInstance().getLogger().warning("Can not enforce default channel on server '"+serverName+"'. Channel does not exist.");
			}
		}
	}
}
