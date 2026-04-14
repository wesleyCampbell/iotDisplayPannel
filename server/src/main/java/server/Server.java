package server;

import dataaccess.exception.*;
import dataaccess.DatabaseManager;
import dataaccess.habits.HabitsDatabaseManager;

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

	private HabitsDatabaseManager habitsDB;
	
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
		// Init the connection to the habits database
		try {
			this.habitsDB = new HabitsDatabaseManager();
			this.habitsDB.initDatabase();
		} catch (DataAccessException ex) {
			throw new RuntimeException("Failed to load habit database: " + ex.getMessage(), ex);
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
