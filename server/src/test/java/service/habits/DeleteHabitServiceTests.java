package service.habits;

import dataaccess.habits.*;
import dataaccess.exception.*;

import api.habits.*;

import org.junit.jupiter.api.*;

import static org.mockito.Mockito.*;

public class DeleteHabitServiceTests {
	//
	// ========================== GLOBALS ======================
	//
	
	private static final long ID1 = 3298;
	private static final long ID2 = 49;

	private static final DeleteHabitEntryRequest REQUEST1
		= DeleteHabitEntryRequest.newBuilder()
		.setHabitId(ID1)
		.build();
	private static final DeleteHabitEntryRequest REQUEST2
		= DeleteHabitEntryRequest.newBuilder()
		.setHabitId(ID2)
		.build();

	//
	// ======================== TEST INITIALIZATION ==================
	//
	
	private static HabitsDAO habitsDAO;
	private static DeleteHabitService service;

	/**
	 * Initializes the DAO and service
	 */
	@BeforeAll
	public static void initTests() throws DataAccessException {
		habitsDAO = mock(HabitsDAO.class);
		service = new DeleteHabitService(habitsDAO);
	}

	//
	// ======================== UNIT TESTS ==========================
	//
	
	/**
	 * Tests to make sure that the correct delete DAO method is called
	 * from the service class.
	 */
	@Test 
	void deleteGameTest() throws DataAccessException {
		// Make first request and verify that the correct method was called
		DeleteHabitEntryResponse response1 = service.deleteHabit(REQUEST1);
		verify(habitsDAO, times(1)).deleteHabit(ID1);
		Assertions.assertNotNull(response1);

		// Make the second request and verify it
		DeleteHabitEntryResponse response2 = service.deleteHabit(REQUEST2);
		verify(habitsDAO, times(1)).deleteHabit(ID2);
		Assertions.assertNotNull(response2);

		// Verify that no other DAO methds are called
		verifyNoMoreInteractions(habitsDAO);
	}
}

