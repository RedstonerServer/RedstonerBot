package com.redstoner.redstonerBot.managers;

import com.redstoner.redstonerBot.Manager;
import com.redstoner.redstonerBot.ReactionHandler;
import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ReactionManager implements Manager {
	private static final Logger logger = LoggerFactory.getLogger(ReactionManager.class);

	private static final Map<String, ReactionHandler> handlers = new HashMap<>();

	@Override
	public boolean start() {
		logger.info("Reaction Manager starting...");

		logger.info("Reaction Manager started!");
		return true;
	}

	@Override
	public boolean stop() {
		logger.info("Reaction Manager stopping...");

		logger.info("Reaction Manager stopped!");
		return true;
	}

	public static void setHandler(String messageId, ReactionHandler handler) {
		handlers.put(messageId, handler);
	}

	public static void removeHandler(String messageId) {
		handlers.remove(messageId);
	}

	public static void handle(String messageId, MessageReaction reaction, User author, boolean added) {
		ReactionHandler handler = handlers.get(messageId);

		if (handler == null) return;

		handler.handle(messageId, reaction, author, added);
	}
}
