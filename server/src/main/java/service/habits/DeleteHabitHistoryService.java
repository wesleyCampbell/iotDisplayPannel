package service.habits;

import dataaccess.habits.*;
import dataaccess.exception.*;

import java.time.LocalDate;

import api.habits.*;
import com.google.type.Date;

public class DeleteHabitHistoryService {
	//
	// =============== CONSTRUCTORS ===============
	//
	
	private HistoryDAO historyDAO;

	public DeleteHabitHistoryService(HistoryDAO historyDAO) {
		this.historyDAO = historyDAO;
	}

	//
	// ==================== API TRANSLATION METHODS =================
	//
	
	/**
	 * Translates an API request to delete an individual history
	 * entry into a DAO call.
	 *
	 * @param request The API request
	 *
	 * @param The API response
	 */
	DeleteHistoryEntryResponse deleteHistoryEntry(DeleteHistoryEntryRequest request) throws DataAccessException {
		long historyId = request.getHistoryId();

		this.historyDAO.deleteHistoryEntry(historyId);

		return DeleteHistoryEntryResponse.newBuilder().build();
	}

	/**
	 * Translates an API request to delete all habit history 
	 * entries associated with a habit id.
	 *
	 * @param request The API request
	 *
	 * @param The API response
	 */
	DeleteHabitHistoryResponse deleteHabitHistory(DeleteHabitHistoryRequest request) throws DataAccessException {
		long habitId = request.getHabitId();

		this.historyDAO.deleteHabitsHistory(habitId);

		return DeleteHabitHistoryResponse.newBuilder().build();
	}

	/**
	 * converts a com.google.Date to a java.util.LocalDate
	 *
	 * @param date The date to convert
	 *
	 * @return The Local Date object
	 */
	private LocalDate convertDate(Date date) {
		return LocalDate.of(
			date.getYear(),
			date.getMonth(),
			date.getDay()
		);
	}

	/**
	 * Translates an API request to delete all habit history
	 * entries associated with a date.
	 *
	 * @param request The API request
	 *
	 * @param The API response
	 */
	DeleteHistoryEntriesResponse deleteHistoryEntries(DeleteHistoryEntriesRequest request) throws DataAccessException {
		LocalDate date = convertDate(request.getCompletionDate());

		this.historyDAO.deleteHabitsHistory(date);

		return DeleteHistoryEntriesResponse.newBuilder().build();
	}
}
