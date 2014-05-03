package net.zaiyers.Channels.command;

import java.io.IOException;
import java.util.UUID;

import net.md_5.bungee.api.CommandSender;
import net.zaiyers.Channels.Channel;
import net.zaiyers.Channels.Channels;

public class ChannelCreateCommand extends AbstractCommand implements ChannelsCommand{

	public ChannelCreateCommand(CommandSender sender, String[] args) {
		super(sender, args);
	}

	public void execute() {
		String name, tag, password = "";
		if (args[1].matches("^[a-zA-Z0-9_]+$")) {
			name = args[1];
		} else {
			Channels.notify(sender, "channels.usage.channelname-disallowed-chars");
			return;
		}
		
		if (args[2].matches("^[a-zA-Z0-9_]+$")) {
			tag = args[2];
		} else {
			Channels.notify(sender, "channels.usage.channeltag-disallowed-chars");
			return;
		}
		
		if (args.length > 3 && args[3].matches("^[a-zA-Z0-9_]+$")) {
			password = args[3];
		} else if (args.length > 3) {
			Channels.notify(sender, "channels.usage.channelpassword-disallowed-chars");
			return;
		}
	
		if (Channels.getInstance().getChannel(tag) != null) {
			Channels.notify(sender, "channels.command.channel-tag-in-use");
			return;
		} else if (Channels.getInstance().getChannel(name) != null) {
			Channels.notify(sender, "channels.command.channel-name-in-use");
			return;
		}
		
		try {
			// generate new uuid
			String chanUUID = UUID.randomUUID().toString();
			while (Channels.getConfig().getChannels().contains(chanUUID)) {
				chanUUID = UUID.randomUUID().toString();
			}
			
			// create new channel
			Channel chan = new Channel(chanUUID);
			chan.setName(name);
			chan.setTag(tag);
			chan.setPassword(password);
			chan.save();
									
			// register channel
			Channels.getInstance().addChannel(chan);
			Channels.getInstance().registerTag(tag);
			
			Channels.notify(sender, "channels.command.channel-created");
		} catch (IOException e) {
			Channels.getInstance().getLogger().severe("Unable to create new config");
			e.printStackTrace();
		}
	}

	public boolean validateInput() {
		return args.length > 2;
	}
}
