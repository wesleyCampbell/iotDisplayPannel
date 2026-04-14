package dataaccess.habits;

import dataaccess.SQLDatabaseDAO;
import dataaccess.exception.*;

public class StatsDAO extends SQLDatabaseDAO {
	//
	// ================= DATABASE STATEMENTS ==================
	//
	
	private static final String TABLE_NAME = "habits__stats";

	private static final String DB_SELECT_STAT_STATEMENT = String.format("""
			SELECT * FROM %s WHERE habit_id=?""",
			TABLE_NAME);

	private static final String DB_CLEAR_STAT_TABLE_STATEMENT = String.format("""
			TRUNCATE TABLE %s""",
			TABLE_NAME);

	private static final String DB_CHECK_STAT_EXIST_STATEMENT = String.format("""
			SELECT 1 FROM %s WHERE habit_id=?""",
			TABLE_NAME);

	private static final String DB_SET_COL_VAL_STATEMENT= String.format("""
			UPDATE %s SET """, TABLE_NAME) + "%s=? WHERE habit_id=?";
	//
	// ======================= CONSTRUCTORS ==========================
	//
	
	public StatsDAO(HabitsDatabaseManager dbManager) throws DataAccessException {
		super(TABLE_NAME, dbManager);
	}
}
