CREATE TABLE habits__history (
	id BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
	habit_id BIGINT UNSIGNED NOT NULL,
	completion_date DATE NOT NULL DEFAULT CURRENT_DATE,
	completed BOOLEAN NOT NULL DEFAULT FALSE,
	notes TEXT,

	CONSTRAINT fk_habit
		FOREIGN KEY (habit_id)
		REFERENCES habits__catalog(habit_id)
		ON DELETE RESTRICT,
	
	CONSTRAINT unique_habit_per_day
		UNIQUE (habit_id, completion_date)
)
ENGINE=InnoDB 
DEFAULT CHARSET=utf8mb4 
COLLATE=utf8mb4_0900_as_cs;
