package server;

import dataaccess.exception.*;
import dataaccess.DatabaseManager;
import dataaccess.habits.HabitsDatabaseManager;

import handler.habits.*;

import io.javalin.*;

public class Server {
	//
	// ===================== GLOBALS =============================
	//
	
	private static final String HABITS_ENDPOINT = "/habits";
	private static final String HABIT_HISTORY_ENDPOINT = String.format("%s/history", HABITS_ENDPOINT);

	//
	// ==================== DATABASE ACCESS INTERFACES ========================
	//
	
	private HabitHandler habitHandler;
	private HabitHistoryHandler habitHistoryHandler;

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
		this.initHandlers();

		// Initialize HTTP endpoints
		this.initHttpEndpoints();
	}

	private void initHandlers() {
		this.habitHandler = new HabitHandler(this.habitsDB);
		this.habitHistoryHandler = new HabitHistoryHandler(this.habitsDB);
	}

	private void initDatabaseConnections() {
		// Init the connection to the habits database
		try {
			this.habitsDB = new HabitsDatabaseManager();
		} catch (DataAccessException ex) {
			throw new RuntimeException("Failed to load habit database: " + ex.getMessage(), ex);
		}
	}

	private void initHttpEndpoints() {
		// Habit HTTP endpoints
		javalin.post(HABITS_ENDPOINT, this.habitHandler::createHabitRequest);
		javalin.delete(HABITS_ENDPOINT, this.habitHandler::deleteHabitRequest); 
		javalin.get(HABITS_ENDPOINT, this.habitHandler::listHabitsRequest);

		// Habit History endpoints
		javalin.post(HABIT_HISTORY_ENDPOINT, this.habitHistoryHandler::createHistoryRequest);
		javalin.delete(HABIT_HISTORY_ENDPOINT, this.habitHistoryHandler::deleteHistoryRequest);
		javalin.get(HABIT_HISTORY_ENDPOINT, this.habitHistoryHandler::getHistoryRequest);
		javalin.put(HABIT_HISTORY_ENDPOINT, this.habitHistoryHandler::updateHistoryRequest);
		
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
