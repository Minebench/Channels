package net.zaiyers.Channels.command;

import com.google.common.collect.ImmutableMap;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import com.velocitypowered.api.proxy.Player;
import net.zaiyers.Channels.Channel;
import net.zaiyers.Channels.Channels;
import net.zaiyers.Channels.events.ChannelsChatEvent;
import net.zaiyers.Channels.Chatter;
import net.zaiyers.Channels.message.ChannelMessage;
import net.zaiyers.Channels.message.ConsoleMessage;
import net.zaiyers.Channels.message.Message;

import java.util.List;

public class ChannelTagCommandExecutor extends AbstractCommandExecutor {
	/**
	 * uuid of the channel
	 */
	private final String channelUUID;

	public ChannelTagCommandExecutor(String name) {
		super(name);
		
		channelUUID = Channels.getInstance().getChannel(name).getUUID();
	}

	@Override
	public void execute(CommandSource sender, String[] args) {
		Message msg;
		Channel chan = Channels.getInstance().getChannel(channelUUID);
		
		if (!(sender instanceof Player)) {
			msg = new ConsoleMessage(chan, argsToMessage(args));
		} else {
			Chatter chatter = Channels.getInstance().getChatter(((Player) sender).getUniqueId());
			
			if (!chatter.getSubscriptions().contains(chan.getUUID())) {
				Channels.notify(sender, "channels.chatter.channel-not-subscribed", ImmutableMap.of("channel", chan.getName(), "channelColor", chan.getColor().toString()));
				return;
			}
			
			if (!chatter.hasPermission(chan, "speak")) {
				Channels.notify(sender, "channels.permission.channel-no-speak", ImmutableMap.of("channel", chan.getName(), "channelColor", chan.getColor().toString()));
				return;
			}
			
			if (args.length == 0) {
				chatter.setDefaultChannelUUID(chan.getUUID());
				chatter.setPrivateRecipient(null);
				Channels.notify(sender, "channels.chatter.default-channel-set", ImmutableMap.of("channel", chan.getName(), "channelColor", chan.getColor().toString()));
				return;
			} else {
				if(chan.isBackend()) {
					((Player) sender).spoofChatInput(argsToMessage(args));
					return;
				} else {
					msg = new ChannelMessage(chatter, chan, argsToMessage(args));
				}
			}
		}
		
		ChannelsChatEvent chatEvent = new ChannelsChatEvent(msg);
		if (!Channels.getInstance().getProxy().getEventManager().fire( chatEvent ).isCancelled()) {
			msg.send(chatEvent.isHidden());
		}
	}
	
	private String argsToMessage(String[] args) {
		StringBuilder message = new StringBuilder(args[0]);
		for (int i=1; i<args.length; i++) {
			message.append(" ").append(args[i]);
		}
		return message.toString();
	}

	public List<String> onTabComplete(CommandSource arg0, String[] args) {
		return ChannelsCommandExecutor.matchingPlayers((args.length > 0) ? args[args.length-1] : "");
	}

	public boolean hasPermission(final SimpleCommand.Invocation invocation) {
		if (!super.hasPermission(invocation)) {
			return false;
		}

		if (!(invocation.source() instanceof Player)) {
			return true;
		}

		Channel chan = Channels.getInstance().getChannel(channelUUID);
		Chatter chatter = Channels.getInstance().getChatter(((Player) invocation.source()).getUniqueId());
		return chatter.getSubscriptions().contains(chan.getUUID()) && chatter.hasPermission(chan, "speak");
	}
}
