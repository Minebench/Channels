package net.zaiyers.Channels.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;

import java.util.List;

public abstract class AbstractCommandExecutor implements SimpleCommand {
	private final String name;
	private final String permission;
	private final String[] aliases;

	public AbstractCommandExecutor(String name) {
		this(name, null);
	}

	public AbstractCommandExecutor(String name, String permission, String... aliases) {
		this.name = name;
		this.permission = permission;
		this.aliases = aliases;
	}

	public abstract void execute(CommandSource sender, String[] args);

	public abstract List<String> onTabComplete(CommandSource sender, String[] args);

	public void execute(SimpleCommand.Invocation invocation) {
		execute(invocation.source(), invocation.arguments());
	}

	public List<String> suggest(final SimpleCommand.Invocation invocation) {
		return onTabComplete(invocation.source(), invocation.arguments());
	}

	public boolean hasPermission(final SimpleCommand.Invocation invocation) {
		return invocation.source().hasPermission(permission);
	}

	public String getName() {
		return name;
	}

	public String getPermission() {
		return permission;
	}

	public String[] getAliases() {
		return aliases;
	}
}
