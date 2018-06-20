package net.zaiyers.Channels.message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;

import de.themoep.minedown.MineDown;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.zaiyers.Channels.Channel;
import net.zaiyers.Channels.Channels;

public class ConsoleMessage extends AbstractMessage {
	/**
	 * channel this message was sent to
	 */
	private Channel channel;
	
	/**
	 * constructor
	 *
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
		Date date = new Date(getTime());
		SimpleDateFormat dateFormat = Channels.getConfig().getDateFormat();
		SimpleDateFormat timeFormat = Channels.getConfig().getTimeFormat();
		
		processedMessage = MineDown.parse(channel.getConsoleFormat(),
				"channelColor", channel.getColor().toString(),
				"channelTag", channel.getTag(),
				"channelName", channel.getName(),
				"date", dateFormat.format(date),
				"time", timeFormat.format(date),
				"msg", rawMessage
		);
	}
	
	public void send() {
		processMessage();
		channel.send(this);
	}

	public CommandSender getSender() {
		return ProxyServer.getInstance().getConsole();
	}
	
	public Channel getChannel() {
		return channel;
	}
}
