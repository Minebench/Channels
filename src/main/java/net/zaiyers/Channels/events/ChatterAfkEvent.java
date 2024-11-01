package net.zaiyers.Channels.events;

import net.zaiyers.Channels.Chatter;

public class ChatterAfkEvent {
	private final Chatter chatter;
	private final boolean isAfk;
	private String message;
	
	public ChatterAfkEvent(Chatter chatter, boolean isAfk, String message) {
		this.chatter = chatter;
		this.isAfk = isAfk;
		this.message = message;
	}
	
	public Chatter getChatter() {
		return chatter;
	}
	
	public boolean isAfk() {
		return isAfk;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
}
