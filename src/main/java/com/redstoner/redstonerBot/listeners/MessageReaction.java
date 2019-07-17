package com.redstoner.redstonerBot.listeners;

import com.redstoner.redstonerBot.managers.DataManager;
import com.redstoner.redstonerBot.managers.ReactionManager;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import net.dv8tion.jda.core.utils.Checks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class MessageReaction extends ListenerAdapter {
	private static final Logger logger = LoggerFactory.getLogger(MessageReaction.class);

	@Override
	public final void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
		Checks.notNull(event, "Event");

		final Guild                                         guild    = event.getGuild();
		final TextChannel                                   channel  = event.getChannel();
		final User                                          author   = event.getUser();
		final net.dv8tion.jda.core.entities.MessageReaction reaction = event.getReaction();

		final String messageId = event.getMessageId();

		final Member self = guild.getSelfMember();

		if (!Objects.equals(guild.getId(), DataManager.getConfigValue("guild_id"))) return;
		if (author.isBot()) return;
		if (!self.hasPermission(channel, Permission.MESSAGE_WRITE)) return;
		if (!self.hasPermission(channel, Permission.MESSAGE_EMBED_LINKS)) return;

		logger.info("[" + messageId + "] " + author.getAsTag() + " -> + " + reaction.getReactionEmote().getName());

		ReactionManager.handle(messageId, reaction, author, true);
	}

	@Override
	public final void onGuildMessageReactionRemove(GuildMessageReactionRemoveEvent event) {
		Checks.notNull(event, "Event");

		final Guild                                         guild    = event.getGuild();
		final TextChannel                                   channel  = event.getChannel();
		final User                                          author   = event.getUser();
		final net.dv8tion.jda.core.entities.MessageReaction reaction = event.getReaction();

		final String messageId = event.getMessageId();

		final Member self = guild.getSelfMember();

		if (!Objects.equals(guild.getId(), DataManager.getConfigValue("guild_id"))) return;
		if (author.isBot()) return;
		if (!self.hasPermission(channel, Permission.MESSAGE_WRITE)) return;
		if (!self.hasPermission(channel, Permission.MESSAGE_EMBED_LINKS)) return;

		logger.info("[" + messageId + "] " + author.getAsTag() + " -> - " + reaction.getReactionEmote().getName());

		ReactionManager.handle(messageId, reaction, author, false);
	}
}