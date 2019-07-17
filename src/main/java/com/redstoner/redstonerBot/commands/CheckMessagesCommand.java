package com.redstoner.redstonerBot.commands;

import com.redstoner.redstonerBot.managers.DiscordManager;
import com.redstoner.redstonerBot.managers.InfoManager;
import net.dv8tion.jda.core.entities.*;

public class CheckMessagesCommand implements Command {
	@Override
	public boolean execute(Guild guild, TextChannel channel, Message message, User author, Member self, String command, String[] params) {
		if (!guild.getOwnerId().equals(author.getId())) {
			message.getChannel().sendMessage("You are not allowed to run this command " + author.getAsMention()).queue(DiscordManager::expireMessage);

			return false;
		}

		message.getChannel().sendMessage("Rechecking messages " + author.getAsMention()).queue(DiscordManager::expireMessage);

		InfoManager.checkMessages();

		return true;
	}
}
