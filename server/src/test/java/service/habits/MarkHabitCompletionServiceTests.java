package service.habits;

import dataaccess.habits.*;
import dataaccess.exception.*;

import api.habits.*;
import model.jooq.habits.enums.HabitsStatsGoalType;
import model.jooq.habits.tables.pojos.*;

import org.jooq.types.ULong;
import org.junit.jupiter.api.*;

import static org.mockito.Mockito.*;
import org.mockito.invocation.*;
import org.mockito.stubbing.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MarkHabitCompletionServiceTests {
	//
	// ======================= GLOBALS ======================
	//
	
	private static final int HISTORY_NUM = 3;
	private static final int HABIT_ID = 1;

	private static final LocalDate TODAY = LocalDate.now();

	//
	// ====================== TEST INITIALIZATION ================
	//
	
	private static HistoryDAO historyDAO;
	private static MarkHabitCompletionService service;

	private static List<HabitsHistory> histories = new ArrayList<>();
	
	/**
	 * Initializes the DAO and service.
	 * Outlines the mock data access logic
	 */
	@BeforeAll
	public static void initTests() throws DataAccessException {
		historyDAO = mock(HistoryDAO.class);
		service = new MarkHabitCompletionService(historyDAO);

		for (int i = 0; i < HISTORY_NUM; i++) {
			long id = i + 1;

			HabitsHistory hist = new HabitsHistory(
				ULong.valueOf(id),
				ULong.valueOf(HABIT_ID),
				TODAY,
				false,
				"note here"
			);

			histories.add(hist);

			when(historyDAO.getHistoryEntry(id)).thenReturn(hist);

			// Allows the boolean state to be modified
			doAnswer(new Answer<Void>() {
				@Override
				public Void answer(InvocationOnMock invocation) {
					boolean state = invocation.getArgument(1);

					hist.setCompleted(state);

					return null;
				}
			}).when(historyDAO).setHistoryEntryCompleteState(eq(id), anyBoolean());
		}
	}
	
	//
	// ====================== UNIT TESTS ==================
	//

	/**
	 * Test that the service fetches the correct data from the DAO level.
	 * tests setHistoryCompletionState()
	 */
	@Test
	public void setHistoryCompletionStateTest() throws DataAccessException {
		for (int i = 0; i < HISTORY_NUM; i++) {
			long id = i + 1;

			// make sure it can be set to true
			MarkHistoryCompletionStateRequest request = MarkHistoryCompletionStateRequest.newBuilder()
				.setHistoryId(id)
				.setStatus(true)
				.build();

			// make the API call
			MarkHistoryCompletionStateResponse response = service.setHistoryCompletionState(request);

			// verify that the status was changed
			HabitsHistory historyDAO = histories.get(i);
			
			Assertions.assertEquals(true, historyDAO.getCompleted());

			// Make sure it can be set to false
			MarkHistoryCompletionStateRequest request2 = MarkHistoryCompletionStateRequest.newBuilder()
				.setHistoryId(id)
				.setStatus(false)
				.build();

			response = service.setHistoryCompletionState(request2);

			Assertions.assertEquals(false, historyDAO.getCompleted());
		}
	}
}
