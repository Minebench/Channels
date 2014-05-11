package net.zaiyers.Channels.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.zaiyers.Channels.Channel;
import net.zaiyers.Channels.Channels;

public class ChannelOpenCommand extends ChannelCreateCommand {
	public ChannelOpenCommand(CommandSender sender, String[] args) {
		super(sender, args);
	}

	public void execute() {
		if (!(sender instanceof ProxiedPlayer)) {
			Channels.notify(sender, "channels.command.is-player-command");
			return;
		}
		
		// register channel
		super.execute();
		
		Channel channel = Channels.getInstance().getChannel(args[1]);		
		if (channel != null) {
			// make channel temporary
			channel.setTemporary(true);
			
			// add sender to moderators
			channel.addModerator(((ProxiedPlayer) sender).getUniqueId().toString());
		}
	}

	public boolean validateInput() {
		return args.length > 2;
	}
}
