package dataaccess.habits;

import java.util.List;
import java.time.LocalDate;

import model.jooq.habits.tables.pojos.*;

import dataaccess.exception.*;

public interface HistoryDAO {
	//
	// ========================= SELECTION METHODS =====================
	//
	
	/**
	 * Fetches a HabitsHistory entry based on its historyId
	 * Throws `ObjectNotFoundException` if the history entry does not exist
	 *
	 * @param historyId the HitoryId of the history entry in the database
	 *
	 * @return HabitsHistory object
	 */
	public HabitsHistory getHistoryEntry(long historyId) throws DataAccessException;


	/** Fetches all of the history entries that pertain to a specific historyID Throws `ObjectNotFoundException` if the habit_id matches no entry
	 *
	 * @param habitID The id of the habit to get the history of
	 *
	 * @param A List of habit history entries
	 */
	public List<HabitsHistory> getHabitsHistory(long habitID) throws DataAccessException;

	/** 
	 * Fetches all the completed habit history from a given habit
	 * Will throw an `ObjectNotFoundException` if passed a habit Id that does 
	 * not exist.
	 *
	 * @param habitID The id of the habit to get the history of
	 *
	 * @return A List of habit history entries
	 */
	public List<HabitsHistory> getCompletedHistory(long habitID) throws DataAccessException;


	/**
	 * Fetches all the uncompleted habit history from a given habit
	 * Will throw an `ObjectNotFoundException` if passed a habit Id that does 
	 * not exist.
	 *
	 * @param habitID The id of the habit to get the history of
	 *
	 * @param A List of habit history entries.
	 */
	public List<HabitsHistory> getUncompletedHistory(long habitID) throws DataAccessException;

	/**
	 * Fetches all of the history entry from a given date
	 *
	 * @param date The date of the desired history entires
	 *
	 * @return A List of habit history entries
	 */
	public List<HabitsHistory> getHabitsHistory(LocalDate date) throws DataAccessException;

	/**
	 * Fetches all the history entries from a given date range (inclusive)
	 *
	 * @param startDate The start date of the desired entries
	 * @param endDate The end date of the desired entries
	 *
	 * @return A List of habit history entires
	 */
	public List<HabitsHistory> getHabitsHistoryByDateRange(LocalDate startDate, LocalDate endDate) throws DataAccessException;

	//
	// ====================== DELETION METHODS =======================
	//
	
	/**
	 * Deletes a specific habitHistory entry from the database.
	 * Will throw an `ObjectNotFoundException` if the entry is not in the database
	 *
	 * @param historyID The history id of the entry to delete
	 */
	public void deleteHistoryEntry(long historyId) throws DataAccessException;

	/**
	 * Deletes all habitHistory entries pertaining to a specific habit.
	 *
	 * @param habitsID The id of the habit
	 */
	public void deleteHabitsHistory(long habitsId) throws DataAccessException;

	/**
	 * Deletes all habitHistory pertaining to a specific date.
	 *
	 * @param date The date to delete all entries from
	 */
	public void deleteHabitsHistory(LocalDate date) throws DataAccessException;

	/**
	 * Clears the entire database and resets it to its default state.
	 */
	public void clearHabitHistoryDatabase() throws DataAccessException;

	//
	// =========================== INSERTION METHODS =================
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
	public long createHistoryEntry(long habitID, boolean completed, String notes) throws DataAccessException;

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
	public long createHistoryEntry(long habitID, LocalDate completionDate, boolean completed, String notes) throws DataAccessException;

	//
	// ========================== MODIFICATION METHODS ========================
	//
	
	/**
	 * Will set the completion state of a given history entry to a given state.
	 *
	 * @param historyID The id of the history entry to modify
	 * @param state Whether to mark the entry as completed or not completed
	 */
	public void setHistoryEntryCompleteState(long historyID, boolean state) throws DataAccessException;

	/**
	 * Will set the notes of a given history entry to a provided string.
	 *
	 * @param historyID The id of the history entry to modify
	 * @param newNotes The notes to insert into the entry
	 */
	public void setHistoryEntryNotes(long historyID, String newNotes) throws DataAccessException;
}
