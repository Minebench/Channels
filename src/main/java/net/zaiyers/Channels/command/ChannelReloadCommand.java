package net.zaiyers.Channels.command;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import com.velocitypowered.api.command.CommandSource;
import net.zaiyers.Channels.Channels;

public class ChannelReloadCommand extends AbstractCommand {

	public ChannelReloadCommand(CommandSource sender, String[] args) {
		super(sender, args);
	}

	public void execute() {
		if (Channels.getInstance().reloadConfig()) {
			sender.sendMessage(Component.text("Language config reloaded! Restart to fully reload the plugin...").color(NamedTextColor.GREEN));
		} else {
			sender.sendMessage(Component.text("Error while reloading the config!").color(NamedTextColor.RED));
		}
	}

	public boolean validateInput() {
		return true;
	}
}
