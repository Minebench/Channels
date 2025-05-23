package net.zaiyers.Channels.message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;

import com.google.common.collect.ImmutableMap;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.player.PlayerSettings;
import de.themoep.minedown.adventure.MineDown;
import de.themoep.minedown.adventure.MineDownParser;
import net.zaiyers.Channels.Channels;
import net.zaiyers.Channels.Chatter;

public class PrivateMessage extends AbstractMessage {
	private Chatter sender;
	private Chatter receiver;
	
	public enum SenderRole {
		SENDER, RECEIVER
	}
	
	public PrivateMessage(Chatter sender, Chatter receiver, String message) {
		this.sender = sender;
		this.receiver = receiver;
		rawMessage = Matcher.quoteReplacement(message);
	}
	
	public void send(boolean hidden) {
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
		if (receiver != null && receiver.getPlayer().getPlayerSettings().getChatMode() != PlayerSettings.ChatMode.SHOWN && !bypassIgnore) {
			Channels.notify(sender.getPlayer(), "Channels.chatter.hides-chat", ImmutableMap.of("chatter", receiver.getName()));
		}
		
		processMessage(SenderRole.SENDER);
		sender.sendMessage(sender, this);

		if (!hidden && (bypassIgnore || !receiver.getIgnores().contains(sender.getPlayer().getUniqueId().toString()))) {
			if (receiver.canSeeChat()) {
				processMessage(SenderRole.RECEIVER);
				receiver.sendMessage(bypassIgnore ? null : sender, this);
				receiver.setLastPrivateSender(sender);
			}
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
	
	public CommandSource getSender() {
		return sender.getPlayer();
	}
	
	public Chatter getChatter() {
		return sender;
	}
	
	public Chatter getReceiver() {
		return receiver;
	}
}
