package net.zaiyers.Channels.listener;

import com.google.common.collect.ImmutableMap;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerPostConnectEvent;
import net.zaiyers.Channels.Channel;
import net.zaiyers.Channels.Channels;
import net.zaiyers.Channels.Chatter;

public class ServerSwitchListener {

	@Subscribe
	public void onServerSwitch(ServerPostConnectEvent e) {
		if (e.getPlayer().getCurrentServer().isEmpty()) {
			return;
		}

		String serverName = e.getPlayer().getCurrentServer().get().getServerInfo().getName();
		
		if (Channels.getConfig().forceServerDefaultChannel(serverName)) {
			Channel channel = Channels.getInstance().getChannel(Channels.getConfig().getServerDefaultChannel(serverName));
			Chatter chatter = Channels.getInstance().getChatter(e.getPlayer());
			
			if (channel != null && !channel.getUUID().equals(chatter.getChannel())) {
				chatter.subscribe(channel);
				if (!chatter.hasPermission("channels.force-default-channel-bypass")) {
					chatter.setDefaultChannelUUID(channel.getUUID());
					Channels.notify(e.getPlayer(), "channels.chatter.default-channel-set", ImmutableMap.of("channel", channel.getName(), "channelColor", channel.getColor().toString()));
				} else {
					Channels.notify(e.getPlayer(), "channels.chatter.default-channel-set-bypassed", ImmutableMap.of("channel", channel.getName(), "channelColor", channel.getColor().toString()));
				}
			} else {
				Channels.getInstance().getLogger().warning("Can not enforce default channel on server '"+serverName+"'. Channel does not exist.");
			}
		}
	}
}
