package service.habits;

import dataaccess.habits.*;
import dataaccess.exception.*;

import java.time.LocalDate;

import api.habits.*;
import com.google.type.Date;

public class CreateHabitHistoryService {
	//
	// ================= CONSTRUCTORS ===================
	//
	
	private HistoryDAO historyDAO;

	public CreateHabitHistoryService(HistoryDAO historyDAO) {
		this.historyDAO = historyDAO;
	}

	//
	// ================= API TRANSLATION METHODS ==============
	//
	
	/**
	 * Converts a com.google.Date to a java.util.LocalDate
	 * 
	 * @param date The date to convert
	 *
	 * @return The LocalDate
	 */
	private LocalDate convertDate(Date date) {
		return LocalDate.of(
			date.getYear(),
			date.getMonth(),
			date.getDay()
		);
	}
	
	/**
	 * Helper method for the Create Habits that performs the actual logic and 
	 * DAO calls
	 *
	 * @param habitId The habit id of the new history entry
	 * @param completionDate The completion date of the new history entry
	 * @param completed The completion status of the new history entry
	 * @param notes The notes associated with the new history entry
	 *
	 * @return The history id of the new history entry
	 */
	private long createHabitHelper(long habitId, LocalDate completionDate, boolean completed, String notes) throws DataAccessException {
		return this.historyDAO.createHistoryEntry(
			habitId,
			completionDate,
			completed,
			notes
		)	;
	}
	
	/**
	 * Translates an API request to create a new habit history entry 
	 * on the DAO level
	 *
	 * @param request The API request
	 *
	 * @return The API response containing the new id;
	 */
	public CreateHabitHistoryResponse createHabit(CreateHabitHistoryRequest request) throws DataAccessException {
		long habitId = request.getHabitId();
		boolean completed = request.getCompleted();
		String notes = request.getNotes();
		LocalDate completionDate = LocalDate.now();

		long historyId = this.createHabitHelper(habitId, completionDate, completed, notes);	

		return CreateHabitHistoryResponse.newBuilder()
			.setHistoryId(historyId)
			.build();
	}

	/**
	 * Translates an API request to create a new habit history entry 
	 * on the DAO level
	 *
	 * @param request The API request
	 *
	 * @return The API response containing the new id;
	 */
	public CreateHabitHistoryResponse createHabitDated(CreateHabitHistoryDatedRequest request) throws DataAccessException {
		long habitId = request.getHabitId();
		boolean completed = request.getCompleted();
		String notes = request.getNotes();
		LocalDate completionDate = convertDate(request.getCompletionDate());

		long historyId = this.createHabitHelper(habitId, completionDate, completed, notes);

		return CreateHabitHistoryResponse.newBuilder()
			.setHistoryId(historyId)
			.build();
	}
}

