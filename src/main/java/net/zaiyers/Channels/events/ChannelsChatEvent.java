package net.zaiyers.Channels.events;

import com.velocitypowered.api.event.ResultedEvent;
import net.zaiyers.Channels.message.Message;

public class ChannelsChatEvent implements ResultedEvent<ResultedEvent.GenericResult> {
	private final Message message;
	private GenericResult result;
	private boolean hidden = false;

	public ChannelsChatEvent(Message msg) {
		message = msg;
	}

	public boolean isCancelled() {
		return !getResult().isAllowed();
	}

	public void setCancelled(boolean cancel) {
		setResult(cancel ? GenericResult.denied() : GenericResult.allowed());
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

	@Override
	public GenericResult getResult() {
		return result;
	}

	@Override
	public void setResult(GenericResult result) {
		this.result = result;
	}
}