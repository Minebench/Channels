package net.zaiyers.Channels.message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;

import com.google.common.collect.ImmutableMap;

import de.themoep.minedown.MineDown;
import de.themoep.minedown.MineDownParser;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
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
			if (!sender.hasPermission("channels.bypass.dnd")) {
				return;
			}
		}

		boolean bypassIgnore = sender.hasPermission("channels.bypass.ignore");
		if (receiver != null && receiver.getPlayer().getChatMode() != ProxiedPlayer.ChatMode.SHOWN) {
			if (receiver.getPlayer().getChatMode() == ProxiedPlayer.ChatMode.HIDDEN || !bypassIgnore) {
				Channels.notify(sender.getPlayer(), "Channels.chatter.hides-chat", ImmutableMap.of("chatter", receiver.getName()));
			}
		}
		
		processMessage(SenderRole.SENDER);
		sender.sendMessage(sender, this);

		if (!receiver.getIgnores().contains(sender.getPlayer().getUniqueId().toString()) || bypassIgnore) {
			processMessage(SenderRole.RECEIVER);
			receiver.sendMessage(bypassIgnore ? null : sender, this);
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
				.urlHoverText(ChatColor.translateAlternateColorCodes('&', Channels.getInstance().getLanguage().getTranslation("chat.hover.open-url")));
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
				"sender", sender.getName(),
				"receiver", receiver.getName(),
				"date", dateFormat.format(date),
				"time", timeFormat.format(date)
		).replace("msg", messageMd.toComponent())
				.replace("sender-prefix", MineDown.parse(sender.getPrefix()))
				.replace("sender-suffix", MineDown.parse(sender.getSuffix()))
				.replace("receiver-prefix", MineDown.parse(receiver.getPrefix()))
				.replace("receiver-suffix", MineDown.parse(receiver.getSuffix()))
				.toComponent();
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
