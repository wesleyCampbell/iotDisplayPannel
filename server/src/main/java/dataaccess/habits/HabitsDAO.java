package dataaccess.habits;

import dataaccess.SQLDatabaseDAO;
import dataaccess.exception.*;

import org.jooq.*;
import org.jooq.impl.*;
import org.jooq.Record;
import org.jooq.types.*;
import static org.jooq.impl.DSL.*;

import static jooq.habits.Tables.*;
import jooq.habits.tables.pojos.Habits_Catalog;

import java.sql.SQLException;
import java.sql.*;

import java.util.List;

public class HabitsDAO extends SQLDatabaseDAO {
	//
	// ======================== CONSTRUCTORS =========================
	//
	
	public HabitsDAO(HabitsDatabaseManager dbManager) throws DataAccessException {
		super(dbManager);

		System.out.println(">>> " + getHabitCatalog().toString());
	}

	//
	// ======================== DATA ACCESS METHODS =============================
	//

	/**
	 * Given a habitID, this method will try and fetch the associated habit_catalog object from the database. If not found, will throw a `ObjectNotFoundException`.
	 *
	 * @param habitID The habit_id of the desired habit_catalog entry.
	 *
	 * @return The Habit_Catalog object
	 */
	public Habits_Catalog getHabit(int habitID) throws DataAccessException {
		ULong id = ULong.valueOf(habitID);

		// Open the db connection
		Habits_Catalog habit = this.executeStatement(
			ctx ->
				ctx.select()
				.from(HABITS__CATALOG)
				.where(HABITS__CATALOG.HABIT_ID.eq(id))
				.fetchOneInto(Habits_Catalog.class)
		);

		// If it doesn't exist, throw an exception to notify caller.
		if (habit == null) {
			throw new ObjectNotFoundException(
			String.format("Habit with ID %d not found in database", habitID));
		}
		
		return habit;
	}

	/**
	 * Returns the entire habit catalog from the database
	 *
	 * @return A list containing all habit entries in the database
	 */
	public List<Habits_Catalog> getHabitCatalog() throws DataAccessException {
		return this.executeStatement(
			ctx -> ctx.select()
					.from(HABITS__CATALOG)
					.fetchInto(Habits_Catalog.class)
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
	public List<Habits_Catalog> getHabitCatalog(int limit, int offset) throws DataAccessException {
		List<Habits_Catalog> catalog = this.executeStatement(
			ctx -> 
				ctx.select()
				.from(HABITS__CATALOG)
				.orderBy(HABITS__CATALOG.HABIT_ID.asc())
				.limit(limit)
				.offset(offset)
				.fetchInto(Habits_Catalog.class)
		);

		return catalog;
	}	
}
