package dataaccess.habits;

import dataaccess.SQLDatabaseDAO;
import dataaccess.exception.*;

import java.sql.SQLException;

public class HabitsDAO extends SQLDatabaseDAO {
	//
	// ===================== DATABASE STATEMENTS ===========================
	//
	
	private static final String TABLE_NAME = "habits__catalog";

	private static final String DB_SELECT_HABIT_STATEMENT_NAME = String.format("""
			SELECT * FROM %s WHERE name=?""", TABLE_NAME); 

	private static final String DB_SELECT_HABIT_STATEMENT_ID = String.format("""
			SELECT * FROM %s WHERE id=?""", TABLE_NAME); 
	
	private static final String DB_INSERT_HABIT_STATEMENT = String.format("""
			INSERT INTO %s (name, description, is_active) VALUES (?, ?, ?)"""
			, TABLE_NAME);

	private static final String DB_CLEAR_HABIT_TABLE_STATEMENT = String.format("""
			TRUNCATE TABLE %s""",
			TABLE_NAME);

	private static final String DB_DELETE_HABIT_STATEMENT_ID = String.format("""
			DELETE FROM %s WHERE id=?""",
			TABLE_NAME);

	private static final String DB_DELETE_HABIT_STATEMENT_NAME = String.format("""
			DELETE FROM %s WHERE name=?""",
			TABLE_NAME);

	private static final String DB_CHECK_HABIT_EXIST_STATEMENT_NAME = String.format("""
		SELECT 1 FROM %s WHERE name=?""",
		TABLE_NAME);

	private static final String DB_CHECK_HABIT_EXIST_STATEMENT_ID = String.format("""
		SELECT 1 FROM %s WHERE id=?""",
		TABLE_NAME);

	private static final String DB_SET_COL_VAL_STATEMENT= String.format("""
			UPDATE %s SET """, TABLE_NAME) + "%s=? WHERE habit_id=?";

	//
	// ======================== CONSTRUCTORS =========================
	//
	
	public HabitsDAO(HabitsDatabaseManager dbManager) throws DataAccessException {
		super(TABLE_NAME, dbManager);
	}

	//
	// ======================== DATA ACCESS METHODS =============================
	//
}
