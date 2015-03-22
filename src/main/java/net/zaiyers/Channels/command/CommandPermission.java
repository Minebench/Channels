package net.zaiyers.Channels.command;

public enum CommandPermission {
		ChannelAddModCommand("channels.addmod"),
		ChannelAddServerCommand("channels.addserver"),
		ChannelAutojoinCommand("channels.autojoin"),
		ChannelBanCommand("channels.ban"),
		ChannelColorCommand("channels.color"),
		ChannelCreateCommand("channels.create"),
		ChannelGlobalCommand("channels.global"),
        ChannelBackendCommand("channels.backend"),
		ChannelHelpCommand("channels.help"),
		ChannelInfoCommand("channels.info"),
		ChannelKickCommand("channels.kick"),
		ChannelListCommand("channels.list"),
		ChannelMuteCommand("channels.mute"),
		ChannelOpenCommand("channels.open"),
		ChannelPasswordCommand("channels.password"),
		ChannelPrefixCommand("channels.prefix"),
		ChannelRemoveCommand("channels.remove"),
		ChannelRemoveModCommand("channels.removemod"),
		ChannelRemoveServerCommand("channels.removeserver"),
		ChannelRenameCommand("channels.rename"),
		ChannelSpeakCommand("channels.speak"),
		ChannelSubscribeCommand("channels.subscribe"),
		ChannelSuffixCommand("channels.suffix"),
		ChannelUnbanCommand("channels.unban"),
		ChannelUnmuteCommand("channels.unmute"),
		ChannelUnsubscribeCommand("channels.unsubscribe"),
		DNDCommand("channels.dnd"),
		AFKCommand("channels.afk"),
		IgnoreCommand("channels.ignore"),
		PMCommand("channels.pm"),
		ReplyCommand("channels.pm"),
		ServerDefaultChannelCommand("channels.serverdefaultchannel"),
		;
		
		
		private String permission;
		
		private CommandPermission(final String permission) {
			this.permission = permission;
		}
		
		public String toString() {
			return permission;
		}
}
