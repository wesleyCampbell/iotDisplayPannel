package dataaccess.habits;

import org.junit.jupiter.api.*;

import dataaccess.DatabaseManager;
import dataaccess.exception.DataAccessException;

public class HistoryDAOTests extends HabitsDAOTestParent {
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

	}

	@Test
	public void getHistoryEntryIdTest_Incorrect() {

	}

	@Test 
	public void getHabitsHistoryHabitTest_Correct() {

	}

	@Test
	public void getHabitsHistoryHabitTest_Incorrect() {

	}

	@Test 
	public void getCompletedHabitsHistoryTest_Correct() {

	}

	@Test 
	public void getCompletedHabitsHistoryTest_Incorrect() {

	}

	@Test
	public void getHabitsHistoryDateTest_Correct() {

	}

	@Test 
	public void getHabitsHistoryDateTest_Incorrect() {

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

