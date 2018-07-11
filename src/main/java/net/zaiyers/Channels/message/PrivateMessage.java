package net.zaiyers.Channels.message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;

import com.google.common.collect.ImmutableMap;

import de.themoep.minedown.MineDown;
import de.themoep.minedown.MineDownParser;
import net.md_5.bungee.api.CommandSender;
import net.zaiyers.Channels.Channels;
import net.zaiyers.Channels.Chatter;

public class PrivateMessage extends AbstractMessage {
	private Chatter sender;
	private Chatter receiver;
	
	public static enum SenderRole {
		SENDER, RECEIVER
	}
	
	public PrivateMessage(Chatter sender, Chatter receiver, String message) {
		this.sender = sender;
		this.receiver = receiver;
		rawMessage = Matcher.quoteReplacement(message);
	}
	
	public void send() {
		if (receiver != null && receiver.isAFK()) {
			if (receiver.getAFKMessage() != null) {
				Channels.notify(sender.getPlayer(), "channels.chatter.is-afk-with-msg", ImmutableMap.of("chatter", receiver.getName(), "msg", receiver.getAFKMessage()));
			} else {
				Channels.notify(sender.getPlayer(), "channels.chatter.is-afk", ImmutableMap.of("chatter", receiver.getName()));
			}
		} else if (receiver != null && receiver.isDND()) {
			if (receiver.getDNDMessage() != null) {
				Channels.notify(sender.getPlayer(), "channels.chatter.is-dnd-with-msg", ImmutableMap.of("chatter", receiver.getName(), "msg", receiver.getDNDMessage()));
			} else {
				Channels.notify(sender.getPlayer(), "channels.chatter.is-dnd", ImmutableMap.of("chatter", receiver.getName()));
			}
			return;
		}
		
		processMessage(SenderRole.SENDER);
		sender.sendMessage(this);
		
		if (!receiver.getIgnores().contains(sender.getPlayer().getUniqueId().toString())) {
			processMessage(SenderRole.RECEIVER);
			receiver.sendMessage(this);
			receiver.setLastPrivateSender(sender);
		}
	}
	
	/**
	 * generate private message output
	 * @return
	 */
	private void processMessage(SenderRole role) {
		Date date = new Date(getTime());
		SimpleDateFormat dateFormat = Channels.getConfig().getDateFormat();
		SimpleDateFormat timeFormat = Channels.getConfig().getTimeFormat();
		
		String pmFormat = Channels.getConfig().getPrivateMessageFormat(role);
		
		MineDown messageMd = new MineDown(rawMessage)
				.urlHoverText(Channels.getInstance().getLanguage().getTranslation("chat.hover.open-url"));
		if (!getChatter().hasPermission("channels.color")) {
			messageMd.disable(MineDownParser.Option.LEGACY_COLORS);
		}
		if (!getChatter().hasPermission("channels.minedown.advanced")) {
			messageMd.disable(MineDownParser.Option.ADVANCED_FORMATTING);
		}
		if (!getChatter().hasPermission("channels.minedown.simple")) {
			messageMd.disable(MineDownParser.Option.SIMPLE_FORMATTING);
		}
		
		processedMessage = new MineDown(pmFormat).replace(
				"sender-prefix", sender.getPrefix(),
				"sender", sender.getName(),
				"sender-suffix", sender.getSuffix(),
				"receiver-prefix", receiver.getPrefix(),
				"receiver", receiver.getName(),
				"receiver-suffix", receiver.getSuffix(),
				"date", dateFormat.format(date),
				"time", timeFormat.format(date)
		).replace("msg", messageMd.toComponent()).toComponent();
	}
	
	public CommandSender getSender() {
		return sender.getPlayer();
	}
	
	public Chatter getChatter() {
		return sender;
	}
	
	public Chatter getReceiver() {
		return receiver;
	}
}
