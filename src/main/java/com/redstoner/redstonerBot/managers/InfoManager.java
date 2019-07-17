package com.redstoner.redstonerBot.managers;

import com.redstoner.redstonerBot.Manager;
import com.redstoner.redstonerBot.reactableMessages.InfoReactableMessageHandler;
import com.redstoner.redstonerBot.reactableMessages.OptInReactableMessageHandler;
import com.redstoner.redstonerBot.reactableMessages.ReactableMessageHandler;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class InfoManager implements Manager {
	private static final Logger logger = LoggerFactory.getLogger(InfoManager.class);

	private static List<ReactableMessageHandler> messageHandlers = new ArrayList<>();

	@Override
	public boolean start() {
		logger.info("Info Manager starting...");

		JDA         jda     = DiscordManager.getJda();
		Guild       guild   = jda.getGuildById(DataManager.getConfigValue("guild_id"));
		TextChannel channel = guild.getTextChannelById(DataManager.getConfigValue("info_channel_id"));

		messageHandlers.add(new InfoReactableMessageHandler(jda, guild, channel));
		messageHandlers.add(new OptInReactableMessageHandler(jda, guild, channel));

		checkMessages();

		logger.info("Info Manager started!");
		return true;
	}

	@Override
	public boolean stop() {
		logger.info("Info Manager stopping...");

		for (ReactableMessageHandler handler : messageHandlers) {
			ReactionManager.removeHandler(handler.msgId);
		}

		messageHandlers.clear();

		logger.info("Info Manager stopped!");
		return true;
	}

	public static void checkMessages() {
		new Thread(() -> {
			for (ReactableMessageHandler handler : messageHandlers) {
				handler.checkMessage();
			}
		}).start();
	}


}
