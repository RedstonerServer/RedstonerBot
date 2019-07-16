package com.redstoner.redstonerBot.commands;

import net.dv8tion.jda.core.entities.*;

public interface Command {
	boolean execute(Guild guild, TextChannel channel, Message message, User author, Member self, String command, String[] params);
}
