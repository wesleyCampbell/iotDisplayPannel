package dataaccess.habits;

import dataaccess.SQLDatabaseDAO;
import dataaccess.exception.*;

public class HistoryDAO extends SQLDatabaseDAO {
	//
	// ======================= DATABASE STATEMENTS ======================
	//
	
	private static final String DB_NAME = "habits__history";

	private static final String DB_INIT_STATEMENT = String.format("""
			CREATE TABLE IF NOT EXISTS %s (
				`id` SERIAL PRIMARY KEY,
				`habit_id` INTEGER NOT NULL,
				`completion_date DATE NOT NULL,
				`completed` BOOLEAN NOT NULL DEFAULT FALSE,
				`notes` TEXT,

				CONSTRAINT fk_habit
					FOREIGN KEY (`habit_id`)
					REFERENCES habits__catalog(`id`)
					ON DELETE RESTRICT,

				CONSTRAINT unique_habit_per_day
					UNIQUE (`habit_id`, `completion_date`)
				) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci""",
			DB_NAME);
	
	//
	// ========================= CONSTRUCTORS =======================
	//
	
	public HistoryDAO() throws DataAccessException {
		super(DB_INIT_STATEMENT);
	}
}
