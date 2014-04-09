package net.zaiyers.Channels.message;

import net.md_5.bungee.api.chat.TextComponent;

public interface Message {
	/**
	 * sends the message to its recipients
	 */
	public void send();

	/**
	 * get the final message
	 */
	public TextComponent getProcessedMessage();
}
