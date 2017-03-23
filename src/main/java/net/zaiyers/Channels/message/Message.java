package net.zaiyers.Channels.message;

import net.md_5.bungee.api.CommandSender;
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
	
	/**
	 * get the unprocessed message
	 */
	public String getRawMessage();

	/**
	 * sets a new raw message
	 * @param rawMessage the new raw message
	 */
	public void setRawMessage(String rawMessage);

	/**
	 * get the time the message was sent
	 */
	public long getTime();

	public CommandSender getSender();
}
