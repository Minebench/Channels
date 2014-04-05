package net.zaiyers.Channels.listener;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.zaiyers.Channels.Channels;
import net.zaiyers.Channels.ChannelsChatEvent;
import net.zaiyers.Channels.Chatter;
import net.zaiyers.Channels.command.ChannelsCommand;
import net.zaiyers.Channels.command.PMCommand;
import net.zaiyers.Channels.message.ChannelMessage;
import net.zaiyers.Channels.message.Message;
import net.zaiyers.Channels.message.PrivateMessage;

public class MessageListener implements Listener {
	@EventHandler
	public void onMessageRecieve(ChatEvent event) {
		if (!(event.getSender() instanceof ProxiedPlayer) || event.isCommand()) { return; }
		
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
			Chatter chatter = Channels.getInstance().getChatter(player.getUUID());
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
				Message msg = new ChannelMessage(chatter, Channels.getInstance().getChannel(chatter.getChannel()), event.getMessage());
				ChannelsChatEvent chatEvent = new ChannelsChatEvent(msg);
				if (!Channels.getInstance().getProxy().getPluginManager().callEvent( chatEvent ).isCancelled()) {
					msg.send();
				}
			}
		}
		

		// do not pass to server
		event.setCancelled(true);
	}
}
