package service.habits;

import dataaccess.habits.*;
import dataaccess.exception.*;

import api.habits.*;
import model.jooq.habits.tables.pojos.*;

import org.jooq.types.ULong;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MarkHabitCompletionService {
	//
	// ================== CONSTRUCTORS =================
	//
	private HistoryDAO historyDAO;

	public MarkHabitCompletionService(HistoryDAO historyDAO) {
		this.historyDAO = historyDAO;
	}
	
	//
	// ==================== API TRANSLATION METHODS =====================
	//
	
	public MarkHistoryCompletionStateResponse setHistoryCompletionState(
			MarkHistoryCompletionStateRequest request) throws DataAccessException {	
		long historyId = request.getHistoryId();
		boolean status = request.getStatus();

		this.historyDAO.setHistoryEntryCompleteState(historyId, status);

		return MarkHistoryCompletionStateResponse.newBuilder().build();
	}
}

