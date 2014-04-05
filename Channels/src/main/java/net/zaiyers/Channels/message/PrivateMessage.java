package net.zaiyers.Channels.message;

import com.google.common.collect.ImmutableMap;

import net.md_5.bungee.api.chat.TextComponent;
import net.zaiyers.Channels.Channels;
import net.zaiyers.Channels.Chatter;

public class PrivateMessage extends AbstractMessage {
	private Chatter sender;
	private Chatter recipient;
	
	public PrivateMessage(Chatter sender, Chatter recipient, String message) {
		this.sender = sender;
		this.recipient = recipient;
		rawMessage = message;
	}

	public void send() {
		processMessage();
		
		if (recipient.isAFK()) {
			if (recipient.getAFKMessage() != null) {
				Channels.notify(sender.getPlayer(), "channels.chatter.is-afk-with-msg", ImmutableMap.of("chatter", recipient.getName(), "msg", recipient.getAFKMessage()));
			} else {
				Channels.notify(sender.getPlayer(), "channels.chatter.is-afk", ImmutableMap.of("chatter", recipient.getName()));
			}
		} else if (recipient.isDND()) {
			if (recipient.getDNDMessage() != null) {
				Channels.notify(sender.getPlayer(), "channels.chatter.is-dnd-with-msg", ImmutableMap.of("chatter", recipient.getName(), "msg", recipient.getDNDMessage()));
			} else {
				Channels.notify(sender.getPlayer(), "channels.chatter.is-dnd", ImmutableMap.of("chatter", recipient.getName()));
			}
			return;
		}
		
		recipient.sendMessage(this);
	}
	
	/**
	 * generate private message output
	 * @return
	 */
	private void processMessage() {
		processedMessage = new TextComponent(
			Channels.getConfig().getPrivateMessageFormat()	.replaceAll("%sender%", sender.getName())
															.replaceAll("%recipient%", recipient.getName())
															.replaceAll("%message%", rawMessage)
		);
	}
}
