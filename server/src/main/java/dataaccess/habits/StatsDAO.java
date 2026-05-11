package dataaccess.habits;

import dataaccess.exception.*;

import model.jooq.habits.tables.pojos.*;

public interface StatsDAO {
	//
	// ======================= SELECTION METHODS =====================
	//
	
	/**
	 * Given a valid habitID, will return an object containing that habit's stats.
	 * Will throw an `ObjectNotFoundException` if the requested habit does not exist.
	 *
	 * @param habitID The habit_id of the desired habit
	 *
	 * @return HabitsStats object
	 */
	public HabitsStats getHabitStats(long habitID) throws DataAccessException;
}
