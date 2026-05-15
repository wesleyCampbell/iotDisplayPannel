package dataaccess.habits;

import java.util.List;
import java.time.LocalDate;

import org.jooq.impl.DSL;
import org.jooq.types.ULong;

import dataaccess.SQLDatabaseDAO;
import dataaccess.DatabaseManager;
import dataaccess.exception.*;

import model.jooq.habits.tables.pojos.HabitsHistory;
import static model.jooq.habits.Tables.*;

public class sqlHistoryDAO extends SQLDatabaseDAO implements HistoryDAO {
	//
	// ========================= GLOBALS ======================
	//
	
	private static final String OBJ_NOT_FOUND_TEMPLATE = "Object with History_ID=%d not found in the database";
	private static final String HABIT_ID_NOT_FOUND_TEMPLATE = "Habit ID %d is not found in the database.";

	//
	// ========================= CONSTRUCTORS =======================
	//
	
	public sqlHistoryDAO(DatabaseManager dbManager) throws DataAccessException {
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
	public HabitsHistory getHistoryEntry(long historyId) throws DataAccessException {
		ULong id = ULong.valueOf(historyId);

		// make the query
		HabitsHistory history = this.executeStatement(
				ctx -> ctx.select()
				.from(HABITS_HISTORY)
				.where(HABITS_HISTORY.HISTORY_ID.eq(id))
				.fetchOneInto(HabitsHistory.class)
		);

		// Make sure it isn't null
		if (history == null) {
			throw new ObjectNotFoundException(String.format(
					OBJ_NOT_FOUND_TEMPLATE, historyId));
		}

		return history;
	}

	/** Fetches all of the history entries that pertain to a specific historyID Throws `ObjectNotFoundException` if the habit_id matches no entry
	 *
	 * @param habitID The id of the habit to get the history of
	 *
	 * @param A List of habit history entries
	 */
	public List<HabitsHistory> getHabitsHistory(long habitID) throws DataAccessException {
		ULong id = ULong.valueOf(habitID);

		List<HabitsHistory> histories = this.executeStatement(
			ctx -> ctx.select()
				.from(HABITS_HISTORY)
				.where(HABITS_HISTORY.HABIT_ID.eq(id))
				.fetchInto(HabitsHistory.class)
		);

		if (histories.isEmpty()) {
			throw new ObjectNotFoundException(String.format(
				HABIT_ID_NOT_FOUND_TEMPLATE, habitID)
			);
		}

		return histories;
	}

	/**
	 * Helper function that returns all habit history from a given habit
	 * where the completion state matches the passed boolean.
	 * Will throw an `ObjectNotFoundException` if passed a habit id 
	 * that does not exist
	 *
	 * @param habitID The id of the habit to get the history of
	 * @param state The desired completion state
	 *
	 * @return A List of habit history entries with the given completion state
	 */
	private List<HabitsHistory> getHabitsHistoryCompletionStatus(long habitID, boolean state) throws DataAccessException {
		ULong id = ULong.valueOf(habitID);

		// Confirm that the habit exists in the database
		if (!this.entryExists(HABITS_HISTORY, HABITS_HISTORY.HABIT_ID, id)) {
			throw new ObjectNotFoundException(String.format(
				HABIT_ID_NOT_FOUND_TEMPLATE, habitID)
			);
		}
		
		// Return the completed history entries
		return this.executeStatement(
			ctx -> ctx.select()
				.from(HABITS_HISTORY)
				.where(HABITS_HISTORY.HABIT_ID.eq(id)
					.and(HABITS_HISTORY.COMPLETED.eq(state))
				)
				.fetchInto(HabitsHistory.class)
		);
	}

	/** 
	 * Fetches all the completed habit history from a given habit
	 * Will throw an `ObjectNotFoundException` if passed a habit Id that does 
	 * not exist.
	 *
	 * @param habitID The id of the habit to get the history of
	 *
	 * @return A List of habit history entries
	 */
	public List<HabitsHistory> getCompletedHistory(long habitID) throws DataAccessException {
		return this.getHabitsHistoryCompletionStatus(habitID, true);
	}

	/**
	 * Fetches all the uncompleted habit history from a given habit
	 * Will throw an `ObjectNotFoundException` if passed a habit Id that does 
	 * not exist.
	 *
	 * @param habitID The id of the habit to get the history of
	 *
	 * @param A List of habit history entries.
	 */
	public List<HabitsHistory> getUncompletedHistory(long habitID) throws DataAccessException {
		return this.getHabitsHistoryCompletionStatus(habitID, false);
	}

	/**
	 * Fetches all of the history entry from a given date
	 *
	 * @param date The date of the desired history entires
	 *
	 * @return A List of habit history entries
	 */
	public List<HabitsHistory> getHabitsHistory(LocalDate date) throws DataAccessException {
		return this.executeStatement(
			ctx -> ctx.select()
				.from(HABITS_HISTORY)
				.where(HABITS_HISTORY.COMPLETION_DATE.eq(date))
				.fetchInto(HabitsHistory.class)
		);	
	}

	/**
	 * Fetches all the history entries from a given date range (inclusive)
	 *
	 * @param startDate The start date of the desired entries
	 * @param endDate The end date of the desired entries
	 *
	 * @return A List of habit history entires
	 */
	public List<HabitsHistory> getHabitsHistoryByDateRange(LocalDate startDate, LocalDate endDate) throws DataAccessException {
		return this.executeStatement(
			ctx -> ctx.select()
				.from(HABITS_HISTORY)
				.where(HABITS_HISTORY.COMPLETION_DATE.between(
					startDate,
					endDate))
				.fetchInto(HabitsHistory.class)
		);
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
	public void deleteHistoryEntry(long historyId) throws DataAccessException {
		ULong id = ULong.valueOf(historyId);

		int rows_deleted = this.executeStatement(
				ctx -> ctx.deleteFrom(HABITS_HISTORY)
					.where(HABITS_HISTORY.HISTORY_ID.eq(id))
					.execute()
		);

		if (rows_deleted == 0) {
			throw new ObjectNotFoundException(String.format(
				OBJ_NOT_FOUND_TEMPLATE, historyId)
			);
		}
	}

	/**
	 * Deletes all habitHistory entries pertaining to a specific habit.
	 *
	 * @param habitsID The id of the habit
	 */
	public void deleteHabitsHistory(long habitsId) throws DataAccessException {
		ULong id = ULong.valueOf(habitsId);
		
		// Assert that there are entries to delete
		if (!this.entryExists(HABITS_HISTORY, HABITS_HISTORY.HABIT_ID, id)) {
			throw new ObjectNotFoundException(String.format(
				HABIT_ID_NOT_FOUND_TEMPLATE, habitsId)
			);
		}

		// Perform the deletion
		this.executeStatement(
			ctx -> ctx.deleteFrom(HABITS_HISTORY)
				.where(HABITS_HISTORY.HABIT_ID.eq(id))
				.execute()
		);
	}

	/**
	 * Deletes all habitHistory pertaining to a specific date.
	 *
	 * @param date The date to delete all entries from
	 */
	public void deleteHabitsHistory(LocalDate date) throws DataAccessException {
		this.executeStatement(
			ctx -> ctx.deleteFrom(HABITS_HISTORY)
				.where(HABITS_HISTORY.COMPLETION_DATE.eq(date))
				.execute()
		);
	}

	/**
	 * Clears the entire database and resets it to its default state.
	 */
	public void clearHabitHistoryDatabase() throws DataAccessException {
		this.executeStatement(
			ctx -> ctx.deleteFrom(HABITS_HISTORY).execute()
		);
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
	public long createHistoryEntry(long habitID, boolean completed, String notes) throws DataAccessException {
		ULong id = ULong.valueOf(habitID);

		ULong generatedKey = this.executeStatement(
			ctx -> ctx.insertInto(HABITS_HISTORY)
				.set(HABITS_HISTORY.HABIT_ID, id)
				.set(HABITS_HISTORY.COMPLETED, completed)
				.set(HABITS_HISTORY.NOTES, notes)
				.returning(HABITS_HISTORY.HISTORY_ID)
				.fetchOne(HABITS_HISTORY.HISTORY_ID)
		);

		if (generatedKey == null) {
			throw new DataAccessException("Insertion failed");
		}

		return generatedKey.longValue();
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
	public long createHistoryEntry(long habitID, LocalDate completionDate, boolean completed, String notes) throws DataAccessException {
		ULong id = ULong.valueOf(habitID);

		ULong generatedKey = this.executeStatement(
			ctx -> ctx.insertInto(HABITS_HISTORY)
				.set(HABITS_HISTORY.HABIT_ID, id)
				.set(HABITS_HISTORY.COMPLETION_DATE, completionDate)
				.set(HABITS_HISTORY.COMPLETED, completed)
				.set(HABITS_HISTORY.NOTES, notes)
				.returning(HABITS_HISTORY.HISTORY_ID)
				.fetchOne(HABITS_HISTORY.HISTORY_ID)
		);

		return generatedKey.longValue();
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
	public void setHistoryEntryCompleteState(long historyID, boolean state) throws DataAccessException {
		ULong id = ULong.valueOf(historyID);

		int rows_modified = this.executeStatement(
			ctx -> ctx.update(HABITS_HISTORY)
				.set(HABITS_HISTORY.COMPLETED, state)
				.where(HABITS_HISTORY.HISTORY_ID.eq(id))
				.execute()
		);	

		// if the mutation failed, throw a fit
		if (rows_modified == 0) {
			throw new ObjectNotFoundException(String.format(
				OBJ_NOT_FOUND_TEMPLATE, historyID)
			);
		}
	}

	/**
	 * Will set the notes of a given history entry to a provided string.
	 *
	 * @param historyID The id of the history entry to modify
	 * @param newNotes The notes to insert into the entry
	 */
	public void setHistoryEntryNotes(long historyID, String newNotes) throws DataAccessException {
		ULong id = ULong.valueOf(historyID);

		int rows_modified = this.executeStatement(
			ctx -> ctx.update(HABITS_HISTORY)
				.set(HABITS_HISTORY.NOTES, newNotes)
				.where(HABITS_HISTORY.HISTORY_ID.eq(id))
				.execute()
		);	

		// If the mutation failed, throw a fit
		if (rows_modified == 0) {
			throw new ObjectNotFoundException(String.format(
				OBJ_NOT_FOUND_TEMPLATE, historyID)
			);	
		}
	}
}
