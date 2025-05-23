package net.zaiyers.Channels.events;

import net.zaiyers.Channels.Chatter;

public class ChatterDndEvent {
	private final Chatter chatter;
	private final boolean isDnd;
	private String message;
	
	public ChatterDndEvent(Chatter chatter, boolean isDnd, String message) {
		this.chatter = chatter;
		this.isDnd = isDnd;
		this.message = message;
	}
	
	public Chatter getChatter() {
		return chatter;
	}
	
	public boolean isDnd() {
		return isDnd;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
}
