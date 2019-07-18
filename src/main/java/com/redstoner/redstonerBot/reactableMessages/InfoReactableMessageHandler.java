package com.redstoner.redstonerBot.reactableMessages;

import com.redstoner.redstonerBot.ReactionHandler;
import com.redstoner.redstonerBot.managers.DataManager;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.managers.GuildController;

import java.util.*;

public class InfoReactableMessageHandler extends ReactableMessageHandler {
	static Map<String, String> configNames = new HashMap<>();

	static {
		configNames.put("msg_id", "info_msg_id");

		configNames.put("prefix", "info_prefix");
		configNames.put("suffix", "info_suffix");

		configNames.put("format", "info_format");
	}

	public InfoReactableMessageHandler(JDA jda, Guild guild, TextChannel channel) {
		super(jda, guild, channel);
	}

	@Override
	Map<String, String> getConfigNames() {
		return configNames;
	}

	@Override
	public Message getMessage() {
		String prefix = DataManager.getConfigValue(configNames.get("prefix"));
		String suffix = DataManager.getConfigValue(configNames.get("suffix"));

		String format = DataManager.getConfigValue(configNames.get("format"));

		String rulesLang = DataManager.getConfigValue("rules_lang");

		MessageBuilder msg = new MessageBuilder(prefix + "\n");

		Map<Integer, String> ruleSections = DataManager.getRuleSections();

		for (Integer sectionId : ruleSections.keySet()) {
			List<String> rules = DataManager.getRules(sectionId);

			if (rules != null) {
				msg.append("**");
				msg.append(ruleSections.get(sectionId));
				msg.append("**\n```");
				msg.append(rulesLang);
				msg.append("\n");

				for (int i = 0; i < rules.size(); i++) {
					msg.append(i + 1);
					msg.append(". ");
					msg.append(rules.get(i));
					msg.append("\n");
				}

				msg.append("```\n");
			}
		}

		Map<String, String> reactions = DataManager.getruleAgreeReactions();

		if (reactions != null) {
			for (String emojiName : reactions.keySet()) {
				String why = reactions.get(emojiName);

				String reactionMsg = format;

				reactionMsg = reactionMsg.replaceAll("%emoji", ":" + emojiName + ":");
				reactionMsg = reactionMsg.replaceAll("%why", why);

				msg.append(reactionMsg);
				msg.append("\n");
			}
		}

		msg.append("\n");

		msg.append(suffix);

		return msg.build();
	}


	@Override
	public ReactionHandler getReactionHandler() {
		return (messageId, reaction, author, added) -> {
			String emoteName = reaction.getReactionEmote().getName();
			String authorTag = author.getAsTag();

			if (added) {
				logger.info("Info reaction " + emoteName + " by user " + authorTag + " added!");
			} else {
				logger.info("Info reaction " + emoteName + " by user " + authorTag + " removed!");
			}

			switch (emoteName) {
				case "\uD83D\uDC4C":
				case "\uD83D\uDC4D":
				case "\u270B":
					channel.getMessageById(messageId).queue(message -> {
						List<MessageReaction> reactions = message.getReactions();

						new Thread(() -> {
							int count = 0;

							for (MessageReaction mr : reactions) {
								List<User> users = mr.getUsers().complete();

								if (users.contains(author)) {
									count++;
								}
							}

							logger.info("User " + author.getAsTag() + " has " + count + " reactions!");

							GuildController guildController = guild.getController();
							Member          authorMember    = guild.getMember(author);
							Role            rule_agree_role = guild.getRoleById(DataManager.getConfigValue("rule_agree_role_id"));

							int requiredReactions = Integer.parseInt(DataManager.getConfigValue("rule_agree_required_reactions"));

							if (count >= requiredReactions) {
								logger.info("Adding role " + rule_agree_role.getName() + " to user " + authorTag);
								guildController.addSingleRoleToMember(authorMember, rule_agree_role).queue();
							} else {
								logger.info("Removing role " + rule_agree_role.getName() + " from user " + authorTag);
								guildController.removeSingleRoleFromMember(authorMember, rule_agree_role).queue();
							}
						}).start();
					});

					break;
				default:
					if (added) {
						logger.info("Removing rogue reaction " + emoteName + " by user " + authorTag);
						reaction.removeReaction(author).queue();
					}
			}
		};
	}

	@Override
	public Set<String> getReactions() {
		Map<String, String> r = DataManager.getruleAgreeReactions();

		if (r != null) {
			return r.keySet();
		} else {
			return new HashSet<>();
		}
	}
}
