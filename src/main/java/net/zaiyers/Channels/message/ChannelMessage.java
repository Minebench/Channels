package net.zaiyers.Channels.message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;

import de.themoep.minedown.MineDown;
import de.themoep.minedown.MineDownParser;
import de.themoep.minedown.Replacer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.zaiyers.Channels.Channel;
import net.zaiyers.Channels.Channels;
import net.zaiyers.Channels.Chatter;

public class ChannelMessage extends AbstractMessage {
	/**
	 * channel this message was sent to
	 */
	private Channel channel;
	
	/**
	 * chatter who sent this message
	 */
	private Chatter chatter;
		
	/**
	 * constructor
	 * 
	 * @param chatter
	 * @param channel
	 * @param rawMessage
	 */
	public ChannelMessage(Chatter chatter, Channel channel, String rawMessage) {
		this.chatter = chatter;
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
		
		MineDown messageMd = new MineDown(rawMessage)
				.urlHoverText(ChatColor.translateAlternateColorCodes('&', Channels.getInstance().getLanguage().getTranslation("chat.hover.open-url")));
		if (!chatter.hasPermission("channels.color")) {
			messageMd.disable(MineDownParser.Option.LEGACY_COLORS);
		}
		if (!chatter.hasPermission("channels.minedown.advanced")) {
			messageMd.disable(MineDownParser.Option.ADVANCED_FORMATTING);
		}
		if (!chatter.hasPermission("channels.minedown.simple")) {
			messageMd.disable(MineDownParser.Option.SIMPLE_FORMATTING);
		}
		
		processedMessage = new MineDown(new Replacer()
				.replace("channelColor", channel.getColor().toString())
				.replaceIn(channel.getFormat())
		).replace(
				"sender", chatter.getName(),
				"channelTag", channel.getTag(),
				"channelName", channel.getName(),
				"date", dateFormat.format(date),
				"time", timeFormat.format(date)
		)
				.replace("prefix", MineDown.parse(chatter.getPrefix()))
				.replace("suffix", MineDown.parse(chatter.getSuffix()))
				.replace("msg", messageMd.toComponent())
				.toComponent();
	}
	
	/**
	 * send the message to its channel
	 */
	public void send() {
		processMessage();
		
		channel.send(this);		
	}
	
	/**
	 * get author
	 * @return
	 */
	public Chatter getChatter() {
		return chatter;
	}

	public CommandSender getSender() {
		return chatter.getPlayer();
	}
	
	public Channel getChannel() {
		return channel;
	}

}
