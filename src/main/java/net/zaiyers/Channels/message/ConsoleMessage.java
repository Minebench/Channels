package net.zaiyers.Channels.message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;

import de.themoep.minedown.MineDown;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
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
		
		processedMessage = new MineDown(channel.getFormat())
				.replace(
						"prefix", "",
						"sender", "(Console)",
						"suffix", "",
						"channelColor", channel.getColor().toString(),
						"channelTag", channel.getTag(),
						"channelName", channel.getName(),
						"date", dateFormat.format(date),
						"time", timeFormat.format(date))
				.replace("msg", new MineDown(rawMessage)
						.urlHoverText(ChatColor.translateAlternateColorCodes('&', Channels.getInstance().getLanguage().getTranslation("chat.hover.open-url")))
						.toComponent())
				.toComponent();
	}

	/**
	 * the final message
	 */
	@Override
	public BaseComponent[] getProcessedMessage() {
		if (processedMessage == null)
			processMessage();
		return super.getProcessedMessage();
	}
	
	public void send(boolean hidden) {
		channel.send(this, hidden);
	}

	public CommandSender getSender() {
		return ProxyServer.getInstance().getConsole();
	}
	
	public Channel getChannel() {
		return channel;
	}
}
