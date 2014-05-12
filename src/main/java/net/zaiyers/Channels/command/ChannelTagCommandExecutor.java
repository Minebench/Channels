package net.zaiyers.Channels.command;

import com.google.common.collect.ImmutableMap;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.md_5.bungee.command.ConsoleCommandSender;
import net.zaiyers.Channels.Channel;
import net.zaiyers.Channels.Channels;
import net.zaiyers.Channels.ChannelsChatEvent;
import net.zaiyers.Channels.Chatter;
import net.zaiyers.Channels.message.ChannelMessage;
import net.zaiyers.Channels.message.ConsoleMessage;
import net.zaiyers.Channels.message.Message;

public class ChannelTagCommandExecutor extends Command implements TabExecutor {
	/**
	 * uuid of the channel
	 */
	private String channelUUID;

	public ChannelTagCommandExecutor(String name) {
		super(name);
		
		channelUUID = Channels.getInstance().getChannel(name).getUUID();
	}

	@Override
	public void execute(CommandSender sender, String[] args) {	
		Message msg;
		Channel chan = Channels.getInstance().getChannel(channelUUID);
		
		if (sender instanceof ConsoleCommandSender) {
			msg = new ConsoleMessage(chan, argsToMessage(args));
		} else {
			Chatter chatter = Channels.getInstance().getChatter(((ProxiedPlayer) sender).getUniqueId().toString());
			
			if (!chatter.getSubscriptions().contains(chan.getUUID())) {
				Channels.notify(sender, "channels.chatter.channel-not-subscribed", ImmutableMap.of("channel", chan.getName(), "channelColor", chan.getColor().toString()));
				return;
			}
			
			if (!chatter.hasPermission(chan, "speak") && chan.isTemporary()) {
				Channels.notify(sender, "channels.permission.channel-no-speak", ImmutableMap.of("channel", chan.getName(), "channelColor", chan.getColor().toString()));
			}
			
			if (args.length == 0) {
				chatter.setDefaultChannelUUID(chan.getUUID());
				chatter.setPrivateRecipient(null);
				Channels.notify(sender, "channels.chatter.default-channel-set", ImmutableMap.of("channel", chan.getName(), "channelColor", chan.getColor().toString()));
				return;
			} else {
				msg = new ChannelMessage(chatter, chan, argsToMessage(args));
			}
		}
		
		ChannelsChatEvent chatEvent = new ChannelsChatEvent(msg);
		if (!Channels.getInstance().getProxy().getPluginManager().callEvent( chatEvent ).isCancelled()) {
			msg.send();
		}
	}
	
	private String argsToMessage(String[] args) {
		String message = args[0];
		for (int i=1; i<args.length; i++) {
			message+=" "+args[i];
		}
		return message;
	}

	public Iterable<String> onTabComplete(CommandSender arg0, String[] args) {
		return ChannelsCommandExecutor.matchingPlayers((args.length > 0) ? args[args.length-1] : "");
	}
}
