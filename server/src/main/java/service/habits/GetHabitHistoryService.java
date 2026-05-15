package service.habits;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.google.type.Date;

import api.habits.*;

import dataaccess.exception.*;
import dataaccess.habits.*;

import model.jooq.habits.tables.pojos.*;

public class GetHabitHistoryService {
	//
	// ====================== CONSTRUCTORS =======================
	//
	
	private HistoryDAO historyDAO;

	public GetHabitHistoryService(HistoryDAO historyDAO) {
		this.historyDAO = historyDAO;
	}

	//
	// ======================== HELPER METHODS =======================
	//
	
	/** 
	 * Translates a LocalDate object into a protobuf google Date
	 * object
	 *
	 * @param date The date to convert
	 *
	 * @return The API date object
	 */
	private Date translateDate(LocalDate date) {
		return Date.newBuilder()
			.setYear(date.getYear())
			.setMonth(date.getMonthValue())
			.setDay(date.getDayOfMonth())
			.build();
	}

	/**
	 * Translates an API Date object into a LocalDate object
	 *
	 * @param date The date to convert
	 *
	 * @return The DAO level date object
	 */
	private LocalDate translateDate(Date date) {
		return LocalDate.of(
			date.getYear(),
			date.getMonth(),
			date.getDay()
		);
	}
	
	/**
	 * Translates a DAO level Habit History data object into an 
	 * API level Habit History data object.
	 *
	 * @param history The DAO level object
	 *
	 * @return The API level object
	 */
	private HabitHistory translateHistory(HabitsHistory history) {
		long historyId = history.getHistoryId().longValue();
		long habitId = history.getHabitId().longValue();
		// Got to translate the DAO `LocalDate` into the API `Date`
		Date completionDate = this.translateDate(history.getCompletionDate());
		boolean completed = history.getCompleted();
		String notes = history.getNotes();


		return HabitHistory.newBuilder()
			.setHistoryId(historyId)
			.setHabitId(habitId)
			.setCompletionDate(completionDate)
			.setCompleted(completed)
			.setNotes(notes)
			.build();
	}

	//
	// ======================== API TRANSLATION METHODS =====================
	//
	
	/**
	 * Translates an API request to get a specific habit history entry
	 * into a DAO request and forwards it along.
	 *
	 * @param request The API request
	 *
	 * @return The HistoryEntry from the DAO level
	 */
	public GetHabitHistoryResponse getHabitHistory(GetHabitHistoryRequest request) throws DataAccessException {
		long historyId = request.getHistoryId();

		HabitsHistory histDAO = this.historyDAO.getHistoryEntry(historyId);

		HabitHistory histAPI = this.translateHistory(histDAO);

		return GetHabitHistoryResponse.newBuilder()
			.setHistory(histAPI)
			.build();
	}

	/**
	 * Translates an API request to get all habits on a date range
	 * into a DAO call
	 *
	 * @param request The API request
	 *
	 * @return The API response containing all requested HabitHistory entries
	 */
	public ListHabitHistoryByDateResponse getHabitHistoryByDate(
		ListHabitHistoryByDateRequest request) throws DataAccessException
	{
		LocalDate startDate = this.translateDate(request.getStartDate());
		LocalDate endDate = this.translateDate(request.getEndDate());

		// Fetch the DAO entries and transform them into API entries
		List<HabitHistory> histories = new ArrayList<>();
		this.historyDAO.getHabitsHistoryByDateRange(startDate, endDate).forEach(
			history -> histories.add(this.translateHistory(history))
		);

		return ListHabitHistoryByDateResponse.newBuilder()
			.addAllHistoryList(histories)
			.build();
	}

	/**
	 * Translates an API request to get all history entries from
	 * a given habit id into a DAO call
	 *
	 * @param request The API request
	 *
	 * @return The API response containing all requested HabitHistory entries
	 */
	public ListHabitHistoryByHabitResponse getHabitHistoryByHabit(
		ListHabitHistoryByHabitRequest request) throws DataAccessException
	{
		long habitId = request.getHabitId();

		List<HabitHistory> histories = new ArrayList<>();
		this.historyDAO.getHabitsHistory(habitId).forEach(
			history -> histories.add(this.translateHistory(history))
		);

		return ListHabitHistoryByHabitResponse.newBuilder()
			.addAllHistoryList(histories)
			.build();
	}

	/**
	 * Helper function for get{completion_state}HistoryByDate methods.
	 * Fetches all history entries within a date that match a given 
	 * completion status
	 *
	 * @param startDate The start date of the daterange
	 * @param endDate The end date of the daterange
	 * @param status The completion status of the history entries to include
	 *
	 * @return A List of HabitHistory API objects that meet the critera
	 */
	private List<HabitHistory> getHistoryByDateByCompletionState(
			LocalDate startDate,
			LocalDate endDate,
			boolean status) throws DataAccessException {
		// Get all entries within the date range
		List<HabitHistory> histories = new ArrayList<>();
		this.historyDAO.getHabitsHistoryByDateRange(startDate, endDate).forEach(
			history -> histories.add(this.translateHistory(history))
		);	

		// Filter out the ones that do not match the desired completion status
		histories.removeIf(
			history -> history.getCompleted() != status
		);

		return histories;
	}

	/**
	 * Translates an API request to get all completed history entries from a given 
	 * date range id into a DAO call
	 *
	 * @param request the API request
	 *
	 * @return The API response containing all requested HabitHistory entries
	 */
	public ListCompletedHabitHistoryByDateResponse getCompletedHistoryByDate(
		ListCompletedHabitHistoryByDateRequest request) throws DataAccessException 
	{
		LocalDate startDate = this.translateDate(request.getStartDate());
		LocalDate endDate = this.translateDate(request.getEndDate());

		List<HabitHistory> histories = this.getHistoryByDateByCompletionState(startDate, endDate, true);

		return ListCompletedHabitHistoryByDateResponse.newBuilder()
			.addAllHistoryList(histories)
			.build();
	}

	/**
	 * Translates an API request to get all uncompleted history entries from a given
	 * date range into a DAO call
	 *
	 * @param request The API request
	 *
	 * @return The API response containing all requested HabitHistory entries
	 */
	public ListFailedHabitHistoryByDateResponse getFailedHistoryByDate(
		ListFailedHabitHistoryByDateRequest request) throws DataAccessException
	{
		LocalDate startDate = this.translateDate(request.getStartDate());
		LocalDate endDate = this.translateDate(request.getEndDate());

		List<HabitHistory> histories = this.getHistoryByDateByCompletionState(startDate, endDate, false);

		return ListFailedHabitHistoryByDateResponse.newBuilder()
			.addAllHistoryList(histories)
			.build();
	}

	/**
	 * Helper function for get{completion_status}HistoryByHabit().
	 * Returns a list of API HabitHistory objects that match the 
	 * given completion status and habit id.
	 *
	 * @param habitId The desired habitId
	 * @param state The completion state of the desired entries
	 *
	 * @return A List of API HabitHistory objects that meet the criteria
	 */
	private List<HabitHistory> getHistoryByIdByCompletionState(
		long habitId,
		boolean state) throws DataAccessException {
		// Fetch all the history entries that match the id
		List<HabitHistory> histories = new ArrayList<>();
		this.historyDAO.getHabitsHistory(habitId).forEach(
			history -> histories.add(this.translateHistory(history))
		);
		
		// filter out the ones that do not match the completion state
		histories.removeIf(
			history -> history.getCompleted() != state
		);
		
		return histories;
	}

	/**
	 * Translates an API request to get all completed history entries from
	 * a given habit id into a DAO call
	 *
	 * @param request the API request
	 *
	 * @return The API response containing all requested HabitHistory entries
	 */
	public ListCompletedHabitHistoryByHabitResponse getCompletedHistoryByHabit(
		ListCompletedHabitHistoryByHabitRequest request) throws DataAccessException
	{
		Long habitId = request.getHabitId();

		List<HabitHistory> histories = this.getHistoryByIdByCompletionState(habitId, true);

		return ListCompletedHabitHistoryByHabitResponse.newBuilder()
			.addAllHistoryList(histories)
			.build();
	}

	/**
	 * Translates an API request to get all failed history entries from
	 * a given habit id into a DAO call
	 *
	 * @param request the API request
	 *
	 * @param The API response containing all requested HabitHistory entries
	 */
	public ListFailedHabitHistoryByHabitResponse getFailedHistoryByHabit(
		ListFailedHabitHistoryByHabitRequest request) throws DataAccessException
	{
		Long habitId = request.getHabitId();

		List<HabitHistory> histories = this.getHistoryByIdByCompletionState(habitId, true);

		return ListFailedHabitHistoryByHabitResponse.newBuilder()
			.addAllHistoryList(histories)
			.build();
	}
}
