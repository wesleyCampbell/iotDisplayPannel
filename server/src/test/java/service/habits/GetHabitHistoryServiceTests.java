package service.habits;

import dataaccess.habits.*;
import dataaccess.exception.*;

import api.habits.*;
import model.jooq.habits.tables.pojos.*;

import org.jooq.types.ULong;
import org.junit.jupiter.api.*;

import com.google.type.Date;

import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

public class GetHabitHistoryServiceTests {
	//
	// ===================== GLOBALS ==================
	//
	
	private static final int HABIT_NUM = 2;
	private static final int HISTORY_NUM = 3;
	private static final LocalDate TODAY = LocalDate.now();
	
	//
	// ===================== TEST INITIALIZATION ===================
	//

	private static HistoryDAO historyDAO;
	private static GetHabitHistoryService service;
	private static List<HabitsHistory> histories = new ArrayList<>();

	/**
	 * Initializes the DAO and service objects.
	 * Additionally, mocks the DAO class such that it won't use a database 
	 * but will perform its logic using memory-based storage.
	 * Basic implementation of all HistoryDAO methods
	 */
	@BeforeAll
	public static void initTests() throws DataAccessException {
		historyDAO = mock(HistoryDAO.class);
		service = new GetHabitHistoryService(historyDAO);

		// Prepares mock HistoryDAO to return mock data
		// when requested
		for (int i = 0; i < HABIT_NUM; i++) {
			long habitId = i + 1;
			for (int j = 0; j < HISTORY_NUM; j++) {
				long historyId = j + i * HISTORY_NUM + 1;

				HabitsHistory hist = new HabitsHistory(
					ULong.valueOf(historyId),    // history_id
					ULong.valueOf(habitId),        // habit_id
					TODAY.minusDays(j + 1),      // completionDate
					i % 2 == 0,                  // completed
					"These are some notes"       // notes
				);

				histories.add(hist);

				// Fetch the correct entry by its historyID
				when(historyDAO.getHistoryEntry(historyId)).thenReturn(
					histories.get((int)historyId - 1)
				);
			}

			// Fetch the correct entries by its habitID
			when(historyDAO.getHabitsHistory(habitId)).thenReturn(
				new ArrayList<HabitsHistory>(
					histories.subList(i * HISTORY_NUM, (i + 1) * HISTORY_NUM)
				)
			);
		}

		// Fetch the correct entries by its date
		for (int j = 0; j < HISTORY_NUM; j++) {
			LocalDate date = TODAY.minusDays(j + 1);
			
			// Create a copy of the histories and filter out all incorrect dates
			List<HabitsHistory> dateHists = histories.stream()
				.filter(history -> history.getCompletionDate().equals(date))
				.toList();

			// Return them when querried
			when(historyDAO.getHabitsHistory(date)).thenReturn(
				dateHists	
			);
		}

		// Fetch the correct entries by date range
		when(historyDAO.getHabitsHistoryByDateRange(any(LocalDate.class), any(LocalDate.class)))
			.thenAnswer(invocation -> {
				LocalDate start = invocation.getArgument(0);
				LocalDate end = invocation.getArgument(1);

				return getHistoriesRange(start, end);
			});
	}
	
	//
	// ===================== HELPER METHODS ===================
	//
	
	/**
	 * Tranforms a Date (com.google) to a LocalDate (java.time)
	 *
	 * @param date The date to transform
	 *
	 * @return the LocalDate
	 */
	private static LocalDate convertDate(Date date) {
		return LocalDate.of(
			date.getYear(),
			date.getMonth(),
			date.getDay()
		);
	}

	/**
	 * Transforms a LocalDate (java.time) into a Date (com.google)
	 *
	 * @param date The date to transform
	 *
	 * @return The Date object
	 */
	private static Date convertDate(LocalDate date) {
		return Date.newBuilder()
			.setYear(date.getYear())
			.setMonth(date.getMonthValue())
			.setDay(date.getDayOfMonth())
			.build();
	}

	/**
	 * Fetches the history entires that lie within a given date range
	 *
	 * @param startDate The start date of the range
	 * @param endDate the end date of the range
	 *
	 * @return A list of the valid entries.
	 */
	private static List<HabitsHistory> getHistoriesRange(LocalDate startDate, LocalDate endDate) {
		return histories.stream()
			.filter(history -> 
					!history.getCompletionDate().isBefore(startDate) &&
					!history.getCompletionDate().isAfter(endDate)
			)
			.toList();
	}

	/**
	 * Fetches the history entries that match the given habitId
	 *
	 * @param habitId The habit id of the histories to return
	 *
	 * @return A list containing the requested habit entries
	 */
	private static List<HabitsHistory> getHistoriesHabitId(long habitId) {
		ULong id = ULong.valueOf(habitId);

		return histories.stream().filter(
			history -> history.getHabitId().equals(id)
		).toList();
	}
	
	/**
	 * Tests the equality between an API and DAO representation of a Habit History entry
	 *
	 * @param histDAO The DAO representation of the history
	 * @param histAPI The API representation of the history
	 *
	 * @return true if equal, false, otherwise
	 */
	private static boolean isEqualHistory(HabitsHistory histDAO, HabitHistory histAPI) {
		// check history id
		if (histAPI.getHistoryId() != histDAO.getHistoryId().longValue()) {
			return false;
		}

		// check habit id
		if (histDAO.getHabitId().longValue() != histAPI.getHabitId()) {
			return false;
		}

		// check completion date
		LocalDate dateAPI = convertDate(histAPI.getCompletionDate());
		if (!histDAO.getCompletionDate().equals(dateAPI)) {
			return false;
		}

		// check completion status
		if (histDAO.getCompleted() != histAPI.getCompleted()) {
			return false;
		}

		// check notes
		if (!histDAO.getNotes().equals(histAPI.getNotes())) {
			return false;
		}

		return true;
	}

	/**
	 * Sorts a List of Habit Entries by its historyId
	 *
	 * @param histories The list of history entries
	 *
	 * @return A sorted list
	 */
	private List<HabitHistory> historiesSortedAPI(List<HabitHistory> histories) {
		return histories.stream()
			.sorted(Comparator.comparing(HabitHistory::getHistoryId))
			.toList();
	}

	/**
	 * Sorts a List of Habit Entries by its historyId
	 *
	 * @param histories The list of history entries
	 *
	 * @return A sorted list
	 */
	private List<HabitsHistory> historiesSortedDAO(List<HabitsHistory> histories) {
		return histories.stream()
			.sorted(Comparator.comparing(HabitsHistory::getHistoryId))
			.toList();
	}
	
	//
	// ===================== UNIT TESTS ===================
	//
	
	/**
	 * Tests that the service fetches the correct data from the DAO level.
	 * for getHabitHistory()
	 */
	@Test
	public void getHabitHistoryTest() throws DataAccessException {
		for (int i = 0; i < HABIT_NUM; i++) {
			for (int j = 0; j < HISTORY_NUM; j++) {
				long historyId = j + HISTORY_NUM * i + 1;	

				GetHabitHistoryRequest request = GetHabitHistoryRequest.newBuilder()
					.setHistoryId(historyId).build();

				GetHabitHistoryResponse response = service.getHabitHistory(request);

				HabitsHistory historyDAO = histories.get((int)historyId - 1);
				HabitHistory historyAPI = response.getHistory();

				Assertions.assertTrue(isEqualHistory(historyDAO, historyAPI));
			}
		}
	}

	/**
	 * Tests that the service fetches the correct data from the DAO level.
	 * for getHabitHistoryByDate()
	 */
	@Test
	public void getHabitHistoryByDateTest() throws DataAccessException {
		for (int j = 0; j < HISTORY_NUM; j++) {
			LocalDate date = TODAY.minusDays(j + 1);	

			ListHabitHistoryByDateRequest request = ListHabitHistoryByDateRequest.newBuilder()
				.setStartDate(convertDate(date))
				.setEndDate(convertDate(TODAY))
				.build();

			ListHabitHistoryByDateResponse response = service.getHabitHistoryByDate(request);

			// make the calls and sort the return values by history id
			List<HabitHistory> historiesAPI = historiesSortedAPI(response.getHistoryListList());
			List<HabitsHistory> historiesDAO = historiesSortedDAO(getHistoriesRange(date, TODAY));

			// Make sure that the two methods return the same entries
			Assertions.assertEquals(historiesDAO.size(), historiesAPI.size());
			
			// Make sure each entry is the same.
			for (int i = 0; i < historiesDAO.size(); i++) {
				HabitsHistory histDAO = historiesDAO.get(i);
				HabitHistory histAPI = historiesAPI.get(i);

				Assertions.assertTrue(isEqualHistory(histDAO, histAPI));
			}
		}
	}

	/**
	 * Tests that the service fetches the correct data from the DAO level
	 * for getHabitHistoryByHabit()
	 */
	@Test
	public void getHabitHistoryByHabitTest() throws DataAccessException {
		for (int i = 0; i < HABIT_NUM; i++) {
			long habitID = i + 1;

			ListHabitHistoryByHabitRequest request = ListHabitHistoryByHabitRequest.newBuilder()
				.setHabitId(habitID)
				.build();

			ListHabitHistoryByHabitResponse response = service.getHabitHistoryByHabit(request);

			// Make the calls and sort the returned values by history id
			List<HabitsHistory> historiesDAO = historiesSortedDAO(getHistoriesHabitId(habitID));
			List<HabitHistory> historiesAPI = historiesSortedAPI(response.getHistoryListList());

			// Assert that they are the same length
			Assertions.assertEquals(historiesDAO.size(), historiesAPI.size());

			for (int j = 0; j < historiesDAO.size(); j++) {
				HabitsHistory histDAO = historiesDAO.get(i);
				HabitHistory histAPI = historiesAPI.get(i);

				Assertions.assertTrue(isEqualHistory(histDAO, histAPI));
			}
		}
	}

	/**
	 * Tests that the service fetches the correct data from the DAO level
	 * for getCompletedHistoryByDate()
	 */
	@Test
	public void getCompletedHistoryByDateTest() throws DataAccessException {

	}

	public void getFailedHistoryByDateTest() throws DataAccessException {

	}

	public void getCompletedHistoryByHabitTest() throws DataAccessException {

	}

	public void getFailedHistoryByHabitTest() throws DataAccessException {

	}
}
