package net.zaiyers.Channels.message;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;

public interface Message {
	/**
	 * sends the message to its recipients
	 */
	default void send() {
		send(false);
	}

	/**
	 * sends the message to its recipients
	 * @param hidden Whether this message should only be shown to the sender
	 */
	public void send(boolean hidden);

	/**
	 * get the final message
	 */
	public BaseComponent[] getProcessedMessage();
	
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
