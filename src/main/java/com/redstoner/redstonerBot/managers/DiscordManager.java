package com.redstoner.redstonerBot.managers;

import com.redstoner.redstonerBot.Env;
import com.redstoner.redstonerBot.Manager;
import com.redstoner.redstonerBot.listeners.MessageReceived;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.util.concurrent.TimeUnit;

public class DiscordManager implements Manager {
	private static final Logger logger = LoggerFactory.getLogger(DiscordManager.class);

	private JDA jda;

	public boolean start() {
		logger.info("Discord Manager starting...");

		try {
			final JDABuilder builder = new JDABuilder(AccountType.BOT);

			builder.setAutoReconnect(true);
			builder.setAudioEnabled(true);

			builder.setToken(Env.TOKEN);

			builder.addEventListener(new MessageReceived());

			jda = builder.build().awaitReady();
		} catch (LoginException e) {
			logger.error("The discord token is invalid!");
			return false;
		} catch (Throwable t) {
			logger.error("JDA setup error:", t);
			return false;
		}

		Guild guild = jda.getGuildById(DataManager.getConfigValue("guild_id"));

		String guildName       = guild.getName();
		String infoChannelName = guild.getTextChannelById(DataManager.getConfigValue("info_channel_id")).getName();

		logger.info("Discord Manager started and logged in as '" + jda.getSelfUser().getName() + "'");
		logger.info("Listening to guild '" + guildName + "'");
		logger.info("Using channel '" + infoChannelName + "' as the info channel");

		logger.info("Discord Manager started!");
		return true;
	}

	public boolean stop() {
		logger.info("Discord Manager stopping...");

		jda.shutdown();

		logger.info("Discord Manager stopped!");
		return true;
	}

	public static void expireMessage(Message message) {
		message.delete().reason("Redstoner Bot message expiry").queueAfter(5, TimeUnit.SECONDS);
	}
}
