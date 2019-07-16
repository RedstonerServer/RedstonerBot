package com.redstoner.redstonerBot.commands;

import com.redstoner.redstonerBot.Main;
import com.redstoner.redstonerBot.managers.DiscordManager;
import net.dv8tion.jda.core.entities.*;

public class StopCommand implements Command {
	@Override
	public boolean execute(Guild guild, TextChannel channel, Message message, User author, Member self, String command, String[] params) {
		if (!guild.getOwnerId().equals(author.getId())) return false;

		message.getChannel().sendMessage("This bot will stop in 5 seconds, as requested by " + author.getAsTag()).queue(DiscordManager::expireMessage);

		new Thread(() -> {
			try {
				Thread.sleep(6000);
			} catch (InterruptedException ignored) {
			}

			Main.stop();
		}).start();

		return true;
	}
}
