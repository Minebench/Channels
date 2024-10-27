package net.zaiyers.Channels.message;

import net.kyori.adventure.text.Component;

import java.util.regex.Matcher;

abstract public class AbstractMessage implements Message {
	/**
	 * unprocessed message
	 */
	protected String rawMessage;
	
	/**
	 * pretty message
	 */
	protected Component processedMessage;
	
	/**
	 * time this message was send
	 */
	final private long time = System.currentTimeMillis();
	
	/**
	 * the final message
	 */
	public Component getProcessedMessage() {
		return processedMessage;
	}
	
	public String getRawMessage() {
		return rawMessage;
	}

	/**
	 * sets a new raw message
	 * @param rawMessage the new raw message
	 */
	public void setRawMessage(String rawMessage) {
		this.rawMessage = Matcher.quoteReplacement(rawMessage);
	}
	
	public long getTime() {
		return time;
	}
}
