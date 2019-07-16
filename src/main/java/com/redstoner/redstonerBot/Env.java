package com.redstoner.redstonerBot;

public class Env {
	public static final String TOKEN = System.getenv("TOKEN");

	public static final String MYSQL_HOST = System.getenv("MYSQL_HOST");
	public static final String MYSQL_PORT = System.getenv("MYSQL_PORT");

	public static final String MYSQL_USER = System.getenv("MYSQL_USER");
	public static final String MYSQL_PASS = System.getenv("MYSQL_PASS");

	public static final String MYSQL_DB = System.getenv("MYSQL_DB");
}
