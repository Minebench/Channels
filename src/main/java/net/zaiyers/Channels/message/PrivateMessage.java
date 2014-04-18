package net.zaiyers.Channels.message;

import java.util.regex.Matcher;

import com.google.common.collect.ImmutableMap;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
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
		if (receiver.isAFK()) {
			if (receiver.getAFKMessage() != null) {
				Channels.notify(sender.getPlayer(), "channels.chatter.is-afk-with-msg", ImmutableMap.of("chatter", receiver.getName(), "msg", receiver.getAFKMessage()));
			} else {
				Channels.notify(sender.getPlayer(), "channels.chatter.is-afk", ImmutableMap.of("chatter", receiver.getName()));
			}
		} else if (receiver.isDND()) {
			if (receiver.getDNDMessage() != null) {
				Channels.notify(sender.getPlayer(), "channels.chatter.is-dnd-with-msg", ImmutableMap.of("chatter", receiver.getName(), "msg", receiver.getDNDMessage()));
			} else {
				Channels.notify(sender.getPlayer(), "channels.chatter.is-dnd", ImmutableMap.of("chatter", receiver.getName()));
			}
			return;
		}
				
		processMessage(SenderRole.SENDER);
		sender.sendMessage(this);
		
		if (!receiver.getIgnores().contains(sender.getPlayer().getUUID())) {
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
		processedMessage = new TextComponent(
			Channels.getConfig().getPrivateMessageFormat(role)	.replaceAll("%sender%", sender.getName())
																.replaceAll("%receiver%", receiver.getName())
																.replaceAll("%msg%", rawMessage)
		);
	}

	public CommandSender getSender() {
		return sender.getPlayer();
	}
}
