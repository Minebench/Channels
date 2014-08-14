package net.zaiyers.Channels.command;

import com.google.common.collect.ImmutableMap;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.command.ConsoleCommandSender;
import net.zaiyers.Channels.Channel;
import net.zaiyers.Channels.Channels;
import net.zaiyers.Channels.Chatter;
import net.zaiyers.Channels.message.ChannelMessage;
import net.zaiyers.Channels.message.ConsoleMessage;
import net.zaiyers.Channels.message.Message;

public class ChannelSpeakCommand extends AbstractCommand {

	public ChannelSpeakCommand(CommandSender sender, String[] args) {
		super(sender, args);
	}

	public void execute() {
		if (!args[1].matches("^[a-zA-Z0-9]+$")) {
			Channels.notify(sender, "channels.command.channel-not-found", ImmutableMap.of("channel", args[1]));
			return;
		}
		
		Channel chan = Channels.getInstance().getChannel(args[1]);
		Message msg;
		
		if (chan == null) {
			Channels.notify(sender, "channels.command.channel-not-found", ImmutableMap.of("channel", args[1]));
			return;
		}
		
		if (sender instanceof ConsoleCommandSender) {
			msg = new ConsoleMessage(chan, argsToMessage(args));
		} else {
			Chatter chatter = Channels.getInstance().getChatter(((ProxiedPlayer) sender).getUniqueId().toString());
			
			if (!chatter.getSubscriptions().contains(chan.getUUID())) {
				Channels.notify(sender, "channels.chatter.channel-not-subscribed", ImmutableMap.of("channel", chan.getName(), "channelColor", chan.getColor().toString()));
				return;
			}
			
			if (!chatter.hasPermission(chan, "speak") && !chan.isTemporary()) {
				Channels.notify(sender, "channels.permission.channel-no-speak", ImmutableMap.of("channel", chan.getName(), "channelColor", chan.getColor().toString()));
				return;
			}
			
			if (args.length == 2) {
				chatter.setDefaultChannelUUID(chan.getUUID());
				chatter.setPrivateRecipient(null);
				Channels.notify(sender, "channels.chatter.default-channel-set", ImmutableMap.of("channel", chan.getName(), "channelColor", chan.getColor().toString()));
				return;
			} else {
				msg = new ChannelMessage(chatter, chan, argsToMessage(args));
			}
		}
		
		msg.send();
	}

	/**
	 * generate message from arguments
	 * @return
	 */
	private String argsToMessage(String[] args) {
		String message = args[2];
		for (int i=3; i<args.length; i++) {
			message+=" "+args[i];
		}
		return message;
	}
	
	public boolean validateInput() {
		return args.length > 1;
	}
}
