CREATE TABLE habits_stats (
	habit_id BIGINT UNSIGNED NOT NULL PRIMARY KEY,
	
	last_failure_date DATE,
	last_completed_date DATE,
	longest_day_streak INTEGER NOT NULL DEFAULT 0,

	goal_type ENUM('Daily', 'Weekly', 'Monthly', 'Yearly'),
	goal_target INTEGER,

	longest_goal_streak INTEGER NOT NULL DEFAULT 0,
	current_goal_streak INTEGER NOT NULL DEFAULT 0,
	goal_streak_last_updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

	CONSTRAINT fk_habit_stats
		FOREIGN KEY (habit_id)
		REFERENCES habits_catalog(habit_id)
		ON DELETE CASCADE
)
ENGINE=InnoDB 
DEFAULT CHARSET=utf8mb4 
COLLATE=utf8mb4_unicode_ci;

CREATE TRIGGER after_insert__habits_catalog
AFTER INSERT ON habits_catalog
FOR EACH ROW
BEGIN
	INSERT INTO habits_stats (habit_id)
	VALUES (NEW.habit_id);
END;

