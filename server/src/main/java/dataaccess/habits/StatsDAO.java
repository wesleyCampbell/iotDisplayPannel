package dataaccess.habits;

import dataaccess.DatabaseManager;
import dataaccess.SQLDatabaseDAO;
import dataaccess.exception.*;

import model.jooq.habits.tables.pojos.HabitsStats;
import static model.jooq.habits.Tables.*;

import org.jooq.types.*;

public class StatsDAO extends SQLDatabaseDAO {
	//
	// ======================= GLOBALS ============================
	//
	
	private static final String STATS_NOT_FOUND_TEMPLATE = "HabitStats object with HabitId=%d was not found in the database.";
	//
	// ======================= CONSTRUCTORS ==========================
	//
	
	public StatsDAO(DatabaseManager dbManager) throws DataAccessException {
		super(dbManager);
	}

	//
	// ========================= DATA SELECT METHODS ===================
	//
	
	/**
	 * Given a valid habitID, will return an object containing that habit's stats.
	 * Will throw an `ObjectNotFoundException` if the requested habit does not exist.
	 *
	 * @param habitID The habit_id of the desired habit
	 *
	 * @return HabitsStats object
	 */
	public HabitsStats getHabitStats(int habitID) throws DataAccessException {
		ULong id = ULong.valueOf(habitID);

		HabitsStats stats = this.executeStatement(
			ctx -> ctx.select()
				.from(HABITS_STATS)
				.where(HABITS_STATS.HABIT_ID.eq(id))
				.fetchOneInto(HabitsStats.class)
		);
		
		if (stats == null) {
			throw new ObjectNotFoundException(
					String.format(STATS_NOT_FOUND_TEMPLATE, habitID)
			);
		}

		return stats;
	}
}
