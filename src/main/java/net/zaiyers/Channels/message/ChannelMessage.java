package net.zaiyers.Channels.message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
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
        BaseComponent[] baseComponents = TextComponent.fromLegacyText(
                channel.getFormat()
                        .replaceAll("%prefix%", chatter.getPrefix())
                        .replaceAll("%sender%", chatter.getName())
                        .replaceAll("%suffix%", chatter.getSuffix())
                        .replaceAll("%msg%", chatter.hasPermission(channel, "color") ?
                                Channels.addSpecialChars(rawMessage) : rawMessage)
                        .replaceAll("%channelColor%", channel.getColor().toString())
                        .replaceAll("%channelTag%", channel.getTag())
                        .replaceAll("%channelName%", channel.getName())
        );

        if(baseComponents.length > 0) {
            Date date = new Date(getTime());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            HoverEvent hoverTime = new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(
                    Channels.getConfig().getTimeHoverFormat()
                            .replaceAll("%date%", dateFormat.format(date))
                            .replaceAll("%time%", timeFormat.format(date))
            ));
            baseComponents[0].setHoverEvent(hoverTime);
        }
        processedMessage = new TextComponent(baseComponents);
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

	/**
	 * sets a new raw message
	 * @param rawMessage the new raw message
	 */
	public void setRawMessage(String rawMessage) {
		this.rawMessage = Matcher.quoteReplacement(rawMessage);
		processMessage();
	}
}
