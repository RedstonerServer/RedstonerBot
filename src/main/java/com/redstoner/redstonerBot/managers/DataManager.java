package com.redstoner.redstonerBot.managers;

import com.redstoner.redstonerBot.Env;
import com.redstoner.redstonerBot.Manager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataManager implements Manager {
	private static final Logger logger = LoggerFactory.getLogger(DataManager.class);

	private static final String[] requiredTables = { "config", "opt_in", "rules", "rule_agree_reactions" };

	private static Map<String, String> config = new HashMap<>();

	public boolean start() {
		logger.info("Data Manager starting...");

		if (checkConnection()) {
			logger.info("Loading config...");

			if (!loadConfig()) {
				logger.error("Data Manager failed to load config!");
				return false;
			}

			logger.info("Data Manager started!");
			return true;
		} else {
			logger.error("Data Manager failed to start!");
			return false;
		}
	}

	public boolean stop() {
		logger.info("Data Manager stopping...");

		config.clear();

		logger.info("Data Manager stopped!");
		return true;
	}

	private static String getConnectionString() {
		return "jdbc:mysql://"
		       + Env.MYSQL_HOST + ":" + Env.MYSQL_PORT
		       + "/" + Env.MYSQL_DB
		       + "?useUnicode=true&characterEncoding=UTF-8"
		       + "&user=" + Env.MYSQL_USER
		       + "&password=" + Env.MYSQL_PASS;
	}

	private static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(getConnectionString());
	}

	private boolean checkConnection() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");

			Connection conn = getConnection();
			ResultSet  rs   = conn.prepareStatement("show tables").executeQuery();

			List<String> tables = new ArrayList<>();

			while (rs.next()) {
				tables.add(rs.getString(1));
			}

			conn.close();

			for (String rt : requiredTables) {
				if (!tables.contains(rt)) {
					logger.error("The " + rt + " table is missing from the database!");
					return false;
				}
			}

			return true;
		} catch (ClassNotFoundException | SQLException e) {
			logger.error("SQL error:", e);
			return false;
		}
	}

	public static String getConfigValue(String name) {
		return config.get(name);
	}

	public boolean loadConfig() {
		config.clear();

		try {
			Connection conn = getConnection();
			ResultSet  rs   = conn.prepareStatement("SELECT * FROM config").executeQuery();

			while (rs.next()) {
				String name  = rs.getString(2);
				String value = rs.getString(3);

				config.put(name, value);
			}

			return true;
		} catch (SQLException e) {
			logger.error("SQL error:", e);
			return false;
		}
	}
}
