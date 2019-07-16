package com.redstoner.redstonerBot.managers;

import com.redstoner.redstonerBot.Manager;
import com.redstoner.redstonerBot.commands.Command;
import com.redstoner.redstonerBot.commands.InfoCommand;
import com.redstoner.redstonerBot.commands.StopCommand;
import net.dv8tion.jda.core.entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class CommandManager implements Manager {
	private static final Logger logger = LoggerFactory.getLogger(CommandManager.class);

	private static Map<String, Command> commands = new HashMap<>();

	@Override
	public boolean start() {
		logger.info("Command Manager starting...");

		commands.put("info", new InfoCommand());
		commands.put("stop", new StopCommand());


		logger.info("Command Manager started!");
		return true;
	}

	@Override
	public boolean stop() {
		logger.info("Command Manager stopping...");

		commands.clear();

		logger.info("Command Manager stopped!");
		return true;
	}

	public static void execute(Guild guild, TextChannel channel, Message message, User author, Member self) {
		String   rawMsg = message.getContentRaw();
		String[] rawCmd = rawMsg.split(" ");
		String   cmd    = rawCmd[0].substring(1);
		String[] params = Arrays.copyOfRange(rawCmd, 1, rawCmd.length);

		Command command = commands.get(cmd);

		if (command == null) return;
		logger.info("[" + message.getId() + "] User '" + author.getAsTag() + "' executed command " + cmd + " with parameters " + Arrays.toString(params));

		if (command.execute(guild, channel, message, author, self, cmd, params)) {
			logger.info("[" + message.getId() + "] Command executed successfully!");
		} else {
			logger.error("[" + message.getId() + "] Error while executing command!");
		}

		message.delete().reason("Redstoner Bot command execution").queue();
	}
}
