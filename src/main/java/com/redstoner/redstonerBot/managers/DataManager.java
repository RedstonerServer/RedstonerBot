package com.redstoner.redstonerBot.managers;

import com.redstoner.redstonerBot.Env;
import com.redstoner.redstonerBot.Manager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataManager implements Manager {
	private static final Logger logger = LoggerFactory.getLogger(DataManager.class);

	private static final String[] requiredTables = { "config", "opt_in", "rules", "rule_sections", "rule_agree_reactions" };

	public boolean start() {
		logger.info("Data Manager starting...");

		if (checkConnection()) {
			logger.info("Data Manager started!");
			return true;
		}

		logger.error("Data Manager failed to start!");
		return false;

	}

	public boolean stop() {
		logger.info("Data Manager stopping...");

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
			ResultSet  rs   = conn.prepareStatement("SHOW TABLES").executeQuery();

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
		try {
			Connection        conn = getConnection();
			PreparedStatement ps   = conn.prepareStatement("SELECT * FROM config WHERE name=?");

			ps.setString(1, name);

			ResultSet rs = ps.executeQuery();
			rs.next();

			String value = rs.getString(2);

			conn.close();
			return value;
		} catch (SQLException e) {
			logger.error("SQL error:", e);
			return null;
		}
	}

	public static boolean setConfigValue(String name, String value) {
		String existing = getConfigValue(name);

		if (existing != null && existing.equals(value)) return true;

		try {
			Connection        conn = getConnection();
			PreparedStatement ps   = conn.prepareStatement("INSERT INTO config (name, value) VALUES (?, ?) ON DUPLICATE KEY UPDATE value=?");

			ps.setString(1, name);
			ps.setString(2, value);
			ps.setString(3, value);

			boolean succ = ps.execute();

			conn.close();
			return succ;
		} catch (SQLException e) {
			logger.error("SQL error:", e);
			return false;
		}
	}

	public static Map<Integer, String> getRuleSections() {
		try {
			Connection conn = getConnection();
			ResultSet  rs   = conn.prepareStatement("SELECT * FROM rule_sections").executeQuery();

			Map<Integer, String> sections = new HashMap<>();

			while (rs.next()) {
				sections.put(rs.getInt(1), rs.getString(2));
			}

			conn.close();
			return sections;
		} catch (SQLException e) {
			logger.error("SQL error:", e);
			return new HashMap<>();
		}
	}

	public static List<String> getRules(int sectionId) {
		try {
			Connection        conn = getConnection();
			PreparedStatement ps   = conn.prepareStatement("SELECT * FROM rules WHERE section_id=?");

			ps.setInt(1, sectionId);

			ResultSet rs = ps.executeQuery();

			List<String> rules = new ArrayList<>();

			while (rs.next()) {
				rules.add(rs.getString(2));
			}

			conn.close();
			return rules;
		} catch (SQLException e) {
			logger.error("SQL error:", e);
			return null;
		}
	}

	public static Map<String, String> getruleAgreeReactions() {
		try {
			Connection conn = getConnection();
			ResultSet  rs   = conn.prepareStatement("SELECT * FROM rule_agree_reactions").executeQuery();

			Map<String, String> reactions = new HashMap<>();

			while (rs.next()) {
				reactions.put(rs.getString(3), rs.getString(2));
			}

			conn.close();
			return reactions;
		} catch (SQLException e) {
			logger.error("SQL error:", e);
			return null;
		}
	}

	public static List<Map<String, String>> getOptins() {
		try {
			Connection conn = getConnection();
			ResultSet  rs   = conn.prepareStatement("SELECT * FROM opt_in").executeQuery();

			List<Map<String, String>> optins = new ArrayList<>();

			while (rs.next()) {
				Map<String, String> optin = new HashMap<>();

				optin.put("role_id", rs.getString(3));
				optin.put("channel_id", rs.getString(4));
				optin.put("emoji", rs.getString(5));

				optins.add(optin);
			}

			conn.close();
			return optins;
		} catch (SQLException e) {
			logger.error("SQL error:", e);
			return null;
		}
	}
}
