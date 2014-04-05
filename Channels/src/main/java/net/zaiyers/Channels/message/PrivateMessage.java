package net.zaiyers.Channels.message;

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
