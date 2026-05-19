package service.habits;

import dataaccess.habits.*;
import dataaccess.exception.*;

import api.habits.*;
import model.jooq.habits.enums.HabitsStatsGoalType;
import model.jooq.habits.tables.pojos.*;

import org.jooq.types.ULong;
import org.junit.jupiter.api.*;

import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ListHabitsServiceTests {
	//
	// ==================== GLOBALS ====================
	//
	
	private static int HABIT_NUM = 8;
	
	//
	// ==================== TEST INITIALIZATION ========
	//
	
	private static HabitsDAO habitsDAO;
	private static ListHabitsService service;

	private static List<HabitsCatalog> habits = new ArrayList<>();

	/**
	 * Fetches all the cached habit catalog entries that match
	 * a given completion status
	 *
	 * @param status The completion status of the habits to fetch
	 */
	private static List<HabitsCatalog> filterHabitsByActiveState(boolean status) {
		return habits.stream()
			.filter(
				habit -> habit.getActive() == status
			).toList();
	}

	@BeforeAll
	/**
	 * Initializes the DAO and service objects for the tests.
	 * Writes up the logic for the mock DAO.
	 */
	public static void initTests() throws DataAccessException {
		habitsDAO = mock(HabitsDAO.class);
		service = new ListHabitsService(habitsDAO);

		for (int i = 0; i < HABIT_NUM; i++) {
			ULong habitID = ULong.valueOf(i + 1);
			String name = String.format("Habit%d", i + 1);
			String desc = "This is a very description";
			LocalDateTime now = LocalDateTime.now();
			boolean active = i % 2 == 0;

			HabitsCatalog habit = new HabitsCatalog(
				habitID,
				name,
				desc,
				now,
				active
			);

			habits.add(habit);

			when(habitsDAO.getHabit(i + 1)).thenReturn(habit);
		}

		when(habitsDAO.getHabitCatalog()).thenReturn(habits);
		when(habitsDAO.getActiveHabits()).thenReturn(
			filterHabitsByActiveState(true)
		);
		when(habitsDAO.getInactiveHabits()).thenReturn(
			filterHabitsByActiveState(false)
		);
	}

	/**
	 * Translates a DAO representation of a habit to an API
	 * representation
	 *
	 * @param habit The habit to convert
	 *
	 * @return The API representation of the habit
	 */
	private HabitEntry translateHabit(HabitsCatalog habit) {
		long habitId = habit.getHabitId().longValue();
		String name = habit.getName();
		String desc = habit.getDescription();
		boolean active = habit.getActive();

		return HabitEntry.newBuilder()
			.setHabitId(habitId)
			.setName(name)
			.setDescription(desc)
			.setActive(active)
			.build();
	}

	/**
	 * Determines whether a DAO representation of a habit entry is equal to an 
	 * API representation.
	 *
	 * @param habitDAO The DAO representation of the habit
	 * @param habitAPI The API representation of the habit
	 *
	 * @return True if equal, false otherwise
	 */
	private boolean isHabitEquals(HabitsCatalog habitDAO, HabitEntry habitAPI) {
		// check habitId
		if (habitDAO.getHabitId().longValue() != habitAPI.getHabitId()) {
			return false;
		}

		// check name
		if (!habitDAO.getName().equals(habitAPI.getName())) {
			return false;
		}

		// check description
		if (!habitDAO.getDescription().equals(habitAPI.getDescription())) {
			return false;
		}

		// check active status
		if (habitDAO.getActive() != habitAPI.getActive()) {
			return false;
		}

		return true;
	}

	/**
	 * Fetches a habit from the DAO level that matches a habitID
	 *
	 * @param habitID The habit id of the habit to fetch
	 *
	 * @return The requested habit
	 */
	private HabitsCatalog getHabitById(long habitID) throws DataAccessException {
		for (HabitsCatalog h : habits) {
			if (h.getHabitId().longValue() == habitID) {
				return h;
			}
		}

		throw new DataAccessException("The requested method is not valid!");
	}
	
	//
	// =================== UNIT TESTS ===================
	//
	
	/**
	 * Tests that the service fetches the correct data from the DAO
	 * level from getHabitCatalog()
	 */
	@Test
	public void getHabitCatalogTest() throws DataAccessException {
		GetHabitCatalogRequest request = GetHabitCatalogRequest.newBuilder()
			.build();

		// Make the API call
		GetHabitCatalogResponse response = service.getHabitCatalog(request);

		// Extract API and DAO data
		List<HabitEntry> habitsAPI = response.getHabitDataListList();
		List<HabitsCatalog> habitsDAO = habits;

		// Assert that the lists are the same length
		Assertions.assertEquals(habitsDAO.size(), habitsAPI.size());

		for (int i = 0; i < habitsDAO.size(); i++) {
			HabitEntry habitAPI = habitsAPI.get(i);
			HabitsCatalog habitDAO = habitsDAO.get(i);

			Assertions.assertTrue(isHabitEquals(habitDAO, habitAPI));
		}
	}

	/**
	 * Tests that the service fetches the correct data from the DAO
	 * level from getHabitEntry()
	 */
	@Test
	public void getHabitEntryTest() throws DataAccessException {
		for (int i = 0; i < HABIT_NUM; i++) {
			long id = i + 1;

			GetHabitEntryRequest request = GetHabitEntryRequest.newBuilder()
				.setHabitId(id)
				.build();

			GetHabitEntryResponse response = service.getHabitEntry(request);

			HabitEntry habitAPI = response.getHabitData();
			HabitsCatalog habitDAO = getHabitById(id);

			Assertions.assertTrue(isHabitEquals(habitDAO, habitAPI));
		}
	}

	/**
	 * tests that the service fetches the correct data from the DAO
	 * level from getActiveHabits()
	 */
	@Test 
	public void getActiveHabitsTest() throws DataAccessException {
		GetActiveHabitsRequest request = GetActiveHabitsRequest.newBuilder().build();

		// make the API call
		GetActiveHabitsResponse response = service.getActiveHabits(request);

		// extract the API and DAO data
		List<HabitsCatalog> habitsDAO = filterHabitsByActiveState(true);
		List<HabitEntry> habitsAPI = response.getHabitDataListList();

		// Assert that the two data sets are equal
		Assertions.assertEquals(habitsDAO.size(), habitsAPI.size());

		for (int i = 0; i < habitsDAO.size(); i++) {
			HabitsCatalog habitDAO = habitsDAO.get(i);
			HabitEntry habitAPI = habitsAPI.get(i);

			Assertions.assertTrue(isHabitEquals(habitDAO, habitAPI));
		}
	}

	/**
	 * Tests that the service fetches the correct data from the DAO
	 * level from getInactiveHabits()
	 */
	@Test
	public void getInactiveHabitsTest() throws DataAccessException {
		GetInactiveHabitsRequest request = GetInactiveHabitsRequest.newBuilder().build();

		// make the API call
		GetInactiveHabitsResponse response = service.getInactiveHabits(request);

		// extract the API and DAO data
		List<HabitsCatalog> habitsDAO = filterHabitsByActiveState(false);
		List<HabitEntry> habitsAPI = response.getHabitDataListList();

		// Assert that the two data sets are equal
		Assertions.assertEquals(habitsDAO.size(), habitsAPI.size());

		for (int i = 0; i < habitsDAO.size(); i++) {
			HabitsCatalog habitDAO = habitsDAO.get(i);
			HabitEntry habitAPI = habitsAPI.get(i);

			Assertions.assertTrue(isHabitEquals(habitDAO, habitAPI)); }
	}
}
