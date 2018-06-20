package net.zaiyers.Channels.message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;

import de.themoep.minedown.MineDown;
import de.themoep.minedown.Replacer;
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

		MineDown md = new MineDown(channel.getFormat()).replace(
				"prefix", chatter.getPrefix(),
				"sender", chatter.getName(),
				"suffix", chatter.getSuffix(),
				"channelColor", channel.getColor().toString(),
				"channelTag", channel.getTag(),
				"channelName", channel.getName(),
				"date", dateFormat.format(date),
				"time", timeFormat.format(date)
		);

		if (chatter.hasPermission("channels.minedown")) {
			md.replacer().replacements().put("msg", rawMessage);
			processedMessage = md.toComponent();
		} else  {
			processedMessage = md.toComponent();
			String message = chatter.hasPermission("channels.color") ? Channels.addSpecialChars(rawMessage) : rawMessage;
			processedMessage = Replacer.replace(processedMessage, "msg", message);
		}
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
