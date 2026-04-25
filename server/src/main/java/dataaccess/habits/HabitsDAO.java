package dataaccess.habits;

import dataaccess.SQLDatabaseDAO;
import dataaccess.exception.*;
import dataaccess.DatabaseManager;

import org.jooq.*;
import org.jooq.impl.*;
import org.jooq.Record;
import org.jooq.types.*;
import static org.jooq.impl.DSL.*;

import static jooq.habits.Tables.*;
import jooq.habits.tables.pojos.HabitsCatalog;

import java.sql.SQLException;
import java.sql.*;

import java.util.List;

public class HabitsDAO extends SQLDatabaseDAO {
	//
	// ======================== GLOBALS =============================
	//
		
	private static final String OBJ_NOT_FOUND_ERROR_TEMPLATE = "Object with Habit_ID %d not found in the database";

	//
	// ======================== CONSTRUCTORS =========================
	//
	
	public HabitsDAO(DatabaseManager dbManager) throws DataAccessException {
		super(dbManager);
	}

	//
	// ======================== DATA SELECT METHODS ==========================
	//

	/**
	 * Given a habitID, this method will try and fetch the associated habit_catalog object from the database. If not found, will throw a `ObjectNotFoundException`.
	 *
	 * @param habitID The habit_id of the desired habit_catalog entry.
	 *
	 * @return The Habit_Catalog object
	 */
	public HabitsCatalog getHabit(int habitID) throws DataAccessException {
		ULong id = ULong.valueOf(habitID);

		// Open the db connection
		HabitsCatalog habit = this.executeStatement(
			ctx ->
				ctx.select()
				.from(HABITS_CATALOG)
				.where(HABITS_CATALOG.HABIT_ID.eq(id))
				.fetchOneInto(HabitsCatalog.class)
		);

		// If it doesn't exist, throw an exception to notify caller.
		if (habit == null) {
			throw new ObjectNotFoundException(String.format(OBJ_NOT_FOUND_ERROR_TEMPLATE, habitID));
		}
		
		return habit;
	}

	/**
	 * Returns all active habits
	 *
	 * @return A list containing all active habit entries from the database
	 */
	public List<HabitsCatalog> getActiveHabits() throws DataAccessException {
		return this.executeStatement(
			ctx -> ctx.select()
					.from(HABITS_CATALOG)
					.where(HABITS_CATALOG.ACTIVE.eq(true))
					.fetchInto(HabitsCatalog.class)
		);
	}

	/**
	 * Returns all inactive habits
	 *
	 * @return A list containing all inactive habit entries from the database
	 */
	public List<HabitsCatalog> getInactiveHabits() throws DataAccessException {
		return this.executeStatement(
			ctx -> ctx.select()
					.from(HABITS_CATALOG)
					.where(HABITS_CATALOG.ACTIVE.eq(false))
					.fetchInto(HabitsCatalog.class)
		);
	}

	/**
	 * Returns the entire habit catalog from the database
	 *
	 * @return A list containing all habit entries in the database
	 */
	public List<HabitsCatalog> getHabitCatalog() throws DataAccessException {
		return this.executeStatement(
			ctx -> ctx.select()
					.from(HABITS_CATALOG)
					.fetchInto(HabitsCatalog.class)
		);
	}

	/**
	 * Returns the entire habit catalog from the database
	 *
	 * @param limit The upper limit of entries to return. 
	 * @param offset Where to start the selection
	 * 
	 * @return A List containing all habit entries in the database.
	 */
	public List<HabitsCatalog> getHabitCatalog(int limit, int offset) throws DataAccessException {
		List<HabitsCatalog> catalog = this.executeStatement(
			ctx -> 
				ctx.select()
				.from(HABITS_CATALOG)
				.orderBy(HABITS_CATALOG.HABIT_ID.asc())
				.limit(limit)
				.offset(offset)
				.fetchInto(HabitsCatalog.class)
		);

		return catalog;
	}	
	
	//
	// ======================== DATA DELETE METHODS ==========================
	//

	/**
	 * Deletes the habit entry that matches the provided habit id from the database.
	 *
	 * @param habitID The habit id of the habit entry to delete
	 */
	public void deleteHabit(int habitID) throws DataAccessException {
		ULong id = ULong.valueOf(habitID);

		int rows_deleted = this.executeStatement(
								ctx -> ctx.deleteFrom(HABITS_CATALOG)
										.where(HABITS_CATALOG.HABIT_ID.eq(id))
										.execute()
		);
		if (rows_deleted == 0) {
			throw new ObjectNotFoundException(String.format(OBJ_NOT_FOUND_ERROR_TEMPLATE, habitID));
		}
	}

	/**
	 * Clears the entire habit catalog database
	 */
	public void clearHabitDatabase() throws DataAccessException {
		this.executeStatement(
			ctx -> ctx.truncate(HABITS_CATALOG).execute()	
		);
	}
	
	//
	// ======================== DATA INSERT METHODS ==========================
	//
	
	/**
	 * Inserts a habit into the database.
	 *
	 * @param name The name of the new habit entry
	 * @param description A description of the new habit
	 * @param active Whether or not the new habit should be considered active
	 */
	public void insertHabit(String name, String description, boolean active) throws DataAccessException {
		int rows_affected = this.executeStatement(
			ctx -> ctx.insertInto(HABITS_CATALOG, HABITS_CATALOG.NAME, HABITS_CATALOG.DESCRIPTION, HABITS_CATALOG.ACTIVE)
			.values(name, description, active)
			.execute()
		);

		if (rows_affected == 0) {
			throw new DataAccessException("Insertion failed");
		}
	}

	/**
	 * Inserts a habit into the database.
	 *
	 * @param name The name of the new habit entry
	 * @param description A description of the new habit
	 */
	public void insertHabit(String name, String description) throws DataAccessException {
		this.insertHabit(name, description, true);
	}
}
