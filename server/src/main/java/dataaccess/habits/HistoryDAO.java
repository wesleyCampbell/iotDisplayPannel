package dataaccess.habits;

import java.util.List;
import java.time.LocalDate;

import org.jooq.types.ULong;

import dataaccess.SQLDatabaseDAO;
import dataaccess.DatabaseManager;
import dataaccess.exception.*;

import model.jooq.habits.tables.HabitsHistory;

public class HistoryDAO extends SQLDatabaseDAO {
	//
	// ========================= CONSTRUCTORS =======================
	//
	
	public HistoryDAO(DatabaseManager dbManager) throws DataAccessException {
		super(dbManager);
	}

	//
	// ============================== DATA SELECT METHODS ========================
	//
	
	/**
	 * Fetches a HabitsHistory entry based on its historyId
	 * Throws `ObjectNotFoundException` if the history entry does not exist
	 *
	 * @param historyId the HitoryId of the history entry in the database
	 *
	 * @return HabitsHistory object
	 */
	public HabitsHistory getHistoryEntry(int historyId) throws DataAccessException {
		return null;
	}

	/**
	 * Fetches all of the history entries that pertain to a specific historyID
	 * Throws `ObjectNotFoundException` if the habit_id matches no entry
	 *
	 * @param habitID The id of the habit to get the history of
	 *
	 * @param A List of habit history entries
	 */
	public List<HabitsHistory> getHabitsHistory(int habitID) throws DataAccessException {
		return null;
	}

	/** 
	 * Fetches all the completed habit history from a given habit
	 *
	 * @param habitID The id of the habit to get the history of
	 *
	 * @param A List of habit history entries
	 */
	public List<HabitsHistory> getCompletedHistory(int habitID) throws DataAccessException {
		return null;
	}

	/**
	 * Fetches all of the history entry from a given date
	 *
	 * @param date The date of the desired history entires
	 *
	 * @return A List of habit history entries
	 */
	public List<HabitsHistory> getHabitsHistory(LocalDate date) throws DataAccessException {
		return null;
	}

	//
	// =========================== DATA DELETE METHODS =======================
	//
	
	/**
	 * Deletes a specific habitHistory entry from the database.
	 * Will throw an `ObjectNotFoundException` if the entry is not in the database
	 *
	 * @param historyID The history id of the entry to delete
	 */
	public void deleteHistoryEntry(int historyId) throws DataAccessException {

	}

	/**
	 * Deletes all habitHistory entries pertaining to a specific habit.
	 *
	 * @param habitsID The id of the habit
	 */
	public void deleteHabitsHistory(int habitsId) throws DataAccessException {

	}

	/**
	 * Deletes all habitHistory pertaining to a specific date.
	 *
	 * @param date The date to delete all entries from
	 */
	public void deleteHabitsHistory(LocalDate date) throws DataAccessException {

	}

	/**
	 * Clears the entire database and resets it to its default state.
	 */
	public void clearHabitHistoryDatabase() throws DataAccessException {

	}

	//
	// ================================ DATA INSERTION METHODS ====================
	//
	
	/**
	 * Creates a new habitHistory entry and inserts it into the database.
	 * Will throw an `ObjectNotFoundException` if the habitID provided does not match
	 * any known habit in the HabitsCatalog.
	 * Note that in this function, the database will assign the completion date to the 
	 * current date.
	 *
	 * @param habitID The id of the assigned habit for the history entry
	 * @param completed Whether or not the habit was completed or not
	 * @param notes Any notes related to the entry history
	 */
	public void createHistoryEntry(int habitID, boolean completed, String notes) throws DataAccessException {

	}

	/**
	 * Creates a new habitHistory entry and inserts it into the database.
	 * Will throw an `ObjectNotFoundException` if the habitID provided does not match
	 * any known habit in the HabitsCatalog.
	 *
	 * @param habitID The id of the assigned habit for the history entry
	 * @param completionDate The date that the entry was completed
	 * @param completed Whether or not the habit was completed or not
	 * @param notes Any notes related to the entry history
	 */
	public void createHistoryEntry(int habitID, LocalDate completionDate, boolean completed, String notes) throws DataAccessException {

	}

	//
	// ======================== DATA MODIIFCATION METHODS =======================
	//
	
	/**
	 * Will set the completion state of a given history entry to a given state.
	 *
	 * @param historyID The id of the history entry to modify
	 * @param state Whether to mark the entry as completed or not completed
	 */
	public void setHistoryEntryCompleted(int historyID, boolean state) throws DataAccessException {

	}

	/**
	 * Will set the notes of a given history entry to a provided string.
	 *
	 * @param historyID The id of the history entry to modify
	 * @param newNotes The notes to insert into the entry
	 */
	public void setHistoryEntryNotes(int historyID, String newNotes) throws DataAccessException {

	}
}
