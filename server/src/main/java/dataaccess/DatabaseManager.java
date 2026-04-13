package dataaccess;

import dataaccess.exception.*;

import java.sql.*;
import java.util.Properties;

public class DatabaseManager {
	//
	// ==================== GLOBAL VARIABLES ==============================
	//
	
	private static final String DB_PROPERTIES_FILE = "db.properties";
	private static final String DB_URL_TEMPLATE = "jdbc:mariadb://%s:%d";
	
	//
	// ==================== MESSAGES ==============================
	//
	
	private static final String NO_LOCATE_DB_PROPERTIES_FILE_MSG = String.format(
			"ERROR: Unable to locate `%s' file. Is it in the file path?", 
			DB_PROPERTIES_FILE);
	private static final String INVALID_PORT_MSG_TEMPLATE = """
		ERROR: Invalid database port '%s' provided. Must be an iteger value.""";

	//
	// ==================== DATABASE PARAMETERS ====================
	//
	
	private static String databaseName;
	private static String dbUsername;
	private static String dbPassword;
	private static String connectionUrl;

	//
	// ======================== INITIALIZATION METHODS ================
	//

	/*
	 * Load the database from the `db.properties` file.
	 */
	static {
		loadPropertiesFromResources();
	}

	/**
	 * Reads the `db.properties` file to get all the relevant information to connect to the database.
	 */ 
	private static void loadPropertiesFromResources() {
		// Try to open the database property file
		try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(DB_PROPERTIES_FILE)) {
			if (propStream == null) { 
				throw new Exception("Unable to load db properties file");
			}

			// Load the file properties into a parser
			Properties props = new Properties();
			props.load(propStream);

			// Stores the properties in the static values
			loadProperties(props);
		} catch (Exception ex) {
			// If the file couldn't be found, it is a critical failure.
			System.out.println(NO_LOCATE_DB_PROPERTIES_FILE_MSG);
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	/**
	 * Loads the properties stored in a `Properties` object into the static variables
	 *
	 * @params props The `Property` object containing the DB properties
	 */
	private static void loadProperties(Properties props) {
		databaseName = props.getProperty("db.name");
		dbUsername = props.getProperty("db.user");
		dbPassword = props.getProperty("db.password");

		String host = props.getProperty("db.host");
		int port;
		try {
			port = Integer.parseInt(props.getProperty("db.port"));
		} catch (NumberFormatException ex) {
			System.out.println(String.format(INVALID_PORT_MSG_TEMPLATE, 
						props.getProperty("db.port")));
			System.exit(1);
			return;
		}	
		
		connectionUrl = String.format(DB_URL_TEMPLATE, host, port);
	}

	//
	// ========================= DATABASE MANAGEMENT METHODS =======================
	//
	
	/**
	 * Will try to create the database using the credentials in the `db.properties` file.
	 * Upon creation error, will throw `DataAccessException` to notify that creation has 
	 * failed.
	 */
	static public void createDatabase() throws DataAccessException {
		String statement = "CREATE DATABASE IF NOT EXISTS " + databaseName;
		try (var conn = DriverManager.getConnection(connectionUrl, dbUsername, dbPassword)) {
			PreparedStatement ps = conn.prepareStatement(statement);
			ps.executeUpdate();
		} catch (SQLException ex) {
			String err = String.format("Failed to create database %s", connectionUrl);
			throw new DataAccessException(err, ex);
		}
	}

	/**
	 * Creates a connection to the database and sets the catalog based upon the properties 
	 * specified in `db.properties` file. Connections are short lived and should be closed when
	 * done.
	 * Will throw a `DataAccessException upon failure to establish connection.
	 */
	public static Connection getConn() throws DataAccessException {
		try {
			Connection conn = DriverManager.getConnection(connectionUrl, dbUsername, dbPassword);
			conn.setCatalog(databaseName);
			return conn;
		} catch (SQLException ex) {
			String err = String.format("Failed to get connection to %s", connectionUrl);
			throw new DataAccessException(err, ex);
		}
	}
}
