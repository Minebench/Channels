package net.zaiyers.Channels.command;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.zaiyers.Channels.Channels;

public class ChannelReloadCommand extends AbstractCommand {

	public ChannelReloadCommand(CommandSender sender, String[] args) {
		super(sender, args);
	}

	public void execute() {
		if (Channels.getInstance().reloadConfig()) {
			sender.sendMessage(ChatColor.GREEN + "Language config reloaded! Restart to fully reload the plugin...");
		} else {
			sender.sendMessage(ChatColor.RED + "Error while reloading the config!");
		}
	}

	public boolean validateInput() {
		return true;
	}
}
