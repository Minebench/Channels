package net.zaiyers.Channels;

import net.zaiyers.Channels.message.Message;

/**
 * @deprecated Use {@link net.zaiyers.Channels.events.ChannelsChatEvent}
 */
@Deprecated
public class ChannelsChatEvent extends net.zaiyers.Channels.events.ChannelsChatEvent {
	
	public ChannelsChatEvent(Message msg) {
		super(msg);
	}
}
