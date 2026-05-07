package dataaccess.habits;

import org.junit.jupiter.api.*;

import static model.jooq.habits.Tables.*;
import model.jooq.habits.tables.pojos.HabitsCatalog;
import model.jooq.habits.tables.pojos.HabitsHistory;

import org.jooq.*;
import org.jooq.impl.*;
import org.jooq.types.*;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

import dataaccess.DatabaseManager;
import dataaccess.exception.DataAccessException;
import dataaccess.exception.ObjectNotFoundException;

public class HistoryDAOTests extends HabitsDAOTestParent {
	//
	// ====================== GLOBALS ============================
	//
	
	private static final int HISTORY_NUM_PER_HABIT_DATE = 2;

	private static final LocalDate DATE1 = LocalDate.now();
	private static final LocalDate DATE2 = LocalDate.of(2026, 5, 3);
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
			this.historyDAO = new HistoryDAO(this.dbManager);
		} catch (DataAccessException ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}	
	}

	//
	// ========================== SELECTION TESTS ======================
	//
	
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

	@Test
	public void getHistoryEntryIdTest_Incorrect() {
		final int incorrectHistoryId = HABIT_NUM_TRUE * HISTORY_NUM_PER_HABIT_DATE + 30;

		Assertions.assertThrows(ObjectNotFoundException.class,
				() -> this.historyDAO.getHistoryEntry(incorrectHistoryId)
		);	
	}

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

	@Test
	public void getHabitsHistoryHabitTest_Incorrect() {
		final int incorrectHabitId = HABIT_NUM_TRUE + 1;

		Assertions.assertThrows(ObjectNotFoundException.class,
				() -> this.historyDAO.getHabitsHistory(incorrectHabitId)
		);
	}

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

	@Test 
	public void getCompletedHabitsHistoryTest_Incorrect() {
		final int incorrectHabitId = HABIT_NUM_TRUE + 1;

		Assertions.assertThrows(ObjectNotFoundException.class,
				() -> this.historyDAO.getCompletedHistory(incorrectHabitId)
		);
	}

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

	@Test
	public void getUncompletedHabitsHistoryTest_Incorrect() {

	}

	//
	// ========================== DELETION TESTS ======================
	//
	
	@Test
	public void deleteHistoryEntryTest_Correct() {

	}

	@Test 
	public void deleteHistoryEntryTest_Incorrect() {

	}

	@Test
	public void deleteHabitsHistoryHabitTest_Correct() {

	}

	@Test
	public void deleteHabitsHistoryHabitTest_Incorrect() {

	}

	@Test
	public void deleteHabitsHistoryDateTest_Correct() {

	}

	@Test
	public void deleteHabitsHistoryDateTest_Incorrect() {

	}

	@Test
	public void clearHabitHistoryDatabaseTest() {

	}
	
	//
	// ========================== INSERTION TESTS ======================
	//

	@Test
	public void createHistoryEntryTest_Correct() {

	}

	@Test
	public void createHistoryEntryTest_Incorrect() {

	}

	@Test 
	public void createHistoryEntryDateTest_Correct() {

	}

	@Test 
	public void createHistoryEntryDateTest_Incorrect() {

	}

	//
	// ========================== MODIFICATION TESTS ======================
	//
	
	@Test
	public void setHistoryEntryCompletedTest_Correct() {

	}
	
	@Test
	public void setHistoryEntryCompletedTest_Incorrect() {

	}

	@Test
	public void setHistoryEntryNotesTest_Correct() {

	}

	@Test
	public void setHistoryEntryNotesTest_Incorrect() {

	}
}

