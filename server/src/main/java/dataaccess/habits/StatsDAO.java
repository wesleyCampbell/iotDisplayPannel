package dataaccess.habits;

import dataaccess.SQLDatabaseDAO;
import dataaccess.exception.*;

public class StatsDAO extends SQLDatabaseDAO {
	//
	// ================= DATABASE STATEMENTS ==================
	//
	
	private static final String DB_NAME = "habits__stats";

	private static final String DB_INIT_STATEMENT = String.format("""
			CREATE TABLE IF NOT EXISTS %s (
				`habit_id` BIGINT UNSIGNED NOT NULL PRIMARY KEY,
				`current_streak` INTEGER NOT NULL DEFAULT 0,
				`longest_streak` INTEGER NOT NULL DEFAULT 0,
				`last_completed_date` DATE,

				`goal_type` VARCHAR(50),
				`goal_target` INTEGER NOT NULL,
				
				`streak_goal` INTEGER DEFAULT 0,

				`updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

				CONSTRAINT fk_habbit_stats
					FOREIGN KEY (`habit_id`)
					REFERENCES habits__catalog(`id`)
					ON DELETE CASCADE
				) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci""",
			DB_NAME);
	
	//
	// ======================= CONSTRUCTORS ==========================
	//
	
	public StatsDAO() throws DataAccessException {
		super(DB_INIT_STATEMENT);
	}
}
