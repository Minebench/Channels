package net.zaiyers.Channels.message;

import net.md_5.bungee.api.chat.TextComponent;

abstract public class AbstractMessage implements Message {
	/**
	 * unprocessed message
	 */
	protected String rawMessage;
	
	/**
	 * pretty message
	 */
	protected TextComponent processedMessage;
	
	/**
	 * time this message was send
	 */
	final private long time = System.currentTimeMillis();
	
	/**
	 * the final message
	 */
	public TextComponent getProcessedMessage() {
		return processedMessage;
	}
	
	public String getRawMessage() {
		return rawMessage;
	}
	
	public long getTime() {
		return time;
	}
}
