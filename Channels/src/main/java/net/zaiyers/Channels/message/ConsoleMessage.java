package net.zaiyers.Channels.message;

import net.md_5.bungee.api.chat.TextComponent;
import net.zaiyers.Channels.Channel;

public class ConsoleMessage extends AbstractMessage {
	/**
	 * channel this message was sent to
	 */
	private Channel channel;
	
	/**
	 * constructor
	 * 
	 * @param chatter
	 * @param channel
	 * @param rawMessage
	 */
	public ConsoleMessage(Channel channel, String rawMessage) {
		this.channel = channel;
		this.rawMessage = rawMessage;
	}
	
	/**
	 * generate message and format it
	 */
	public void processMessage() {
		processedMessage = new TextComponent( 
				channel.getConsoleFormat()
									.replaceAll("%channelColor%",	channel.getColor().toString())
									.replaceAll("%channelTag%",		channel.getTag())
									.replaceAll("%channelName%",	channel.getName())
		);
	}
	
	public void send() {
		processMessage();
		
		channel.send(this);	
	}
}
