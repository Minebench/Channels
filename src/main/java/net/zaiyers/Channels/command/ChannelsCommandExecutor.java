package net.zaiyers.Channels.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.google.common.collect.ImmutableMap;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import net.zaiyers.Channels.Channel;
import net.zaiyers.Channels.Channels;

public class ChannelsCommandExecutor extends Command implements TabExecutor {
	String command;
	
	public ChannelsCommandExecutor(String name, String... aliases) {
		super(name, "", aliases);
		
		command = name;
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		ChannelsCommand cmd;
		
		if (command.equals("channel")) {
			if (args.length > 0) {
				String cmdName = args[0].toLowerCase();
				if (cmdName.equals("ignore")) {
					cmd = new IgnoreCommand(sender, args);
				} else if (cmdName.matches("^subscribe|join$")) {
					cmd = new ChannelSubscribeCommand(sender, args);
				} else if (cmdName.matches("^unsubscribe|quit|leave$")) {
					cmd = new ChannelUnsubscribeCommand(sender, args);
				} else if (cmdName.matches("^speak|say$")) {
					cmd = new ChannelSpeakCommand(sender, args);
                } else if (cmdName.equals("backend")) {
                    cmd = new ChannelBackendCommand(sender, args);
				} else if (cmdName.equals("global")) {
					cmd = new ChannelGlobalCommand(sender, args);
				} else if (cmdName.equals("addserver")) {
					cmd = new ChannelAddServerCommand(sender, args);
				} else if (cmdName.equals("removeserver")) {
					cmd = new ChannelRemoveServerCommand(sender, args);
				} else if (cmdName.equals("create")) {
					cmd = new ChannelCreateCommand(sender, args);
				} else if (cmdName.equals("open")) {
					cmd = new ChannelOpenCommand(sender, args);
				} else if (cmdName.matches("^remove|close$")) {
					cmd = new ChannelRemoveCommand(sender, args);
				} else if (cmdName.equals("addmod")) {
					cmd = new ChannelAddModCommand(sender, args);
				} else if (cmdName.equals("removemod")) {
					cmd = new ChannelRemoveModCommand(sender, args);
				} else if (cmdName.equals("autofocus")) {
					cmd = new ChannelAutofocusCommand(sender, args);
				} else if (cmdName.equals("autojoin")) {
					cmd = new ChannelAutojoinCommand(sender, args);
				} else if (cmdName.equals("ban")) {
					cmd = new ChannelBanCommand(sender, args);
				} else if (cmdName.equals("kick")) {
					cmd = new ChannelKickCommand(sender, args);
				} else if (cmdName.equals("unban")) {
					cmd = new ChannelUnbanCommand(sender, args);
				} else if (cmdName.equals("color")) {
					cmd = new ChannelColorCommand(sender, args);
				} else if (cmdName.equals("format")) {
					cmd = new ChannelFormatCommand(sender, args);
				} else if (cmdName.equals("mute")) {
					cmd = new ChannelMuteCommand(sender, args);
				} else if (cmdName.equals("unmute")) {
					cmd = new ChannelUnmuteCommand(sender, args);
				} else if (cmdName.equals("prefix")) {
					cmd = new ChannelPrefixCommand(sender, args);
				} else if (cmdName.equals("suffix")) {
					cmd = new ChannelSuffixCommand(sender, args);
				} else if (cmdName.equals("serverdefaultchannel")) {
					cmd = new ServerDefaultChannelCommand(sender, args);
				} else if (cmdName.matches("^list|who$")) {
					cmd = new ChannelListCommand(sender, args);
				} else if (cmdName.equals("info")) {
					cmd = new ChannelInfoCommand(sender, args);
				} else if (cmdName.equals("rename")) {
					cmd = new ChannelRenameCommand(sender, args);
				} else if (cmdName.equals("password")) {
					cmd = new ChannelPasswordCommand(sender, args);
				} else if (cmdName.equals("help")) {
					cmd = new ChannelHelpCommand(sender, args);
				} else if (Channels.getInstance().getChannel(args[0]) != null) {
					String[] shiftedArgs = new String[args.length + 1];
					System.arraycopy(args, 0, shiftedArgs, 1, args.length);
					cmd = new ChannelSpeakCommand(sender, shiftedArgs);
				} else {
					// notify sender and exit
					Channels.notify(sender, "channels.usage.unknown-command", ImmutableMap.of("command", args[0]));
					return;
				}
			} else {
				cmd = new ChannelHelpCommand(sender, args);
			}
		} else if (command.equals("pm")) {
			cmd = new PMCommand(sender, args);
		} else if (command.equals("reply")) {
			cmd = new ReplyCommand(sender, args);
		} else if (command.equals("afk")) {
			cmd = new AFKCommand(sender, args);
		} else if (command.equals("dnd")) {
			cmd = new DNDCommand(sender, args);
		} else if (command.equals("ignore")) {
			cmd = new IgnoreCommand(sender, args);
		} else {
			cmd = new ChannelHelpCommand(sender, args);
		}
		
		// execute command
		if (sender.hasPermission(CommandPermission.valueOf(cmd.getClass().getSimpleName()).toString())) {
			if (cmd.validateInput()) {
				cmd.execute();
			} else {
				Channels.notify(sender, "channels.usage."+cmd.getClass().getSimpleName());
			}
		} else {
			Channels.notify(sender, "channels.permission.insufficient-permission", ImmutableMap.of("permission", CommandPermission.valueOf(cmd.getClass().getSimpleName()).toString()));
		}
	}

	public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
		if ("channel".equals(getName())) {
			if (args.length <= 1) {
				return matchingCommands(sender, (args.length > 0) ? args[0] : "");
			} else if (args.length == 2) {
				if (args[0].toLowerCase().matches("^addserver|autofocus|autojoin|global|backend|remove|removeserver|addmod|ban|color|info|kick|password|removemod|rename|unban|speak|list|who|subscribe|unsubscribe$")) {
					return matchingChannels(args[1]);
				} else if (args[0].toLowerCase().matches("^mute|prefix|suffix$")) {
					return matchingPlayers(args[1]);
				} else if (args[0].toLowerCase().matches("^serverdefaultchannel$")) {
					return matchingServers(args[1]);
				}
			} else if (args.length == 3) {
				if (args[0].toLowerCase().matches("^addserver|removeserver$")) {
					return matchingServers(args[2]);
				} else if (args[0].toLowerCase().matches("^autofocus|autojoin|global|backend$")) {
					return matchingBoolean(args[2]);
				} else if (args[0].toLowerCase().matches("^serverdefaultchannel$")) {
					return matchingChannels(args[2]);
				} else if (args[0].toLowerCase().matches("^addmod|ban|kick|removemod|unban$")) {
					return matchingPlayers(args[2]);
				} else if (args[0].toLowerCase().matches("^color$")) {
					return matchingColors(args[2]);
				}
			} else if (args.length == 4) {
				if (args[0].toLowerCase().matches("^serverdefaultchannel$")) {
					return matchingBoolean(args[3]);
				}
			}
		}
		
		return matchingPlayers(args[args.length-1]);
	}
	
	/**
	 * tab completion / list of matching commands
	 * @param s
	 * @return
	 */
	private Iterable<String> matchingCommands(CommandSender sender, String s) {
		List<String> commands = new ArrayList<String>();
		if ("ignore".startsWith(s.toLowerCase()) && sender.hasPermission(CommandPermission.IgnoreCommand.toString())) {
			commands.add("ignore");
		}
		
		if ("subscribe".startsWith(s.toLowerCase()) && sender.hasPermission(CommandPermission.ChannelSubscribeCommand.toString())) {
			commands.add("subscribe");
		}
		if ("join".startsWith(s.toLowerCase()) && sender.hasPermission(CommandPermission.ChannelSubscribeCommand.toString())) {
			commands.add("join");
		}
		
		if ("unsubscribe".startsWith(s.toLowerCase()) && sender.hasPermission(CommandPermission.ChannelUnsubscribeCommand.toString())) {
			commands.add("unsubscribe");
		}
		if ("quit".startsWith(s.toLowerCase()) && sender.hasPermission(CommandPermission.ChannelUnsubscribeCommand.toString())) {
			commands.add("quit");
		}
		if ("leave".startsWith(s.toLowerCase()) && sender.hasPermission(CommandPermission.ChannelUnsubscribeCommand.toString())) {
			commands.add("leave");
		}
		
		if ("speak".startsWith(s.toLowerCase()) && sender.hasPermission(CommandPermission.ChannelSpeakCommand.toString())) {
			commands.add("leave");
		}
		if ("say".startsWith(s.toLowerCase()) && sender.hasPermission(CommandPermission.ChannelSpeakCommand.toString())) {
			commands.add("say");
		}
		
		if ("global".startsWith(s.toLowerCase()) && sender.hasPermission(CommandPermission.ChannelGlobalCommand.toString())) {
			commands.add("global");
		}
		
		if ("backend".startsWith(s.toLowerCase()) && sender.hasPermission(CommandPermission.ChannelBackendCommand.toString())) {
			commands.add("backend");
		}
		
		if ("format".startsWith(s.toLowerCase()) && sender.hasPermission(CommandPermission.ChannelFormatCommand.toString())) {
			commands.add("format");
		}
		
		if ("addserver".startsWith(s.toLowerCase()) && sender.hasPermission(CommandPermission.ChannelAddServerCommand.toString())) {
			commands.add("addServer");
		}
		
		if ("removeserver".startsWith(s.toLowerCase()) && sender.hasPermission(CommandPermission.ChannelRemoveServerCommand.toString())) {
			commands.add("removeServer");
		}
		
		if ("create".startsWith(s.toLowerCase()) && sender.hasPermission(CommandPermission.ChannelCreateCommand.toString())) {
			commands.add("create");
		}
		
		if ("open".startsWith(s.toLowerCase()) && sender.hasPermission(CommandPermission.ChannelOpenCommand.toString())) {
			commands.add("open");
		}
		
		if ("remove".startsWith(s.toLowerCase()) && sender.hasPermission(CommandPermission.ChannelRemoveCommand.toString())) {
			commands.add("remove");
		}
		if ("close".startsWith(s.toLowerCase()) && sender.hasPermission(CommandPermission.ChannelRemoveCommand.toString())) {
			commands.add("close");
		}
		
		if ("addMod".startsWith(s.toLowerCase()) && sender.hasPermission(CommandPermission.ChannelAddModCommand.toString())) {
			commands.add("addMod");
		}
		
		if ("removemod".startsWith(s.toLowerCase()) && sender.hasPermission(CommandPermission.ChannelRemoveModCommand.toString())) {
			commands.add("removeMod");
		}

		if ("autofocus".startsWith(s.toLowerCase()) && sender.hasPermission(CommandPermission.ChannelAutofocusCommand.toString())) {
			commands.add("autofocus");
		}

		if ("autojoin".startsWith(s.toLowerCase()) && sender.hasPermission(CommandPermission.ChannelAutojoinCommand.toString())) {
			commands.add("autojoin");
		}
		
		if ("ban".startsWith(s.toLowerCase()) && sender.hasPermission(CommandPermission.ChannelBanCommand.toString())) {
			commands.add("ban");
		}
		
		if ("kick".startsWith(s.toLowerCase()) && sender.hasPermission(CommandPermission.ChannelKickCommand.toString())) {
			commands.add("kick");
		}
		
		if ("unban".startsWith(s.toLowerCase()) && sender.hasPermission(CommandPermission.ChannelUnbanCommand.toString())) {
			commands.add("unban");
		}
		
		if ("color".startsWith(s.toLowerCase()) && sender.hasPermission(CommandPermission.ChannelColorCommand.toString())) {
			commands.add("color");
		}
		
		if ("mute".startsWith(s.toLowerCase()) && sender.hasPermission(CommandPermission.ChannelMuteCommand.toString())) {
			commands.add("mute");
		}
		
		if ("unmute".startsWith(s.toLowerCase()) && sender.hasPermission(CommandPermission.ChannelUnmuteCommand.toString())) {
			commands.add("unmute");
		}
		
		if ("prefix".startsWith(s.toLowerCase()) && sender.hasPermission(CommandPermission.ChannelPrefixCommand.toString())) {
			commands.add("prefix");
		}
		
		if ("suffix".startsWith(s.toLowerCase()) && sender.hasPermission(CommandPermission.ChannelSuffixCommand.toString())) {
			commands.add("suffix");
		}
		
		if ("serverdefaultchannel".startsWith(s.toLowerCase()) && sender.hasPermission(CommandPermission.ServerDefaultChannelCommand.toString())) {
			commands.add("serverDefaultChannel");
		}
		
		if ("list".startsWith(s.toLowerCase()) && sender.hasPermission(CommandPermission.ChannelListCommand.toString())) {
			commands.add("list");
		}
		if ("who".startsWith(s.toLowerCase()) && sender.hasPermission(CommandPermission.ChannelListCommand.toString())) {
			commands.add("who");
		}
		
		if ("info".startsWith(s.toLowerCase()) && sender.hasPermission(CommandPermission.ChannelInfoCommand.toString())) {
			commands.add("info");
		}
		
		if ("rename".startsWith(s.toLowerCase()) && sender.hasPermission(CommandPermission.ChannelRenameCommand.toString())) {
			commands.add("rename");
		}
		
		if ("password".startsWith(s.toLowerCase()) && sender.hasPermission(CommandPermission.ChannelPasswordCommand.toString())) {
			commands.add("password");
		}
		
		if ("help".startsWith(s.toLowerCase()) && sender.hasPermission(CommandPermission.ChannelHelpCommand.toString())) {
			commands.add("help");
		}
		return commands;
	}
	
	/**
	 * tab completion / list of matching players
	 * @param s
	 * @return
	 */
	public static Iterable<String> matchingPlayers(final String s) {
		return ProxyServer.getInstance().getPlayers().stream().filter(
		        player -> player.getName().toLowerCase().startsWith(s.toLowerCase())
        ).collect(Collectors.toList()).stream().map(
                CommandSender::getName
        ).collect(Collectors.toList());
	}
	
	/**
	 * tab completion / get a list of matching channels
	 * @param s
	 * @return
	 */
	private static Iterable<String> matchingChannels(final String s) {
		return Channels.getInstance().getChannels().values().stream().filter(
				channel -> channel.getName().toLowerCase().startsWith(s.toLowerCase())
		).collect(Collectors.toList()).stream().map(
				Channel::getName
		).collect(Collectors.toList());
	}
	
	/**
	 * tab completion / get a list of matching servers
	 * @param s
	 * @return
	 */
	private static Iterable<String> matchingServers(final String s) {
		return StreamSupport.stream(ProxyServer.getInstance().getServers().values().stream().filter(
				info -> info.getName().toLowerCase().startsWith(s.toLowerCase())
		).collect(Collectors.toList()).spliterator(), false).map(
				ServerInfo::getName
		).collect(Collectors.toList());
	}
	
	/**
	 * tab completion / auto complete boolean
	 * @param s
	 * @return
	 */
	private static Iterable<String> matchingBoolean(final String s) {
		if ("true".startsWith(s.toLowerCase())) {
			return Arrays.asList((new String[] {"true"}));
		} else if ("false".startsWith(s.toLowerCase())) {
			return Arrays.asList((new String[] {"false"}));
		}
		
		return Collections.emptyList();
	}
	
	/**
	 * tab completion / list of matching colors
	 * @param s
	 * @return
	 */
	private static Iterable<String> matchingColors(final String s) {
		return Arrays.stream(ChatColor.values()).filter(
				color -> color.toString().toLowerCase().startsWith(s.toLowerCase())
		).collect(Collectors.toList()).stream().map(
				ChatColor::toString
		).collect(Collectors.toList());
	}
}
