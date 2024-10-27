package net.zaiyers.Channels.message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;

import de.themoep.minedown.adventure.MineDown;
import net.kyori.adventure.text.Component;
import com.velocitypowered.api.command.CommandSource;
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
						.urlHoverText(Channels.getInstance().getLanguage().getTranslation("chat.hover.open-url"))
						.toComponent())
				.toComponent();
	}

	/**
	 * the final message
	 */
	@Override
	public Component getProcessedMessage() {
		if (processedMessage == null)
			processMessage();
		return super.getProcessedMessage();
	}
	
	public void send(boolean hidden) {
		channel.send(this, hidden);
	}

	public CommandSource getSender() {
		return Channels.getInstance().getProxy().getConsoleCommandSource();
	}
	
	public Channel getChannel() {
		return channel;
	}
}
