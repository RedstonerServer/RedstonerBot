package com.redstoner.redstonerBot.reactableMessages;

import com.redstoner.redstonerBot.ReactionHandler;
import com.redstoner.redstonerBot.managers.DataManager;
import com.redstoner.redstonerBot.managers.DiscordManager;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.managers.GuildController;

import java.util.*;
import java.util.function.Consumer;

public class OptInReactableMessageHandler extends ReactableMessageHandler {
	static Map<String, String> configNames = new HashMap<>();

	static {
		configNames.put("msg_id", "opt_in_msg_id");

		configNames.put("prefix", "opt_in_prefix");
		configNames.put("suffix", "opt_in_suffix");

		configNames.put("channel_format", "opt_in_channel_format");
		configNames.put("role_format", "opt_in_role_format");
	}

	public OptInReactableMessageHandler(JDA jda, Guild guild, TextChannel channel) {
		super(jda, guild, channel);
	}

	@Override
	Map<String, String> getConfigNames() {
		return configNames;
	}

	@Override
	public void send(Consumer<? super Message> onSent) {
		String prefix = DataManager.getConfigValue(configNames.get("prefix"));
		String suffix = DataManager.getConfigValue(configNames.get("suffix"));

		String channelFormat = DataManager.getConfigValue(configNames.get("channel_format"));
		String roleFormat    = DataManager.getConfigValue(configNames.get("role_format"));

		MessageBuilder msg = new MessageBuilder(prefix + "\n");

		List<Map<String, String>> optIns = DataManager.getOptins();

		if (optIns != null) {
			List<Map<String, String>> channelOptIns = new ArrayList<>();
			List<Map<String, String>> roleOptIns    = new ArrayList<>();

			for (Map<String, String> optIn : optIns) {
				if (optIn.get("channel_id") != null) {
					channelOptIns.add(optIn);
				} else {
					roleOptIns.add(optIn);
				}
			}

			if (channelOptIns.size() > 0) {
				for (Map<String, String> optIn : channelOptIns) {
					TextChannel optinChannel   = guild.getTextChannelById(optIn.get("channel_id"));
					String      channelMention = optinChannel.getAsMention();

					String emoji         = optIn.get("emoji");
					Object resolvedEmoji = DiscordManager.getEmoteByName(emoji);

					if (resolvedEmoji instanceof String) emoji = (String) resolvedEmoji;
					if (resolvedEmoji instanceof Emote) emoji = ((Emote) resolvedEmoji).getAsMention();

					String reactionMsg = channelFormat;

					reactionMsg = reactionMsg.replaceAll("%emoji", emoji);
					reactionMsg = reactionMsg.replaceAll("%channel", channelMention);

					msg.append(reactionMsg);
					msg.append("\n");
				}

				msg.append("\n");
			}

			if (roleOptIns.size() > 0) {
				for (Map<String, String> optIn : roleOptIns) {
					Role   optinRole   = guild.getRoleById(optIn.get("role_id"));
					String roleMention = optinRole.getAsMention();

					String emoji         = optIn.get("emoji");
					Object resolvedEmoji = DiscordManager.getEmoteByName(emoji);

					if (resolvedEmoji instanceof String) emoji = (String) resolvedEmoji;
					if (resolvedEmoji instanceof Emote) emoji = ((Emote) resolvedEmoji).getAsMention();

					String reactionMsg = roleFormat;

					reactionMsg = reactionMsg.replaceAll("%emoji", emoji);
					reactionMsg = reactionMsg.replaceAll("%role", roleMention);

					msg.append(reactionMsg);
					msg.append("\n");
				}

				msg.append("\n");
			}
		}

		msg.append(suffix);

		channel.sendMessage(msg.build()).queue(onSent);
	}

	@Override
	public ReactionHandler getReactionHandler() {
		return (messageId, reaction, author, added) -> {
			String emoteName = reaction.getReactionEmote().getName();
			String authorTag = author.getAsTag();

			if (added) {
				logger.info("Opt in reaction " + emoteName + " by user " + authorTag + " added!");
			} else {
				logger.info("Opt in reaction " + emoteName + " by user " + authorTag + " removed!");
			}

			List<Map<String, String>> optIns = DataManager.getOptins();
			if (optIns == null) return;

			String roleId = null;

			for (Map<String, String> optIn : optIns) {
				String rId = optIn.get("role_id");
				String e   = optIn.get("emoji");

				if (rId != null && e != null) {
					if (reaction.getReactionEmote().isEmote()) {
						if (e.equals(emoteName)) {
							roleId = rId;
							break;
						}
					} else {
						Object discordEmote = DiscordManager.getEmoteByName(e);

						if (discordEmote != null) {
							if (discordEmote instanceof String && discordEmote.equals(emoteName)) {
								roleId = rId;
								break;
							} else if (discordEmote instanceof Emote && ((Emote) discordEmote).getName().equals(emoteName)) {
								roleId = rId;
								break;
							}
						}
					}
				}
			}

			if (roleId != null) {
				GuildController guildController = guild.getController();
				Member          authorMember    = guild.getMember(author);
				Role            optInRole       = guild.getRoleById(roleId);

				if (added) {
					logger.info("Adding role " + optInRole.getName() + " to user " + authorTag);
					guildController.addSingleRoleToMember(authorMember, optInRole).queue();
				} else {
					logger.info("Removing role " + optInRole.getName() + " from user " + authorTag);
					guildController.removeSingleRoleFromMember(authorMember, optInRole).queue();
				}
			} else if (added) {
				logger.info("Removing rogue reaction " + emoteName + " by user " + authorTag);
				reaction.removeReaction(author).queue();
			}
		};
	}

	@Override
	public Set<String> getReactions() {
		Set<String> reactions = new HashSet<>();

		List<Map<String, String>> optIns = DataManager.getOptins();

		if (optIns != null) {
			for (Map<String, String> optIn : optIns) {
				reactions.add(optIn.get("emoji"));
			}
		}

		return reactions;
	}
}
