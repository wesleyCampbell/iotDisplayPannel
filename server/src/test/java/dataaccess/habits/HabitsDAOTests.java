package dataaccess.habits;

import org.junit.jupiter.api.*;

import org.jooq.*;
import org.jooq.impl.*;
import static org.jooq.impl.DSL.*;
import static org.junit.Assert.fail;
import static model.jooq.habits.Tables.*;
import model.jooq.habits.tables.pojos.HabitsCatalog;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;

import dataaccess.DAOTests;
import dataaccess.exception.*;
import dataaccess.DatabaseManager;

public class HabitsDAOTests extends DAOTests {
	//
	// ====================== GLOBALS =====================
	//
	
	private static final int habitNumTrue = 3;
	private static final int habitNumFalse = 2;

	private static final String DB_NAME = "habits";

	//
	// ======================= CONSTRUCTORS =====================
	//
	
	private HabitsDAO habitsDAO;

	public HabitsDAOTests() {
		super(DB_NAME, (dbManager) -> initTestTables(dbManager));
	}

	//
	// ================ TEST INITIALIZATION =================
	//
	
	@BeforeEach
	protected void connectDAO() {
		try {
			if (this.dbManager == null) {
				throw new DataAccessException("parent dbManager is null.");
			}
			this.habitsDAO = new HabitsDAO(this.dbManager);
		} catch (DataAccessException ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}
	
	/**
	 * Wrapper function for `insertHabitsNum` method that uses default
	 * values. Used for the test database autoconfiguration function
	 * passed into the parent.
	 */
	private static void initTestTables(DatabaseManager dbManager) {
		insertHabitsNum(dbManager, habitNumTrue, true);
		insertHabitsNum(dbManager, habitNumFalse, false);
	}
	
	/**
	 * Helper function that inserts n number of habits into the database
	 *
	 * @param dbManager The database manager to make the connection with
	 * @param num The number of habits to insert
	 * @param active Whether the habits should be active or inactive
	 */
	private static void insertHabitsNum(DatabaseManager dbManager, int num, boolean active) {
		try (Connection conn = dbManager.getConn()) {
			DSLContext ctx = DSL.using(conn, SQLDialect.MARIADB);

			for (int i = 0; i < num; i++) {
				String name = String.format("habit%s%d",
						active ? "Active" : "Inactive", i);
				String desc = String.format("%s description here", name);

				ctx.insertInto(HABITS_CATALOG,
						HABITS_CATALOG.NAME,
						HABITS_CATALOG.DESCRIPTION,
						HABITS_CATALOG.ACTIVE)
					.values(name, desc, active)
					.execute();
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	//
	// ========================== SELECTION TESTS =============================
	//
	
	/**
	 * Tests to make sure that the DAO can correctly return individual habits based on HabitId
	 */
	@Test
	public void selectOneHabitIdTest_Correct() {
		// Make sure can access all active tests
		for (int i = 0; i < habitNumTrue; i++) {
			HabitsCatalog habit;
			int id = i + 1;
			habit = Assertions.assertDoesNotThrow(() -> this.habitsDAO.getHabit(id));		
			final String nameExpected = String.format("habitActive%d", i);
			Assertions.assertEquals(nameExpected, habit.getName());
		}

		// Make sure can access all inactive tests 
		for (int i = 0; i < habitNumFalse; i++) {
			HabitsCatalog habit;
			int id = habitNumTrue + i + 1;
			habit = Assertions.assertDoesNotThrow(() -> this.habitsDAO.getHabit(id));

			final String nameExpected = String.format("habitInactive%d", i);
			Assertions.assertEquals(nameExpected, habit.getName());
		}
	}

	/**
	 * Tests to make sure that DAO throws error when attempting to access record that does not exist by id
	 */
	@Test
	public void selectOneHabitIdTest_Incorrect() {
		int id = habitNumTrue + habitNumFalse + 1;

		Assertions.assertThrows(ObjectNotFoundException.class, () -> this.habitsDAO.getHabit(id));
	}

	/**
	 * Tests to make sure the DAO can correctly return a habit by name
	 */
	@Test
	public void selectOneHabitNameTest_Correct() {
		// Make sure can access all active tests
		for (int i = 0; i < habitNumTrue; i++) {
			HabitsCatalog habit;
			String name = String.format("habitActive%d", i);
			habit = Assertions.assertDoesNotThrow(() -> this.habitsDAO.getHabit(name));		

			int expectedId = i + 1;
			Assertions.assertEquals(expectedId, habit.getHabitId().intValue());
		}

		// Make sure can access all inactive tests 
		for (int i = 0; i < habitNumFalse; i++) {
			HabitsCatalog habit;
			String name = String.format("habitInactive%d", i);
			habit = Assertions.assertDoesNotThrow(() -> this.habitsDAO.getHabit(name));

			int expectedId = habitNumTrue + i + 1;
			Assertions.assertEquals(expectedId, habit.getHabitId().intValue());
		}
	}

	/**
	 * Tests to make sure the DAO throws the corerct error why attempting to access a habit record by an invalid name
	 */
	@Test
	public void selectOneHabitNameTest_Incorrect() {
		String[] names = {
			"This sure aint a name",
			"nor this",
			"this better not be one"
		};

		for (String name : names) {
			Assertions.assertThrows(ObjectNotFoundException.class, 
					() -> this.habitsDAO.getHabit(name));
		}
	}

	/**
	 * Tests to make sure that gethabitCatalog returns all habits in the database
	 */
	@Test
	public void selectAllHabitsTest() {
		List<HabitsCatalog> habits = Assertions.assertDoesNotThrow(
				() -> this.habitsDAO.getHabitCatalog());

		int totalNum = habitNumTrue + habitNumFalse;
		Assertions.assertEquals(totalNum, habits.size());
	}

	/**
	 * Tests to make sure that the limit on getHabitCatalog functions
	 */
	public void selectAllHabitsWithLimitTest() {
		List<HabitsCatalog> habits = Assertions.assertDoesNotThrow(
			() -> this.habitsDAO.getHabitCatalog(4, 0)
		);

		// Make sure it returned the correct size
		Assertions.assertEquals(4, habits.size());
		// Make sure it returned the correct subset
		for (int i = 1; i <= 4; i++) {
			Assertions.assertEquals(i, habits.get(i - 1).getHabitId());
		}

		habits = Assertions.assertDoesNotThrow(
			() -> this.habitsDAO.getHabitCatalog(2, 3)
		);

		// Make sure it returned the correct size
		Assertions.assertEquals(2, habits.size());
		// Make sure it returned the correct subset
		for (int i = 3; i <= 5; i++) {
			Assertions.assertEquals(i + 1, habits.get(i - 3).getHabitId());
		}
	}

	@Test
	public void selectActiveHabitsTest() {
		List<HabitsCatalog> habits = Assertions.assertDoesNotThrow(
			() -> this.habitsDAO.getActiveHabits()
		);

		Assertions.assertEquals(habitNumTrue, habits.size());

		// Collect all the names
		List<String> habitNames = new ArrayList<>();
		for (int i = 0; i < habitNumTrue; i++) {
			habitNames.add(habits.get(i).getName());	
		}

		// Verify that all names are in result
		for (int i = 0; i < habitNumTrue; i++) {
			String name = String.format("habitActive%d", i);
			Assertions.assertTrue(habitNames.contains(name));
		}
	}

	@Test
	public void selectInactiveHabitsTest() {
		List<HabitsCatalog> habits = Assertions.assertDoesNotThrow(
			() -> this.habitsDAO.getInactiveHabits()
		);

		Assertions.assertEquals(habitNumFalse, habits.size());

		// Collect all the names
		List<String> habitNames = new ArrayList<>();
		for (int i = 0; i < habitNumFalse; i++) {
			habitNames.add(habits.get(i).getName());	
		}

		// Verify that all names are in result
		for (int i = 0; i < habitNumFalse; i++) {
			String name = String.format("habitInactive%d", i);
			Assertions.assertTrue(habitNames.contains(name));
		}

	}

	//
	// ========================== DELETION TESTS =============================
	//
	
	@Test
	public void deleteHabitTest_Correct() {
		List<HabitsCatalog> habitsOriginal = Assertions.assertDoesNotThrow(
			() -> this.habitsDAO.getHabitCatalog()
		);

		Assertions.assertDoesNotThrow(
				() -> this.habitsDAO.deleteHabit(1)
		);

		List<HabitsCatalog> habitsNew = Assertions.assertDoesNotThrow(
			() -> this.habitsDAO.getHabitCatalog()
		);	

		// Assert that the size is one less
		Assertions.assertEquals(habitsOriginal.size() - 1, habitsNew.size());
		// Assert that the first index has changed
		Assertions.assertNotEquals(habitsOriginal.get(0), habitsNew.get(0));
	}

	@Test
	public void deleteHabitTest_Incorrect() {
		List<HabitsCatalog> habitsOriginal = Assertions.assertDoesNotThrow(
			() -> this.habitsDAO.getHabitCatalog()
		);

		int invalidId = habitNumFalse + habitNumTrue + 300;
		Assertions.assertThrows(ObjectNotFoundException.class,
				() -> this.habitsDAO.deleteHabit(invalidId)
		);

		List<HabitsCatalog> habitsAfter = Assertions.assertDoesNotThrow(
			() -> this.habitsDAO.getHabitCatalog()
		);

		Assertions.assertEquals(habitsOriginal, habitsAfter);
	}

	@Test
	public void clearHabitsTest() {
		try {
			this.habitsDAO.clearHabitDatabase();
		} catch (Exception ex) {
			if (!(ex instanceof ForeignConstraintException)) {
				fail(String.format("Unexpected exception: %s", ex));
			}
		}

		List<HabitsCatalog> habits = Assertions.assertDoesNotThrow(
			() -> this.habitsDAO.getHabitCatalog()
		);

		Assertions.assertTrue(habits.isEmpty());
	}

	//
	// =========================== INSERTION TESTS ===========================
	//
	
	@Test
	public void insertHabitTest_Correct() {
		int newId = habitNumFalse + habitNumTrue + 1;
		String newName = "NewHabit";
		String desc = "This is a new habit";

		Assertions.assertDoesNotThrow(
			() -> this.habitsDAO.insertHabit(newName, desc, true)
		);	

		HabitsCatalog newHabit = Assertions.assertDoesNotThrow(
			() -> this.habitsDAO.getHabit(newId)
		);

		Assertions.assertEquals(newName, newHabit.getName());
	}
}
