package com.redstoner.redstonerBot.listeners;

import com.redstoner.redstonerBot.managers.CommandManager;
import com.redstoner.redstonerBot.managers.DataManager;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.utils.Checks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class MessageReceived extends ListenerAdapter {
	private static final Logger logger = LoggerFactory.getLogger(MessageReceived.class);

	@Override
	public final void onGuildMessageReceived(GuildMessageReceivedEvent event) {
		Checks.notNull(event, "Event");

		final Guild       guild   = event.getGuild();
		final TextChannel channel = event.getChannel();
		final Message     message = event.getMessage();
		final User        author  = event.getAuthor();

		final Member self = guild.getSelfMember();

		if (!Objects.equals(guild.getId(), DataManager.getConfigValue("guild_id"))) return;
		if (author.isBot()) return;
		if (message.isTTS()) return;
		if (!self.hasPermission(channel, Permission.MESSAGE_WRITE)) return;
		if (!self.hasPermission(channel, Permission.MESSAGE_EMBED_LINKS)) return;

		String rawMessage = message.getContentRaw();

		// TOS :omegalul:
		// logger.info(author.getAsTag() + " -> " + rawMessage);

		String prefixChar = DataManager.getConfigValue("prefix_char");

		if (prefixChar != null && rawMessage.startsWith(prefixChar)) {
			CommandManager.execute(guild, channel, message, author, self);
		}
	}
}