package com.redstoner.redstonerBot.commands;

import com.redstoner.redstonerBot.managers.DiscordManager;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.*;

import java.awt.*;

public class InfoCommand implements Command {
	@Override
	public boolean execute(Guild guild, TextChannel channel, Message message, User author, Member self, String command, String[] params) {
		EmbedBuilder embed = new EmbedBuilder();

		embed.setTitle("Information");
		embed.setDescription("This is a Discord bot built by psrcek for the Redstoner Guild.");

		embed.setColor(Color.ORANGE);
		embed.setFooter("Redstoner bot", "https://cdn.discordapp.com/avatars/576432236702859284/b08d4dc368b2e041ebb3fc208a2e8230.png");

		message.getChannel().sendMessage(embed.build()).queue(DiscordManager::expireMessage);

		return true;
	}
}
