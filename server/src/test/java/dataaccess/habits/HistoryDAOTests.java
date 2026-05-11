package dataaccess.habits;

import org.junit.jupiter.api.*;

import static model.jooq.habits.Tables.*;
import model.jooq.habits.tables.pojos.HabitsCatalog;
import model.jooq.habits.tables.pojos.HabitsHistory;
import model.jooq.habits.tables.records.HabitsHistoryRecord;

import org.jooq.*;
import org.jooq.impl.*;
import org.jooq.types.*;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

import dataaccess.DatabaseManager;
import dataaccess.exception.*;

public class HistoryDAOTests extends HabitsDAOTestParent {
	//
	// ====================== GLOBALS ============================
	//
	
	private static final int HISTORY_NUM_PER_HABIT_DATE = 2;

	private static final LocalDate DATE1 = LocalDate.of(2000, 3, 5);
	private static final LocalDate DATE2 = LocalDate.of(2021, 5, 3);
	private static final LocalDate INVALID_DATE = LocalDate.of(2021, 4, 2);

	//
	// ====================== CONSTRUCTORS ===========================
	//
	private HistoryDAO historyDAO;
	
	public HistoryDAOTests() {
		super(dbManager -> initHistoryTestTables(dbManager));
	}

	// 
	// ========================= TEST INITIALIZATION =====================
	//
	
	/**
	 * A function that will initialize a database for each unit test so that
	 * they all start from the same initial state.
	 *
	 * @param dbManager the DatabaseManager hooked up to the database
	 */
	private static void initHistoryTestTables(DatabaseManager dbManager) {
		initTestTables(dbManager);

		// add in a few base history entries
		try (Connection conn = dbManager.getConn()) {
			DSLContext ctx = DSL.using(conn, SQLDialect.MARIADB);

			for (int i = 0; i < HABIT_NUM_TRUE; i++) {
				ULong habitID = ULong.valueOf(i + 1);
				boolean isCompleted = i % 2 == 0 ? true : false;

				ctx.insertInto(HABITS_HISTORY,
						HABITS_HISTORY.HABIT_ID,
						HABITS_HISTORY.COMPLETION_DATE,
						HABITS_HISTORY.COMPLETED)
					.values(habitID, DATE1, isCompleted)
					.execute();

				ctx.insertInto(HABITS_HISTORY,
						HABITS_HISTORY.HABIT_ID,
						HABITS_HISTORY.COMPLETION_DATE,
						HABITS_HISTORY.COMPLETED)
					.values(habitID, DATE2, !isCompleted)
					.execute();
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	/**
	 * Makes sure that the DAO is hooked up to the correct database for each test
	 */
	@BeforeAll
	protected void connectDAO() {
		try {
			if (this.dbManager == null) {
				throw new DataAccessException("Parent dbManager is null.");
			}
			this.historyDAO = new sqlHistoryDAO(this.dbManager);
		} catch (DataAccessException ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}	
	}

	//
	// ========================== SELECTION TESTS ======================
	//
	
	/**
	 * Verifies that getHistoryEntry() returns the correct history record
	 * for a valid entry ID and that the returned record is associated
	 * with the expected habit.
	 */
	@Test 
	public void getHistoryEntryIdTest_Correct() {
		int historyID = 1;

		for (int i = 0; i < HABIT_NUM_TRUE; i++) {
			for (int j = 0; j < HISTORY_NUM_PER_HABIT_DATE; j++) {
				final int id = historyID++;  // increment the id tracker
				HabitsHistory history = Assertions.assertDoesNotThrow(
						() -> this.historyDAO.getHistoryEntry(Integer.valueOf(id))
				);

				// Assert that it contains the correct habit id.
				Assertions.assertEquals(ULong.valueOf(i + 1), history.getHabitId());
			}
		}
	}

	/**
	 * Verifies that getHistoryEntry() throws an ObjectNotFoundException
	 * when the requested history entry ID does not exist.
	 */
	@Test
	public void getHistoryEntryIdTest_Incorrect() {
		final int incorrectHistoryId = HABIT_NUM_TRUE * HISTORY_NUM_PER_HABIT_DATE + 30;

		Assertions.assertThrows(ObjectNotFoundException.class,
				() -> this.historyDAO.getHistoryEntry(incorrectHistoryId)
		);	
	}

	/**
	 * Verifies that getHabitsHistory() correctly retrieves all history entries
	 * associated with a given valid habit ID and that the number of returned
	 * entries matches the expected count.
	 */
	@Test 
	public void getHabitsHistoryHabitTest_Correct() {
		for (int i = 0; i < HABIT_NUM_TRUE; i++) {
			final int habitID = i + 1;

			List<HabitsHistory> habits = Assertions.assertDoesNotThrow(
				() -> this.historyDAO.getHabitsHistory(habitID)
			);

			Assertions.assertTrue(habits.size() == HISTORY_NUM_PER_HABIT_DATE);
		}
	}

	/**
	 * Verifies that getHabitsHistory() throws an ObjectNotFoundException
	 * when attempting to retrieve history entries for a habit ID that does not exist.
	 */
	@Test
	public void getHabitsHistoryHabitTest_Incorrect() {
		final int incorrectHabitId = HABIT_NUM_TRUE + 1;

		Assertions.assertThrows(ObjectNotFoundException.class,
				() -> this.historyDAO.getHabitsHistory(incorrectHabitId)
		);
	}

	/**
	 * Verifies that getHabitsHistory() correctly retrieves all history entries
	 * for valid dates, returning the expected number of entries per date, and
	 * returns an empty list when no entries exist for a given invalid date.
	 */
	@Test
	public void getHabitsHistoryDateTest_Correct() {
		// Test that all DATE1 dates were collected
		final int expectedHabitNum = HABIT_NUM_TRUE;

		List<HabitsHistory> habitsDate1 = Assertions.assertDoesNotThrow(
			() -> this.historyDAO.getHabitsHistory(DATE1)
		);

		Assertions.assertEquals(expectedHabitNum, habitsDate1.size());

		// Test that all DATE2 dates were collected
		List<HabitsHistory> habitsDate2 = Assertions.assertDoesNotThrow(
			() -> this.historyDAO.getHabitsHistory(DATE2)
		);

		Assertions.assertEquals(expectedHabitNum, habitsDate2.size());

		// Test that null is returned for random date
		List<HabitsHistory> habitsDateInv = Assertions.assertDoesNotThrow(
			() -> this.historyDAO.getHabitsHistory(INVALID_DATE)
		);

		Assertions.assertTrue(habitsDateInv.isEmpty());
	}

	/**
	 * Verifies that getCompletedHistory() correctly retrieves all completed
	 * habit history entries across all valid habit IDs and that the total
	 * number of completed entries matches the expected count.
	 */
	@Test 
	public void getCompletedHabitsHistoryTest_Correct() {
		int completedNum = 0;

		for (int i = 0; i < HABIT_NUM_TRUE; i++) {
			final int habitID = i + 1;
			List<HabitsHistory> completedHabits = Assertions.assertDoesNotThrow(
				() -> this.historyDAO.getCompletedHistory(habitID)
			);

			completedNum += completedHabits.size();
		}

		Assertions.assertEquals(HABIT_NUM_TRUE, completedNum);
	}

	/**
	 * Verifies that getCompletedHistory() throws an ObjectNotFoundException
	 * when attempting to retrieve completed history for a non-existent habit ID.
	 */
	@Test 
	public void getCompletedHabitsHistoryTest_Incorrect() {
		final int incorrectHabitId = HABIT_NUM_TRUE + 1;

		Assertions.assertThrows(ObjectNotFoundException.class,
				() -> this.historyDAO.getCompletedHistory(incorrectHabitId)
		);
	}

	/**
	 * Verifies that getUncompletedHistory() correctly retrieves all uncompleted
	 * habit history entries across all valid habit IDs and that the total number
	 * of returned entries matches the expected count.
	 */
	@Test
	public void getUncompletedHabitsHistoryTest_Correct() {
		int uncompletedNum = 0;

		for (int i = 0; i < HABIT_NUM_TRUE; i++) {
			final int habitID = i + 1;
			List<HabitsHistory> uncompletedHabits = Assertions.assertDoesNotThrow(
				() -> this.historyDAO.getUncompletedHistory(habitID)
			);

			uncompletedNum += uncompletedHabits.size();
		}

		Assertions.assertEquals(HABIT_NUM_TRUE, uncompletedNum);
	}

	/**
	 * Verifies that getUncompletedHistory() throws an ObjectNotFoundException
	 * when attempting to retrieve uncompleted history for a non-existent habit ID.
	 */
	@Test
	public void getUncompletedHabitsHistoryTest_Incorrect() {
		final int incorrectHabitId = HABIT_NUM_TRUE + 1;

		Assertions.assertThrows(
			ObjectNotFoundException.class,
			() -> this.historyDAO.getUncompletedHistory(incorrectHabitId)
		);
	}

	//
	// ========================== DELETION TESTS ======================
	//
	
	/**
	 * Verifies that deleteHistoryEntry() successfully removes each existing
	 * history entry from the database, that the entry no longer exists after
	 * deletion, and that the total table size decreases accordingly until empty.
	 */
	@Test
	public void deleteHistoryEntryTest_Correct() {
		for (int i = 0; i < HABIT_NUM_TRUE * HISTORY_NUM_PER_HABIT_DATE; i++) {
			int id = i + 1;

			final int currentHistoryNum = this.getTableLength(HABITS_HISTORY);

			// Assert that an entry exists
			Assertions.assertTrue(this.entryExists(
					HABITS_HISTORY, HABITS_HISTORY.HISTORY_ID, ULong.valueOf(id))
			);

			Assertions.assertDoesNotThrow(
				() -> this.historyDAO.deleteHistoryEntry(id)
			);

			// Assert that the entry no longer exists in the database.
			Assertions.assertFalse(this.entryExists(
					HABITS_HISTORY, HABITS_HISTORY.HISTORY_ID, ULong.valueOf(id))
			);

			final int finalHistoryNum = this.getTableLength(HABITS_HISTORY);

			Assertions.assertEquals(currentHistoryNum - 1, finalHistoryNum);
		}

		Assertions.assertEquals(0, this.getTableLength(HABITS_HISTORY));
	}

	/**
	 * Verifies that deleteHistoryEntry() throws an ObjectNotFoundException
	 * when attempting to delete a non-existent history entry and ensures that
	 * the database state remains unchanged after the failed deletion attempt.
	 */
	@Test 
	public void deleteHistoryEntryTest_Incorrect() {
		int incorrectHistoryId = HABIT_NUM_TRUE * HISTORY_NUM_PER_HABIT_DATE + 1;

		final int initialHistoryNum = this.getTableLength(HABITS_HISTORY);

		Assertions.assertThrows(
			ObjectNotFoundException.class,
			() -> this.historyDAO.deleteHistoryEntry(incorrectHistoryId)
		);

		final int finalHistoryNum = this.getTableLength(HABITS_HISTORY);

		Assertions.assertEquals(initialHistoryNum, finalHistoryNum);
	}

	/**
	 * Verifies that deleteHabitsHistory() successfully removes all history
	 * entries associated with each valid habit ID and that no entries remain
	 * for those habits after deletion.
	 */
	@Test
	public void deleteHabitsHistoryHabitTest_Correct() {
		for (int i = 0; i < HABIT_NUM_TRUE; i++) {
			final int id = i + 1;
			
			Assertions.assertDoesNotThrow(
				() -> this.historyDAO.deleteHabitsHistory(id)
			);

			int finalHistoryNum = this.getEntryNum(
				HABITS_HISTORY, HABITS_HISTORY.HABIT_ID, ULong.valueOf(id)
			);

			Assertions.assertEquals(0, finalHistoryNum);
		}
	}

	/**
	 * Verifies that deleteHabitsHistory() throws an ObjectNotFoundException
	 * when attempting to delete history entries for a non-existent habit ID
	 * and ensures that the database state remains unchanged.
	 */
	@Test
	public void deleteHabitsHistoryHabitTest_Incorrect() {
		final int invalidHabitId = HABIT_NUM_TRUE + 1;
		int initialHistoryNum = this.getTableLength(HABITS_HISTORY);

		Assertions.assertThrows(
			ObjectNotFoundException.class,
			() -> this.historyDAO.deleteHabitsHistory(invalidHabitId)
		);

		int finalHistoryNum = this.getTableLength(HABITS_HISTORY);

		Assertions.assertEquals(initialHistoryNum, finalHistoryNum);
	}

	/**
	 * Verifies that deleteHabitsHistory() successfully deletes all history
	 * entries associated with a given completion date and ensures that no
	 * entries remain for that date after deletion.
	 */
	@Test
	public void deleteHabitsHistoryDateTest_Correct() {
		Assertions.assertDoesNotThrow(
			() -> this.historyDAO.deleteHabitsHistory(DATE1)
		);

		int finalHistoryNum = this.getEntryNum(
			HABITS_HISTORY, HABITS_HISTORY.COMPLETION_DATE, DATE1
		);

		Assertions.assertEquals(0, finalHistoryNum);
	}

	/**
	 * Verifies that deleteHabitsHistory() performs no deletion and does not
	 * modify the database when provided with an invalid or non-matching date.
	 */
	@Test
	public void deleteHabitsHistoryDateTest_Incorrect() {
		final int initialHistoryNum = this.getTableLength(HABITS_HISTORY);

		Assertions.assertDoesNotThrow(
			() -> this.historyDAO.deleteHabitsHistory(INVALID_DATE)
		);

		final int finalHistoryNum = this.getTableLength(HABITS_HISTORY);

		// Deletions with invalid dates should not delete any entries
		Assertions.assertEquals(initialHistoryNum, finalHistoryNum);
	}

	/**
	 * Verifies that clearHabitHistoryDatabase() successfully removes all
	 * entries from the habit history table and leaves the table empty after execution.
	 */
	@Test
	public void clearHabitHistoryDatabaseTest() {
		Assertions.assertDoesNotThrow(
			() -> this.historyDAO.clearHabitHistoryDatabase()
		);

		Assertions.assertEquals(0, this.getTableLength(HABITS_HISTORY));
	}
	
	//
	// ========================== INSERTION TESTS ======================
	//
	
	/**
	 * Helper function for insertion tests. Will call HistoryDAO.createHistoryEntry()
	 * using the given paramaters. If no date is passed in, the createHistoryEntry()
	 * with no date in the function signature will be called.
	 *
	 * Will insert, confirm creation in database, and will confirm data correctness in
	 * the database.
	 *
	 * @param habitID The id of the habit to assign to the history entry. Must be valid.
	 * @param completed Whether or not the habbit should be marked as completed.
	 * @param notes Any notes to attach to the history entry
	 * @param date The date to assign to the history entry, if not null
	 */
	private void createHistoryEntryTestHelper(int habitID, boolean completed, String notes, LocalDate date) {
		final int initialHistoryNum = this.getTableLength(HABITS_HISTORY);

		long newKey;
		if (date == null) {
			newKey = Assertions.assertDoesNotThrow(
				() -> this.historyDAO.createHistoryEntry(
					habitID, completed, notes	
				)
			);
		} else {
			newKey = Assertions.assertDoesNotThrow(
				() -> this.historyDAO.createHistoryEntry(
					habitID, date, completed, notes
				)
			);
		}

		final int finalHistoryNum = this.getTableLength(HABITS_HISTORY);

		Assertions.assertEquals(initialHistoryNum + 1, finalHistoryNum);
	
		// Assert that the new entry does in fact exist. 
		boolean exists = this.entryExists(
				HABITS_HISTORY,
				HABITS_HISTORY.HISTORY_ID,
				ULong.valueOf(newKey));
		Assertions.assertTrue(exists);

		// Assert that all the info is correct
		HabitsHistory history;
		try (Connection conn = dbManager.getConn()) {
			DSLContext ctx = DSL.using(conn, SQLDialect.MARIADB);

			history = ctx.selectFrom(HABITS_HISTORY)
				.where(HABITS_HISTORY.HISTORY_ID.eq(ULong.valueOf(newKey)))
				.fetchOneInto(HabitsHistory.class);

		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

		Assertions.assertEquals(completed, history.getCompleted());
		Assertions.assertEquals(notes, history.getNotes());
		Assertions.assertEquals(ULong.valueOf(habitID), history.getHabitId());
		if (date != null) {
			Assertions.assertEquals(date, history.getCompletionDate());
		}
	}

	/**
	 * Tests correct data insertion via createHistoryEntry.
	 * Will confirm insertion and validity of data after insertion
	 */
	@Test
	public void createHistoryEntryTest_Correct() {
		final String notes = "Lorem ipsum da de doo";
		
		for (int i = 1; i < HABIT_NUM_TRUE + 1; i++) {
			this.createHistoryEntryTestHelper(i, true, notes + Integer.toString(i), null);
		}
	}

	/**
	 * Confirms that createHistoryEntry throws the correct error on bad
	 * insertion call
	 */
	@Test
	public void createHistoryEntryTest_Incorrect() {
		final int invalidHabitID = HABIT_NUM_TRUE + HABIT_NUM_FALSE + 1;
		final String notes = "this shouldn't work.";

		Assertions.assertThrows(
			ForeignConstraintException.class,
			() -> this.historyDAO.createHistoryEntry(invalidHabitID, true, notes)
		);
	}

	/**
	 * Tests correct data insertion via createHistoryEntry with passed date.
	 * Will confirm insertion and validity of data after insertion.
	 */
	@Test 
	public void createHistoryEntryDateTest_Correct() {
		final String notes = "These are some notes";
		
		for (int i = 1; i < HABIT_NUM_TRUE + 1; i++) {
			// generate a unique date (ish)
			LocalDate date = LocalDate.of(2026 + i, i * 5 % 12, i * 7 % 28);
			LocalDate date2 = LocalDate.of(2026 + i + 1, i * 5 % 12, i * 7 % 28);

			this.createHistoryEntryTestHelper(i, true, notes + Integer.toString(i), date);
			this.createHistoryEntryTestHelper(i, false, notes + "f" + Integer.toString(i), date2);
		}
	}

	/**
	 * Confirms that createHistoryEntry() will throw the correct error on bad
	 * habitId pass
	 */
	@Test 
	public void createHistoryEntryDateTest_Incorrect() {
		final int invalidHabitID = HABIT_NUM_TRUE + HABIT_NUM_FALSE + 1;
		final String notes = "this shouldn't work.";
		final LocalDate date = LocalDate.now();

		Assertions.assertThrows(
			ForeignConstraintException.class,
			() -> this.historyDAO.createHistoryEntry(invalidHabitID, date, true, notes)
		);

	}

	//
	// ========================== MODIFICATION TESTS ======================
	//
	
	/**
	 * Tests that the historyDAO can correctly modify the completion state of a stored entry 
	 * within the database.
	 */
	@Test
	public void setHistoryEntryCompletedTest_Correct() {
		// get every history entry in the database
		List<HabitsHistoryRecord> histories = this.getTableEntries(HABITS_HISTORY);

		// For each History entry, swap its completion state
		for (HabitsHistoryRecord hist : histories) {
			boolean completed = hist.getCompleted();
			int id = hist.getHistoryId().intValue();

			Assertions.assertDoesNotThrow(
				() -> this.historyDAO.setHistoryEntryCompleteState(id, !completed)
			);

			// get the updated entry to confirm mutation
			HabitsHistory newHist;
			try (Connection conn = dbManager.getConn()) {
				DSLContext ctx = DSL.using(conn, SQLDialect.MARIADB);

				newHist = ctx.selectFrom(HABITS_HISTORY)
					.where(HABITS_HISTORY.HISTORY_ID.eq(ULong.valueOf(id)))
					.fetchOneInto(HabitsHistory.class);
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}

			Assertions.assertEquals(!completed, newHist.getCompleted());
		}
	}
	
	/**
	 * Verifies that setHistoryEntryCompleteState throws the correct error upon attempting
	 * to mutate a non-existent entry.
	 */
	@Test
	public void setHistoryEntryCompletedTest_Incorrect() {
		int badHistoryId = HABIT_NUM_TRUE * HISTORY_NUM_PER_HABIT_DATE + 1;

		Assertions.assertThrows(
			ObjectNotFoundException.class,
			() -> this.historyDAO.setHistoryEntryCompleteState(badHistoryId, true)
		);

		Assertions.assertThrows(
			ObjectNotFoundException.class,
			() -> this.historyDAO.setHistoryEntryCompleteState(badHistoryId, false)
		);
	}

	/**
	 * Tests that the HistoryDAO can correctly modify the note entry
	 * of an already stored entry
	 */
	@Test
	public void setHistoryEntryNotesTest_Correct() {
		String newNotesTemplate = "New Note ";

		// get every history entry in the database
		List<HabitsHistoryRecord> histories = this.getTableEntries(HABITS_HISTORY);

		// For each history entry, change its note
		for (int i = 0; i < histories.size(); i++) {
			HabitsHistoryRecord hist = histories.get(i);
			int id = hist.getHistoryId().intValue();
			String newNote = newNotesTemplate + Integer.toString(i);

			Assertions.assertDoesNotThrow(
				() ->this.historyDAO.setHistoryEntryNotes(id, newNote)
			);

			// get the updated entry to confirm mutation
			HabitsHistory newHist;
			try (Connection conn = dbManager.getConn()) {
				DSLContext ctx = DSL.using(conn, SQLDialect.MARIADB);

				newHist = ctx.selectFrom(HABITS_HISTORY)
					.where(HABITS_HISTORY.HISTORY_ID.eq(ULong.valueOf(id)))
					.fetchOneInto(HabitsHistory.class);

			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}

			Assertions.assertEquals(newNote, newHist.getNotes());
		}
	}

	/**
	 * Confirms that setHistoryEntryNotes throws the correct error
	 * when attempting to modify a non-existent entry
	 */
	@Test
	public void setHistoryEntryNotesTest_Incorrect() {
		int badHistoryId = HABIT_NUM_TRUE * HISTORY_NUM_PER_HABIT_DATE + 1;

		Assertions.assertThrows(
			ObjectNotFoundException.class,
			() -> this.historyDAO.setHistoryEntryNotes(badHistoryId, "bad bad bad")
		);
	}
}

