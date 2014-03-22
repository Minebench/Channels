package net.zaiyers.Channels.command;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.zaiyers.Channels.Channels;

public class ChannelOpenCommand extends ChannelCreateCommand {
	public ChannelOpenCommand(CommandSender sender, String[] args) {
		super(sender, args);
	}

	public String getPermission() {
		return "channels.open";
	}

	public void execute() {
		if (!(sender instanceof ProxiedPlayer)) {
			Channels.notify(sender, "channels.command.is-player-command");
			return;
		}
		
		// register channel
		super.execute();
		
		if (Channels.getInstance().getChannel(args[1]) != null) {
			// make channel temporary
			Channels.getInstance().getChannel(args[1]).setTemporary(true);
			
			// add sender to moderators
			Channels.getInstance().getChannel(args[1]).addModerator(((ProxiedPlayer) sender).getUUID());
		}
	}

	public boolean validateInput() {
		return args.length > 2;
	}
}
