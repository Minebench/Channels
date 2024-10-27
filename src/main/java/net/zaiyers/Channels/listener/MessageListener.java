package net.zaiyers.Channels.listener;

import com.google.common.collect.ImmutableMap;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.PlayerChatEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import net.zaiyers.Channels.Channel;
import net.zaiyers.Channels.Channels;
import net.zaiyers.Channels.events.ChannelsChatEvent;
import net.zaiyers.Channels.Chatter;
import net.zaiyers.Channels.command.ChannelsCommand;
import net.zaiyers.Channels.command.PMCommand;
import net.zaiyers.Channels.message.ChannelMessage;
import net.zaiyers.Channels.message.Message;
import net.zaiyers.Channels.message.PrivateMessage;

import java.util.Optional;

public class MessageListener {

	@Subscribe(order = PostOrder.LATE)
	public void onMessageRecieve(PlayerChatEvent event) {
		if (!event.getResult().isAllowed() || event.getMessage().startsWith("/")) {
			return;
		}

		boolean canceled = true;
		Player player = event.getPlayer();

		if (!event.getMessage().isEmpty() && event.getMessage().charAt(0) == '@') {
			//private message
			String[] splittedMsg;
			if (event.getMessage().length() > 1) {
				splittedMsg = event.getMessage().split(" ");
				splittedMsg[0] = splittedMsg[0].substring(1);
			} else {
				splittedMsg = new String[0];
			}

			ChannelsCommand cmd = new PMCommand(player, splittedMsg);
			cmd.execute();
		} else {
			// message in default channel
			Chatter chatter = Channels.getInstance().getChatter(player);
			if (chatter.getLastRecipient() != null) {
				Chatter recipient = Channels.getInstance().getChatter(chatter.getLastRecipient());
				if (recipient == null) {
					Channels.notify(player, "channels.chatter.recipient-offline");
				} else {
					Message msg = new PrivateMessage(chatter, recipient, event.getMessage());
					ChannelsChatEvent chatEvent = new ChannelsChatEvent(msg);
					if (!Channels.getInstance().getProxy().getEventManager().fire(chatEvent).isCancelled()) {
						msg.send(chatEvent.isHidden());
						if (!chatEvent.isHidden()) {
							recipient.setLastPrivateSender(chatter);
						}
					}
				}
			} else {
				// channel message
				Channel chan = Channels.getInstance().getChannel(chatter.getChannel());

				Optional<ServerConnection> server = player.getCurrentServer();
				if (chatter.hasPermission(chan, "speak") && server.isPresent()) {
					String serverName = server.get().getServerInfo().getName();
					if (!chan.isGlobal() && !chan.getServers().contains(serverName)) {
						// channel is not available on this server
						String serverDefaultChannel = Channels.getConfig().getServerDefaultChannel(serverName);
						if (serverDefaultChannel != null) {
							// use server default channel
							chan = Channels.getInstance().getChannel(serverDefaultChannel);
						} else {
							// use global default channel
							chan = Channels.getInstance().getChannel(Channels.getConfig().getDefaultChannelUUID());
						}

						if (chan != null && chan.doAutojoin() && chatter.hasPermission(chan, "subscribe")) {
							chatter.setDefaultChannelUUID(chan.getUUID());
							chatter.subscribe(chan);
							Channels.notify(chatter.getPlayer(), "channels.chatter.default-channel-set", ImmutableMap.of("channel", chan.getName(), "channelColor", chan.getColor().toString()));
						} else {
							// else the guy is screwed due to a misconfiguration - see Channels.checkSanity()
							event.setResult(PlayerChatEvent.ChatResult.denied());
							return;
						}
					}

					ChannelMessage msg = new ChannelMessage(chatter, chan, event.getMessage());
					ChannelsChatEvent chatEvent = new ChannelsChatEvent(msg);
					if (!Channels.getInstance().getProxy().getEventManager().fire(chatEvent).isCancelled()) {
						if (msg.getChannel().isBackend()) {
							canceled = false;
						} else {
							msg.send(chatEvent.isHidden());
						}
					}
				} else {
					// Chatter cannot speak in channel
					Channels.notify(chatter.getPlayer(), "channels.permission.channel-no-speak", ImmutableMap.of("channel", chan.getName(), "channelColor", chan.getColor().toString()));
				}
			}
		}


		// do not pass to server
		if (canceled) {
			event.setResult(PlayerChatEvent.ChatResult.denied());
		}
	}
}
