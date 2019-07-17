package com.redstoner.redstonerBot;

import net.dv8tion.jda.core.entities.MessageReaction;
import net.dv8tion.jda.core.entities.User;

public interface ReactionHandler {
	void handle(String messageId, MessageReaction reaction, User author, boolean added);
}
