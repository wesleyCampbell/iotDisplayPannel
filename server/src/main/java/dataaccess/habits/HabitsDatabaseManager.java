package dataaccess.habits;

import dataaccess.DatabaseManager;
import dataaccess.exception.*;

public class HabitsDatabaseManager extends DatabaseManager {
	private static final String DB_NAME = "habits";

	private HistoryDAO historyDAO;
	private StatsDAO statsDAO;
	private HabitsDAO habitsDAO;

public HabitsDatabaseManager() throws DataAccessException {
		super(DB_NAME);

		this.habitsDAO = new HabitsDAO(this);
		this.historyDAO = new HistoryDAO(this);
		this.statsDAO = new StatsDAO(this);
	}
}
