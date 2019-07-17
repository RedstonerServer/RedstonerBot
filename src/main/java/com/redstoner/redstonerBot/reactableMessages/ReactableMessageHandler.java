package com.redstoner.redstonerBot.reactableMessages;

import com.redstoner.redstonerBot.ReactionHandler;
import com.redstoner.redstonerBot.managers.DataManager;
import com.redstoner.redstonerBot.managers.DiscordManager;
import com.redstoner.redstonerBot.managers.ReactionManager;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Emote;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public abstract class ReactableMessageHandler {
	static final Logger logger = LoggerFactory.getLogger(ReactableMessageHandler.class);

	public String msgId;

	final JDA         jda;
	final Guild       guild;
	final TextChannel channel;

	ReactableMessageHandler(JDA jda, Guild guild, TextChannel channel) {
		this.jda = jda;
		this.guild = guild;
		this.channel = channel;

		this.msgId = DataManager.getConfigValue(getConfigNames().get("msg_id"));
	}

	abstract Map<String, String> getConfigNames();

	public abstract void send(Consumer<? super Message> onSent);

	public abstract ReactionHandler getReactionHandler();

	public abstract Set<String> getReactions();

	public void checkMessage() {
		logger.info("[Check] Checking message ID: " + msgId);

		try {
			checkAfterMessageFound(channel.getMessageById(msgId).complete());
		} catch (Exception e) {
			logger.info("[Check] Message with ID " + msgId + " does not exist, sending new one!");
			send(this::checkAfterMessageFound);
		}
	}

	private void checkAfterMessageFound(Message message) {
		msgId = message.getId();
		DataManager.setConfigValue(getConfigNames().get("msg_id"), msgId);

		logger.info("[Check] Found message with ID: " + msgId);

		checkReactions(message, getReactions());
		ReactionManager.setHandler(msgId, getReactionHandler());

		logger.info("[Check] Set reaction handler for message ID " + msgId);
	}

	private void checkReactions(Message message, Set<String> reactions) {
		if (reactions != null) {
			for (String emojiName : reactions) {
				try {
					logger.info("Adding reaction emote: " + emojiName);

					Object reactionEmoji = DiscordManager.getEmoteByName(emojiName);

					if (reactionEmoji instanceof Emote) {
						message.addReaction((Emote) reactionEmoji).queue();
					} else if (reactionEmoji instanceof String) {
						message.addReaction((String) reactionEmoji).queue();
					} else {
						logger.error("Could not find emote for name '" + emojiName + "'");
					}
				} catch (Exception e) {
					logger.error("Error adding reaction: ", e);
				}
			}
		}
	}
}
