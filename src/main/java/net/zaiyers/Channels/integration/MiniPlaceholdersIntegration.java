package net.zaiyers.Channels.integration;

import com.velocitypowered.api.proxy.Player;
import io.github.miniplaceholders.api.Expansion;
import io.github.miniplaceholders.api.utils.TagsUtils;
import net.zaiyers.Channels.Channel;
import net.zaiyers.Channels.Channels;
import net.zaiyers.Channels.Chatter;

public class MiniPlaceholdersIntegration {

	public MiniPlaceholdersIntegration() {
		Expansion.builder("channels").audiencePlaceholder("channel_name", (audience, queue, ctx) -> {
			if (audience instanceof Player player) {
				Chatter chatter = Channels.getInstance().getChatter(player);
				if (chatter != null) {
					Channel channel = Channels.getInstance().getChannel(chatter.getChannel());
					if (channel != null) {
						return TagsUtils.staticTag(channel.getName());
					}
				}
			}
			return TagsUtils.EMPTY_TAG;
		}).audiencePlaceholder("channel_tag", (audience, queue, ctx) -> {
			if (audience instanceof Player player) {
				Chatter chatter = Channels.getInstance().getChatter(player);
				if (chatter != null) {
					Channel channel = Channels.getInstance().getChannel(chatter.getChannel());
					if (channel != null) {
						return TagsUtils.staticTag(channel.getTag());
					}
				}
			}
			return TagsUtils.EMPTY_TAG;
		}).audiencePlaceholder("status", (audience, queue, ctx) -> {
			if (audience instanceof Player player) {
				Chatter chatter = Channels.getInstance().getChatter(player);
				if (chatter != null) {
					if (chatter.isDND()) {
						return TagsUtils.staticTag("dnd");
					}
					if (chatter.isAFK()) {
						return TagsUtils.staticTag("afk");
					}
				}
			}
			return TagsUtils.staticTag("none");
		}).audiencePlaceholder("status_message", (audience, queue, ctx) -> {
			if (audience instanceof Player player) {
				Chatter chatter = Channels.getInstance().getChatter(player);
				if (chatter != null) {
					if (chatter.isDND()) {
						return TagsUtils.staticTag(chatter.getDNDMessage());
					}
					if (chatter.isAFK()) {
						return TagsUtils.staticTag(chatter.getAFKMessage());
					}
				}
			}
			return TagsUtils.EMPTY_TAG;
		}).build().register();
	}
}
