package dataaccess.habits;

import dataaccess.DAOManager;
import dataaccess.exception.*;

public class HabitsDAOManager extends DAOManager {
	private HistoryDAO historyDAO;
	private StatsDAO statsDAO;
	private HabitsDAO habitsDAO;

public HabitsDAOManager() throws DataAccessException {
		super();

		this.habitsDAO = new HabitsDAO();
		this.historyDAO = new HistoryDAO();
		this.statsDAO = new StatsDAO();
	}
}
