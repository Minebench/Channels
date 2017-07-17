package net.zaiyers.Channels.listener;

import com.google.common.collect.ImmutableMap;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.zaiyers.Channels.Channel;
import net.zaiyers.Channels.Channels;
import net.zaiyers.Channels.ChannelsChatEvent;
import net.zaiyers.Channels.Chatter;
import net.zaiyers.Channels.command.ChannelsCommand;
import net.zaiyers.Channels.command.PMCommand;
import net.zaiyers.Channels.message.ChannelMessage;
import net.zaiyers.Channels.message.Message;
import net.zaiyers.Channels.message.PrivateMessage;

public class MessageListener implements Listener {
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onMessageRecieve(ChatEvent event) {
		if (event.isCancelled() || !(event.getSender() instanceof ProxiedPlayer) || event.isCommand()) {
			return;
		}
		
        boolean canceled = true;
		ProxiedPlayer player = (ProxiedPlayer) event.getSender();
		
		if (event.getMessage().charAt(0) == '@' && event.getMessage().length() > 0) {
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
					if (!Channels.getInstance().getProxy().getPluginManager().callEvent( chatEvent ).isCancelled()) {
						msg.send();
						recipient.setLastPrivateSender(chatter);
					}
				}
			} else {
				// channel message
				Channel chan = Channels.getInstance().getChannel(chatter.getChannel());

				if (chatter.hasPermission(chan, "speak")) {
                    if (!chan.isGlobal() && !chan.getServers().contains(chatter.getPlayer().getServer().getInfo().getName())) {
                        // channel is not available on this server
                        String serverDefaultChannel = Channels.getConfig().getServerDefaultChannel(chatter.getPlayer().getServer().getInfo().getName());
                        if (serverDefaultChannel != null) {
                            // use server default channel
                            chan = Channels.getInstance().getChannel(serverDefaultChannel);
                        } else {
                            // use global default channel
                            chan = Channels.getInstance().getChannel(Channels.getConfig().getDefaultChannelUUID());
                        }

                        if (chan.doAutojoin() && chatter.hasPermission(chan, "subscribe")) {
                            chatter.setDefaultChannelUUID(chan.getUUID());
                            chatter.subscribe(chan);
                            Channels.notify(chatter.getPlayer(), "channels.chatter.default-channel-set", ImmutableMap.of("channel", chan.getName(), "channelColor", chan.getColor().toString()));
                        } // else the guy is screwed due to a misconfiguration - see Channels.checkSanity()
                    }

                    Message msg = new ChannelMessage(chatter, Channels.getInstance().getChannel(chatter.getChannel()), event.getMessage());
                    ChannelsChatEvent chatEvent = new ChannelsChatEvent(msg);
                    if (!Channels.getInstance().getProxy().getPluginManager().callEvent(chatEvent).isCancelled()) {
                        if (Channels.getInstance().getChannel(chatter.getChannel()).isBackend()) {
                            canceled = false;
                        } else {
                            msg.send();
                        }
                    }
                } else {
                    // Chatter cannot speak in channel
                    Channels.notify(chatter.getPlayer(), "channels.permission.channel-no-speak", ImmutableMap.of("channel", chan.getName(), "channelColor", chan.getColor().toString()));
                }
			}
		}
		

		// do not pass to server
		event.setCancelled(canceled);
	}
}
