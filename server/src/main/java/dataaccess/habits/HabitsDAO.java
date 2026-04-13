package dataaccess.habbits;

import dataaccess.SQLDatabaseDAO;
import dataaccess.exception.*;

import java.sql.SQLException;

public class HabitsDAO extends SQLDatabaseDAO {
	//
	// ===================== DATABASE STATEMENTS ===========================
	//
	
	private static final String DB_NAME = "habbits__catalog";

	private static final String DB_INIT_STATEMENT = String.format("""
			CREATE TABLE IF NOT EXISTS %s (
				`id` SERIAL PRIMARY KEY,
				`name` VARCHAR(256) NOT NULL,
				`description` TEXT,
				`created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
				`is_active` BOOLEAN DEFAULT TRUE
				) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
			""", DB_NAME);

	//
	// ======================== CONSTRUCTORS =========================
	//
	
	public HabitsDAO() throws DataAccessException {
		super(DB_INIT_STATEMENT);
	}
}
