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

		this.habitsDAO = new sqlHabitsDAO(this);
		this.historyDAO = new sqlHistoryDAO(this);
		this.statsDAO = new sqlStatsDAO(this);
	}

	public HabitsDAO getHabitsDAO() {
		return this.habitsDAO;
	}

	public HistoryDAO getHistoryDAO() {
		return this.historyDAO;
	}

	public StatsDAO getStatsDAO() {
		return this.statsDAO;
	}
}
