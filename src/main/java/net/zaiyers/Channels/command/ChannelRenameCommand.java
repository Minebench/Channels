package net.zaiyers.Channels.command;

import com.google.common.collect.ImmutableMap;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.command.ConsoleCommandSender;
import net.zaiyers.Channels.Channel;
import net.zaiyers.Channels.Channels;

public class ChannelRenameCommand extends AbstractCommand implements ChannelsCommand {

	public ChannelRenameCommand(CommandSender sender, String[] args) {
		super(sender, args);
	}

	public void execute() {
		String newName, newTag;
		Channel chan = Channels.getInstance().getChannel(args[1]);
		
		// get chan
		if (chan == null) {
			Channels.notify(sender, "channels.command.channel-not-found", ImmutableMap.of("channel", args[1]));
			return;
		}
		
		// check perms
		if (!(sender instanceof ConsoleCommandSender) && !chan.isMod(((ProxiedPlayer) sender).getUUID()) && !sender.hasPermission("channels.rename.foreign")) {
			Channels.notify(sender, "channels.command.channel-no-permission");
			return;
		}
		
		// check input
		if (args[2].matches("^[a-zA-Z0-9_]+$")) {
			newName = args[2];
		} else {
			Channels.notify(sender, "channels.usage.channelname-disallowed-chars");
			return;
		}
		
		if (args[3].matches("^[a-zA-Z0-9_]+$")) {
			newTag = args[3];
		} else {
			Channels.notify(sender, "channels.usage.channeltag-disallowed-chars");
			return;
		}
	
		// check new name availability
		if (Channels.getInstance().getChannel(newTag) != null && !chan.getTag().equalsIgnoreCase(newTag)) {
			Channels.notify(sender, "channels.command.channel-tag-in-use");
			return;
		} else if (Channels.getInstance().getChannel(newName) != null && !chan.getName().equalsIgnoreCase(newName)) {
			Channels.notify(sender, "channels.command.channel-name-in-use");
			return;
		}
		
		// rename
		chan.setName(newName);
		if (!chan.getTag().equals(newTag)) {
			Channels.getInstance().unregisterTag(chan.getTag());
			chan.setTag(newTag);
			
			Channels.getInstance().registerTag(chan.getTag());
		}
		
		Channels.notify(sender, "channels.command.channel-modified");
	}

	public boolean validateInput() {
		return args.length > 3;
	}

}
