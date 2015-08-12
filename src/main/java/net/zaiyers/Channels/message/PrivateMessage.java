package net.zaiyers.Channels.message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;

import com.google.common.collect.ImmutableMap;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.HoverEvent;
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        HoverEvent hoverTime = new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(
                Channels.getConfig().getTimeHoverFormat()
                        .replaceAll("%date%", dateFormat.format(date))
                        .replaceAll("%time%", timeFormat.format(date))
        ));
        String message = (getChatter().hasPermission("channels.color")) ? Channels.addSpecialChars(rawMessage) : rawMessage;
        String pmFormat = Channels.getConfig().getPrivateMessageFormat(role);
        int offset = (pmFormat.contains("%receiver%")) ? pmFormat.indexOf("%receiver%") + "%receiver%".length() : pmFormat.indexOf("%sender%") + "%sender%".length();
        if(offset > -1) {
            TextComponent timeComponent = new TextComponent(TextComponent.fromLegacyText(
                    pmFormat.substring(0, offset)
                            .replaceAll("%sender%", sender.getName())
                            .replaceAll("%receiver%", receiver.getName())
                            .replaceAll("%msg%", message)
            ));
            timeComponent.setHoverEvent(hoverTime);
            processedMessage = new TextComponent("");
            processedMessage.addExtra(timeComponent);

            TextComponent msgComponent = new TextComponent(TextComponent.fromLegacyText(
                    pmFormat.substring(offset)
                            .replaceAll("%sender%", sender.getName())
                            .replaceAll("%receiver%", receiver.getName())
                            .replaceAll("%msg%", message)
            ));
            processedMessage.addExtra(msgComponent);
        } else {
            processedMessage = new TextComponent(TextComponent.fromLegacyText(
                    pmFormat
                            .replaceAll("%sender%", sender.getName())
                            .replaceAll("%receiver%", receiver.getName())
                            .replaceAll("%msg%", message)
            ));
            processedMessage.setHoverEvent(hoverTime);
        }
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
