package net.zaiyers.Channels.command;

import com.google.common.collect.ImmutableMap;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import net.zaiyers.Channels.Channels;
import net.zaiyers.Channels.events.ChannelsChatEvent;
import net.zaiyers.Channels.Chatter;
import net.zaiyers.Channels.message.Message;
import net.zaiyers.Channels.message.PrivateMessage;

public class ReplyCommand extends AbstractCommand {
	public ReplyCommand(CommandSource sender, String[] args) {
		super(sender, args);
	}

	public void execute() {
		if (!(sender instanceof Player)) {
			Channels.notify(sender, "channels.command.is-player-command");
			return;
		}
		
		Chatter chatter = Channels.getInstance().getChatter(((Player) sender).getUniqueId());

		if (chatter.getLastSender() != null) {
			Chatter recipient = Channels.getInstance().getChatter(chatter.getLastSender());
			if (recipient != null) {
				if (args.length == 0) {
					chatter.setPrivateRecipient(chatter.getLastSender());
					Channels.notify(sender, "channels.chatter.recipient-set", ImmutableMap.of("recipient", recipient.getName()));
				} else {
					String text = args[0];
					for (int i=1; i<args.length; i++) {
						text+=" "+args[i];
					}
					
					Message msg = new PrivateMessage(chatter, recipient, text);
					ChannelsChatEvent chatEvent = new ChannelsChatEvent(msg);
					if (!Channels.getInstance().getProxy().getEventManager().fire( chatEvent ).isCancelled()) {
						msg.send(chatEvent.isHidden());
					}
				}
			} else {
				Channels.notify(sender, "channels.chatter.recipient-offline");
			}
		} else {
			Channels.notify(sender, "channels.chatter.nobody-wrote");
		}
	}

	public boolean validateInput() {
		return true;
	}
}
