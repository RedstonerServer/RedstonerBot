package com.redstoner.redstonerBot;

import com.redstoner.redstonerBot.managers.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Main {
	private static final Logger logger = LoggerFactory.getLogger(Main.class);

	private static final List<Manager> managers       = new ArrayList<>();
	private static final List<Manager> loadedManagers = new ArrayList<>();

	static {
		managers.add(new DataManager());
		managers.add(new ReactionManager());
		managers.add(new DiscordManager());
		managers.add(new CommandManager());
		managers.add(new InfoManager());
	}

	public static void main(final String... args) {
		logger.info("Starting RedstonerBot...");

		if (!start()) {
			stop();
			System.exit(1);
		}
	}

	private static boolean start() {
		loadedManagers.clear();

		for (Manager m : managers) {
			if (m.start()) {
				loadedManagers.add(m);
			} else {
				return false;
			}
		}

		return true;
	}

	public static boolean stop() {
		for (Manager m : getReversedManagers()) {
			if (!m.stop()) return false;
		}

		return true;
	}

	private static List<Manager> getReversedManagers() {
		List<Manager> reversedManagers = new ArrayList<>(loadedManagers);
		Collections.reverse(reversedManagers);

		return reversedManagers;
	}
}
