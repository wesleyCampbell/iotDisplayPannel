package dataaccess.habits;

import dataaccess.SQLDatabaseDAO;
import dataaccess.exception.*;

public class HistoryDAO extends SQLDatabaseDAO {
	//
	// ======================= DATABASE STATEMENTS ======================
	//
	
	private static final String TABLE_NAME = "habits__history";

	private static final String DB_SELECT_HISTORY_STATEMENT = String.format("""
			SELECT * FROM %s WHERE habit_id=?""",
			TABLE_NAME);

	private static final String DB_INSERT_HISTORY_STATEMENT = String.format("""
			INSERT INTO %s (habit_id, completed, notes) VALUES (?, ?, ?)""",
			TABLE_NAME);

	private static final String DB_CLEAR_HISTORY_TABLE_STATEMENT = String.format("""
				TRUNCATE TABLE %s""",
				TABLE_NAME);

	private static final String DB_DELETE_HISTORY_STATEMENT_DATE = String.format("""
			DELETE FROM %s WHERE completion_date=?""",
			TABLE_NAME);

	private static final String DB_DELETE_HISTORY_STATEMENT_ID = String.format("""
			DELETE FROM %s WHERE id=?""",
			TABLE_NAME);

	private static final String DB_CHECK_HISTORY_EXIST_STATEMENT = String.format("""
			SELECT 1 FROM %s WHERE habit_id=?""",
			TABLE_NAME);

	private static final String DB_SET_COL_VAL_STATEMENT = String.format("""
			UPDATE %s SET """, TABLE_NAME) + "%s=? WHERE habit_id=?";
	
	//
	// ========================= CONSTRUCTORS =======================
	//
	
	public HistoryDAO(HabitsDatabaseManager dbManager) throws DataAccessException {
		super(TABLE_NAME, dbManager);
	}

	//
	// ============================== DATA ACCESS METHODS ========================
	//
}
