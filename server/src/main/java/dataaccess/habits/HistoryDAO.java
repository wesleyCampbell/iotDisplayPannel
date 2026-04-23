package dataaccess.habits;

import dataaccess.SQLDatabaseDAO;
import dataaccess.exception.*;

public class HistoryDAO extends SQLDatabaseDAO {
	//
	// ========================= CONSTRUCTORS =======================
	//
	
	public HistoryDAO(HabitsDatabaseManager dbManager) throws DataAccessException {
		super(dbManager);
	}

	//
	// ============================== DATA ACCESS METHODS ========================
	//
}
