package service.habits;

import dataaccess.habits.*;
import dataaccess.exception.*;

import api.habits.*;

import org.junit.jupiter.api.*;

import static org.mockito.Mockito.*;

public class CreateHabitServiceTests {
	//
	// ======================== GLOBALS =======================
	//
	
	private static final String NAME1 = "Excercize";
	private static final String NAME2 = "Annoying mom";
	private static final String DESC1 = "desc1";
	private static final String DESC2 = "desc2";
	private static final boolean STATUS1 = false;
	private static final boolean STATUS2 = true;

	private static final CreateHabitEntryRequest REQUEST1 
		= CreateHabitEntryRequest.newBuilder()
		.setName(NAME1)
		.setDescription(DESC1)
		.setActive(STATUS1)
		.build();

	private static final CreateHabitEntryRequest REQUEST2
		= CreateHabitEntryRequest.newBuilder()
		.setName(NAME2)
		.setDescription(DESC2)
		.setActive(STATUS2)
		.build();

	//
	// ======================= TEST INITIALIZATION ==================
	//
	
	private static HabitsDAO habitsDAO;
	private static CreateHabitService service;

	/**
	 * Initializes the DAO and service objects for the tests
	 */
	@BeforeAll 
	public static void initTests() throws DataAccessException {
		habitsDAO = mock(HabitsDAO.class);
		service = new CreateHabitService(habitsDAO);
	}

	//
	// ======================== UNIT TESTS ============================
	//
	
	/**
	 * Verifies that the service calls the correct DAO methods for creation.
	 */
	@Test
	public void createGameTest() throws DataAccessException {
		// Make the first request and verify that the correct method was called
		CreateHabitEntryResponse response1 = service.createGame(REQUEST1);
		verify(habitsDAO, times(1)).insertHabit(NAME1, DESC1, STATUS1);	
		Assertions.assertNotNull(response1);

		// Make the second request and verify that the correct method was called
		CreateHabitEntryResponse response2 = service.createGame(REQUEST2);
		verify(habitsDAO, times(1)).insertHabit(NAME2, DESC2, STATUS2);
		Assertions.assertNotNull(response2);

		// Verify that no other DAO methods are called other than create
		verifyNoMoreInteractions(habitsDAO);
	}
}
