package dataaccess.habits;

import org.junit.jupiter.api.*;

import static org.junit.Assert.fail;
import model.jooq.habits.tables.pojos.HabitsCatalog;

import java.util.List;
import java.util.ArrayList;

import dataaccess.exception.*;

public class HabitsDAOTests extends HabitsDAOTestParent {
	//
	// ======================= CONSTRUCTORS =====================
	//
	
	private HabitsDAO habitsDAO;

	public HabitsDAOTests() {
		super((dbManager) -> initTestTables(dbManager));
	}

	//
	// ================ TEST INITIALIZATION =================
	//
	
	/**
	 * Makes sure that the DAO is hooked up to the correct database for each test
	 */
	@BeforeAll
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
	
	//
	// ========================== SELECTION TESTS =============================
	//
	
	/**
	 * Tests to make sure that the DAO can correctly return individual habits based on HabitId
	 */
	@Test
	public void selectOneHabitIdTest_Correct() {
		// Make sure can access all active tests
		for (int i = 0; i < HABIT_NUM_TRUE; i++) {
			HabitsCatalog habit;
			int id = i + 1;
			habit = Assertions.assertDoesNotThrow(() -> this.habitsDAO.getHabit(id));		
			final String nameExpected = String.format("habitActive%d", i);
			Assertions.assertEquals(nameExpected, habit.getName());
		}

		// Make sure can access all inactive tests 
		for (int i = 0; i < HABIT_NUM_FALSE; i++) {
			HabitsCatalog habit;
			int id = HABIT_NUM_TRUE + i + 1;
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
		int id = HABIT_NUM_TRUE + HABIT_NUM_FALSE + 1;

		Assertions.assertThrows(ObjectNotFoundException.class, () -> this.habitsDAO.getHabit(id));
	}

	/**
	 * Tests to make sure the DAO can correctly return a habit by name
	 */
	@Test
	public void selectOneHabitNameTest_Correct() {
		// Make sure can access all active tests
		for (int i = 0; i < HABIT_NUM_TRUE; i++) {
			HabitsCatalog habit;
			String name = String.format("habitActive%d", i);
			habit = Assertions.assertDoesNotThrow(() -> this.habitsDAO.getHabit(name));		

			int expectedId = i + 1;
			Assertions.assertEquals(expectedId, habit.getHabitId().intValue());
		}

		// Make sure can access all inactive tests 
		for (int i = 0; i < HABIT_NUM_FALSE; i++) {
			HabitsCatalog habit;
			String name = String.format("habitInactive%d", i);
			habit = Assertions.assertDoesNotThrow(() -> this.habitsDAO.getHabit(name));

			int expectedId = HABIT_NUM_TRUE + i + 1;
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

		int totalNum = HABIT_NUM_TRUE + HABIT_NUM_FALSE;
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

		Assertions.assertEquals(HABIT_NUM_TRUE, habits.size());

		// Collect all the names
		List<String> habitNames = new ArrayList<>();
		for (int i = 0; i < HABIT_NUM_TRUE; i++) {
			habitNames.add(habits.get(i).getName());	
		}

		// Verify that all names are in result
		for (int i = 0; i < HABIT_NUM_TRUE; i++) {
			String name = String.format("habitActive%d", i);
			Assertions.assertTrue(habitNames.contains(name));
		}
	}

	@Test
	public void selectInactiveHabitsTest() {
		List<HabitsCatalog> habits = Assertions.assertDoesNotThrow(
			() -> this.habitsDAO.getInactiveHabits()
		);

		Assertions.assertEquals(HABIT_NUM_FALSE, habits.size());

		// Collect all the names
		List<String> habitNames = new ArrayList<>();
		for (int i = 0; i < HABIT_NUM_FALSE; i++) {
			habitNames.add(habits.get(i).getName());	
		}

		// Verify that all names are in result
		for (int i = 0; i < HABIT_NUM_FALSE; i++) {
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

		int invalidId = HABIT_NUM_FALSE + HABIT_NUM_TRUE + 300;
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
		int newId = HABIT_NUM_FALSE + HABIT_NUM_TRUE + 1;
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
