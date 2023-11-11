package net.zaiyers.Channels.events;

import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;
import net.zaiyers.Channels.message.Message;

public class ChannelsChatEvent extends Event implements Cancellable {
	private Message message;
	private boolean cancelled = false;
	private boolean hidden = false;
	
	public ChannelsChatEvent(Message msg) {
		message = msg;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancel) {
		cancelled = cancel;
	}
	
	public boolean isHidden() {
		return hidden;
	}

	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}
	
	/**
	 * get Message
	 * @return
	 */
	public Message getMessage() {
		return message;
	}
}