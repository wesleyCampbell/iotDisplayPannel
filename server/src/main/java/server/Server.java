package server;

import dataaccess.exception.*;
import dataaccess.DatabaseManager;
import dataaccess.habits.HabitsDAOManager;

import io.javalin.*;

public class Server {
	//
	// ==================== DATABASE ACCESS INTERFACES ========================
	//
	
	//
	// ==================== HTTP REQUEST HANDLERS =============================
	//
	
	//
	// ==================== CONSTRUCTORS =============================
	//
	
	private final Javalin javalin;

	private HabitsDAOManager habitsDAO;
	
	public Server() {
		// Initialize server
		this.javalin = Javalin.create(config -> config.staticFiles.add("web"));
		
		// Initialize database connections
		this.initDatabaseConnections();	
		
		// Initialize request handlers

		// Initialize HTTP endpoints
		this.initHttpEndpoints();
	}

	private void initDatabaseConnections() {
		// Init the database
		try {
			DatabaseManager.createDatabase();
		} catch (DataAccessException ex) {
			throw new RuntimeException("Database failed to load", ex);
		}

		// Init the connection to the habits database
		try {
			this.habitsDAO = new HabitsDAOManager();
		} catch (DataAccessException ex) {
			throw new RuntimeException("Failed to load habit database");
		}
	}

	private void initHttpEndpoints() {

	}

	//
	// ===================== SERVER METHODS ========================
	//
	
	/**
	 * Starts the server on a given port.
	 *
	 * @param port The desired port to start the server on
	 * @returns The actual port the server is running on
	 */
	public int run(int port) {
		this.javalin.start(port);
		return this.javalin.port();
	}

	/**
	 * Stops the server gracefully
	 */
	public void stop() {
		this.javalin.stop();
	}
}
