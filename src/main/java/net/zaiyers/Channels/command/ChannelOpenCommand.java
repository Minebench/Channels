package net.zaiyers.Channels.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.zaiyers.Channels.Channel;
import net.zaiyers.Channels.Channels;

public class ChannelOpenCommand extends ChannelCreateCommand {
	public ChannelOpenCommand(CommandSource sender, String[] args) {
		super(sender, args);
	}

	public void execute() {
		if (!(sender instanceof Player)) {
			Channels.notify(sender, "channels.command.is-player-command");
			return;
		}
		
		// register channel
		super.execute();
		
		Channel channel = Channels.getInstance().getChannel(args[1]);		
		if (channel != null) {
			// make channel temporary
			channel.setTemporary(true);
			Channels.getInstance().unregisterTag(channel.getTag());
			
			// add sender to moderators
			channel.addModerator(((Player) sender).getUniqueId().toString());
		}
	}

	public boolean validateInput() {
		return args.length > 2;
	}
}
