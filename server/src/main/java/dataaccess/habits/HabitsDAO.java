package dataaccess.habits;

import dataaccess.exception.*;

import model.jooq.habits.tables.pojos.*;

import java.util.List;

public interface HabitsDAO {
	//
	// ====================== SELECTION METHODS ======================
	//
	
	/**
	 * Given a habitID, this method will try and fetch the associated habit_catalog object from the database. If not found, will throw a `ObjectNotFoundException`.
	 *
	 * @param habitID The habit_id of the desired habit_catalog entry.
	 *
	 * @return The Habit_Catalog object
	 */
	public HabitsCatalog getHabit(long habitID) throws DataAccessException;

	/** 
	 * Given a habit's name, this method will try to fetch the associated habit_catalog
	 * object from the database. If not found, will throw an `ObjectNotFoundException`
	 *
	 * @param name The name of the habit to fetch
	 *
	 * @return The HabitCatalog object
	 */
	public HabitsCatalog getHabit(String name) throws DataAccessException;

	/**
	 * Returns all active habits
	 *
	 * @return A list containing all active habit entries from the database
	 */
	public List<HabitsCatalog> getActiveHabits() throws DataAccessException;

	/**
	 * Returns all inactive habits
	 *
	 * @return A list containing all inactive habit entries from the database
	 */
	public List<HabitsCatalog> getInactiveHabits() throws DataAccessException;

	/**
	 * Returns the entire habit catalog from the database
	 *
	 * @return A list containing all habit entries in the database
	 */
	public List<HabitsCatalog> getHabitCatalog() throws DataAccessException;

	/**
	 * Returns the entire habit catalog from the database
	 *
	 * @param limit The upper limit of entries to return. 
	 * @param offset Where to start the selection
	 * 
	 * @return A List containing all habit entries in the database.
	 */
	public List<HabitsCatalog> getHabitCatalog(int limit, int offset) throws DataAccessException;

	//
	// ============================= DELETION METHODS ======================
	//
	
	/**
	 * Deletes the habit entry that matches the provided habit id from the database.
	 *
	 * @param habitID The habit id of the habit entry to delete
	 */
	public void deleteHabit(long habitID) throws DataAccessException;

	/**
	 * Clears the entire habit catalog database
	 */
	public void clearHabitDatabase() throws DataAccessException;

	//
	// ============================ INSERTION METHODS ==========================
	//
	
	/**
	 * Inserts a habit into the database.
	 *
	 * @param name The name of the new habit entry
	 * @param description A description of the new habit
	 * @param active Whether or not the new habit should be considered active
	 */
	public void insertHabit(String name, String description, boolean active) throws DataAccessException;

	/**
	 * Inserts a habit into the database.
	 *
	 * @param name The name of the new habit entry
	 * @param description A description of the new habit
	 */
	public void insertHabit(String name, String description) throws DataAccessException;
}

