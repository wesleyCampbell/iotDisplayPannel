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

public class UpdateHabitHistoryNotesServiceTests {
	// 
	// =================== GLOBALS =================
	//

	private static final int HISTORY_NUM = 4;
	private static final int HABIT_ID = 1;

	private static final LocalDate TODAY = LocalDate.now();
	private static final String OLD_NOTE = "old note";

	//
	// =================== TEST INITIALIZATION ===============
	//
	
	private static HistoryDAO historyDAO;
	private static UpdateHabitHistoryNotesService service;

	private static List<HabitsHistory> histories = new ArrayList<>();
	
	@BeforeAll
	public static void initTests() throws DataAccessException {
		historyDAO = mock(HistoryDAO.class);
		service = new UpdateHabitHistoryNotesService(historyDAO);

		for (long i = 1; i <= HISTORY_NUM; i++) {
			HabitsHistory hist = new HabitsHistory(
				ULong.valueOf(i),
				ULong.valueOf(HABIT_ID),
				TODAY,
				false,
				OLD_NOTE
			);

			histories.add(hist);

			when(historyDAO.getHistoryEntry(i)).thenReturn(hist);

			// Allows the notes to be modified
			doAnswer(new Answer<Void>() {
				@Override
				public Void answer(InvocationOnMock invocation) {
					String notes = invocation.getArgument(1);

					hist.setNotes(notes);

					return null;
				}

			}).when(historyDAO).setHistoryEntryNotes(eq(i), anyString());
		}
	}
	
	//
	// =================== UNIT TESTS ================
	//

	@Test
	public void updateHabitHistoryNotesTest() throws DataAccessException {
		for (int i = 0; i < HISTORY_NUM; i++) {
			long id = i + 1;

			String newNote = String.format("New Note #%d", id);

			// Change the note attached
			UpdateHabitHistoryNotesRequest request = UpdateHabitHistoryNotesRequest.newBuilder()
				.setHistoryId(id)
				.setNewNotes(newNote)
				.build();

			UpdateHabitHistoryNotesResponse response = service.updateHabitHistoryNotes(request);

			HabitsHistory histDAO = histories.get(i);

			Assertions.assertEquals(newNote, histDAO.getNotes());
		}
	}
}
