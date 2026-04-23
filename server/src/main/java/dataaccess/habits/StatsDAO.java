package dataaccess.habits;

import dataaccess.SQLDatabaseDAO;
import dataaccess.exception.*;

public class StatsDAO extends SQLDatabaseDAO {
	//
	// ======================= CONSTRUCTORS ==========================
	//
	
	public StatsDAO(HabitsDatabaseManager dbManager) throws DataAccessException {
		super(dbManager);
	}
}
