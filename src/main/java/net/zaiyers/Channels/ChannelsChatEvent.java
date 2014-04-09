package net.zaiyers.Channels;

import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;
import net.zaiyers.Channels.message.Message;

public class ChannelsChatEvent extends Event implements Cancellable {
	private Message message;
	private boolean cancelled = false;
	
	public ChannelsChatEvent(Message msg) {
		message = msg;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancel) {
		cancelled = cancel;
	}
	
	/**
	 * get Message
	 * @return
	 */
	public Message getMessage() {
		return message;
	}
}
