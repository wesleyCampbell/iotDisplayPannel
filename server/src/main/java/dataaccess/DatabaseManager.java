package dataaccess;

import dataaccess.exception.*;

import java.sql.*;

import java.util.Properties;
import java.util.List;
import java.util.ArrayList;

import org.jooq.*;
import org.jooq.Record;
import org.jooq.types.*;
import org.jooq.impl.DSL;
import static org.jooq.impl.DSL.*;

import static jooq.habits.Tables.*;

public class DatabaseManager {
	//
	// ============================ GLOBALS ================================
	//
	
	private static final String DB_PROPERTIES_FILE = "db.properties";

	private static final String DB_URL_TEMPLATE = "jdbc:mariadb://%s:%d";

	protected static record DatabaseProperties(String name,
									  String username,
									  String password,
									  String connectionUrl) {};

	//
	// ============================ USER MESSAGES ==========================
	//
	
	private static final String NO_LOCATE_DB_PROPS_FILE_ERR_TEMPLATE = """
		ERROR: Unable to locate property file '%s'! Is it located within the classpath?""";
	private static final String INVALID_PORT_MSG_TEMPLATE = """
		ERROR: Invalid database port '%s' provided. Must be an iteger value.""";


	//
	// ============================ CONSTRUCTORS ==========================
	//
	
	private final String dbPropertiesFile;
	private final DatabaseProperties properties;

	/**
	 * Default constructor that assumes a MariaDB database.
	 *
	 * @param dbName The name of the database that the DatabaseManager will connect to
	 */
	public DatabaseManager(String dbName) {
		this.dbPropertiesFile = DB_PROPERTIES_FILE;

		// Load the properties
		Properties props = this.loadPropertiesFromFile(this.dbPropertiesFile);	
		this.properties = this.parseDatabaseProperties(dbName, props);
	}

	/**
	 * Constructor that allows manual setting of database properties
	 *
	 * @param properties The Database properties
	 */
	public DatabaseManager(DatabaseProperties properties) {
		this.dbPropertiesFile = "";
		this.properties = properties;
	}

	//
	// ======================== INIT HELPER FUNCTIONS =========================
	//

	/**
	 * Takes in a string to a file and loads the properties from it into a 
	 * `Properties` object.
	 *
	 * @param dpPropFile The properties file name
	 *
	 * @return The `Properties` object
	 */
	private Properties loadPropertiesFromFile(String dbPropFile) {
		try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(dbPropFile)) {
			if (propStream == null) {
				throw new Exception(String.format(NO_LOCATE_DB_PROPS_FILE_ERR_TEMPLATE, dbPropFile));
			}

			Properties props = new Properties();
			props.load(propStream);
			return props;
		} catch (Exception ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}
	
	/**
	 * Takes in a database name and a `Properties` object and parses the info into a
	 * corerct `DatabaseProperties` object to store the data.
	 *
	 * @param dbName The database name
	 * @param props The `Properties` object containing the database information
	 *
	 * @return A `DatabaseProperties` object containing relevant database info
	 */
	private DatabaseProperties parseDatabaseProperties(String dbName, Properties props) {
		String formatStr = "db." + dbName + ".%s";

		// Extract the relevant properties
		String username = props.getProperty(String.format(formatStr, "username"));
		String password = props.getProperty(String.format(formatStr, "password"));
		String host = props.getProperty(String.format(formatStr, "host"));
		String portStr = props.getProperty(String.format(formatStr, "port"));

		// Verify the port is an integer
		int port;
		try {
			port = Integer.parseInt(portStr);
		} catch (NumberFormatException ex) {
			System.out.println(String.format(INVALID_PORT_MSG_TEMPLATE, portStr));
			//System.exit(1);
			throw new RuntimeException();
		}
		
		// Plug info into data object
DatabaseProperties outProps = new DatabaseProperties(
				dbName,
				username,
				password,
				String.format(DB_URL_TEMPLATE, host, port)
				);

		return outProps;
	}

	//
	// =========================== GETTERS ================================
	//
	
	public DatabaseProperties getProperties() {
		return this.properties;
	}

	//
	// =========================== DATABASE MANIPULATION METHODS =======================
	//

	/**
	 * Forms a connection to the database and returns a Connection object
	 * Note that the connection needs to be closed after use. The best way 
	 * to do so is to only call `getConn` in a try-with-resources block:
	 * ```
	 * try (Connection conn = databaseManager.getConn() {} ...
	 * ```
	 *
	 * @return a Connection to the database
	 */
	public Connection getConn() throws DataAccessException {
		try {
			Connection conn = DriverManager.getConnection(
					this.properties.connectionUrl(),
					this.properties.username(),
					this.properties.password()
			);

			conn.setCatalog(this.properties.name());
			return conn;
		} catch (SQLException ex) {
			String err = String.format("Failed to get connection to %s", this.properties.connectionUrl());
			throw new DataAccessException(err, ex);
		}	
	}
}
