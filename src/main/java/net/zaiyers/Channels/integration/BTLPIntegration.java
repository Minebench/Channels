package net.zaiyers.Channels.integration;

import codecrafter47.bungeetablistplus.api.bungee.BungeeTabListPlusAPI;
import codecrafter47.bungeetablistplus.api.bungee.Variable;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.zaiyers.Channels.Channel;
import net.zaiyers.Channels.Channels;
import net.zaiyers.Channels.Chatter;

public class BTLPIntegration {

	public BTLPIntegration() {
		BungeeTabListPlusAPI.registerVariable(Channels.getInstance(), new Variable("channels_channel_name") {
			@Override
			public String getReplacement(ProxiedPlayer proxiedPlayer) {
				Chatter chatter = Channels.getInstance().getChatter(proxiedPlayer);
				if (chatter != null) {
					Channel channel = Channels.getInstance().getChannel(chatter.getChannel());
					if (channel != null) {
						return channel.getName();
					}
				}
				return "";
			}
		});
		BungeeTabListPlusAPI.registerVariable(Channels.getInstance(), new Variable("channels_channel_tag") {
			@Override
			public String getReplacement(ProxiedPlayer proxiedPlayer) {
				Chatter chatter = Channels.getInstance().getChatter(proxiedPlayer);
				if (chatter != null) {
					Channel channel = Channels.getInstance().getChannel(chatter.getChannel());
					if (channel != null) {
						return channel.getTag();
					}
				}
				return "";
			}
		});
		BungeeTabListPlusAPI.registerVariable(Channels.getInstance(), new Variable("channels_status") {
			@Override
			public String getReplacement(ProxiedPlayer proxiedPlayer) {
				Chatter chatter = Channels.getInstance().getChatter(proxiedPlayer);
				if (chatter != null) {
					if (chatter.isDND()) {
						return "dnd";
					}
					if (chatter.isAFK()) {
						return "afk";
					}
				}
				return "none";
			}
		});
		BungeeTabListPlusAPI.registerVariable(Channels.getInstance(), new Variable("channels_status_message") {
			@Override
			public String getReplacement(ProxiedPlayer proxiedPlayer) {
				Chatter chatter = Channels.getInstance().getChatter(proxiedPlayer);
				if (chatter != null) {
					if (chatter.isDND()) {
						return chatter.getDNDMessage();
					}
					if (chatter.isAFK()) {
						return chatter.getAFKMessage();
					}
				}
				return "";
			}
		});
	}
}
