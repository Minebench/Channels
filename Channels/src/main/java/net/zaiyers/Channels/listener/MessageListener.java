package net.zaiyers.Channels.listener;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.zaiyers.Channels.Channels;
import net.zaiyers.Channels.Chatter;
import net.zaiyers.Channels.message.ChannelMessage;
import net.zaiyers.Channels.message.Message;

public class MessageListener implements Listener {
	@EventHandler
	public void onMessageRecieve(ChatEvent event) {
		if (!(event.getSender() instanceof ProxiedPlayer) || event.isCommand()) { return; }
		
		ProxiedPlayer player = (ProxiedPlayer) event.getSender();
		
		Message msg;
		if (event.getMessage().startsWith("@")) {
			// TODO: is a private message
			// is private message
			
		} else {
			// message in default channel
			
			Chatter chatter = Channels.getInstance().getChatter(player.getUUID());
			if (chatter.getLastRecipient() != null) {
				// TODO: is a private message
				// private message
			} else {
				// channel message
				msg = new ChannelMessage(chatter, Channels.getInstance().getChannel(chatter.getChannel()), event.getMessage());
				msg.send();
				// do not pass through to servers
				event.setCancelled(true);
			}
		}
	}
}
