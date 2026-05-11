package dataaccess.habits;

import org.junit.jupiter.api.*;

import dataaccess.exception.DataAccessException;
import dataaccess.exception.ObjectNotFoundException;
import model.jooq.habits.tables.pojos.HabitsStats;

public class StatsDAOTests extends HabitsDAOTestParent {
	//
	// =========================== CONSTRUCTORS =====================
	//
	
	private StatsDAO statsDAO;

	public StatsDAOTests() {
		super(dbManager -> initTestTables(dbManager));
	}

	//
	// ======================= TEST INITIALIZATION ====================
	//
	
	/**
	 * Makes sure that the DAO is hooked up to the correct database for each test
	 */
	@BeforeAll
	protected void connectDAO() {
		try {	
			if (this.dbManager == null) {
				throw new DataAccessException("Parent dbManager is null");
			}
			this.statsDAO = new sqlStatsDAO(this.dbManager);
		} catch (DataAccessException ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	//
	// =============================== SELECTION TESTS =======================
	//
	
	/**
	 * Tests to make sure that the DAO can correctly return individual habit stat object
	 * coorelated with a valid habit_id
	 */
	@Test 
	public void selectOneHabitStatTest_Correct() {
		for (int i = 0; i < HABIT_NUM_TRUE; i++) {
			HabitsStats stats;
			int id = i + 1;
			stats = Assertions.assertDoesNotThrow(
				() -> this.statsDAO.getHabitStats(id)
			);

			Assertions.assertEquals(id, stats.getHabitId().intValue());
		}

		for (int i = 0; i < HABIT_NUM_FALSE; i++) {
			HabitsStats stats;
			int id = HABIT_NUM_TRUE + i + 1;
			stats = Assertions.assertDoesNotThrow(
				() -> this.statsDAO.getHabitStats(id)
			);

			Assertions.assertEquals(id, stats.getHabitId().intValue());
		}
	}

	/**
	 * Makes sure that that the DAO throws the correct error when trying to access
	 * a non-existant HabitStat entry
	 */
	@Test
	public void selectOneHabitStatTest_Incorrect() {
		int badId = HABIT_NUM_TRUE + HABIT_NUM_FALSE + 300;

		Assertions.assertThrows(ObjectNotFoundException.class,
				() -> this.statsDAO.getHabitStats(badId)
		);
	}
}

