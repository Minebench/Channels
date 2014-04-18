package net.zaiyers.Channels.message;

import java.util.regex.Matcher;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.command.ConsoleCommandSender;
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
		this.rawMessage = Matcher.quoteReplacement(rawMessage);
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
									.replaceAll("%msg%", rawMessage)
		);
	}
	
	public void send() {
		processMessage();
		
		channel.send(this);	
	}

	public CommandSender getSender() {
		return ConsoleCommandSender.getInstance();
	}
}
