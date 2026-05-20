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

public class UpdateHabitHistoryNotesService {
	//
	// ================= CONSTRUCTORS ================
	//
	
	private HistoryDAO historyDAO;

	public UpdateHabitHistoryNotesService(HistoryDAO historyDAO) {
		this.historyDAO = historyDAO;
	}

	//
	// ===================== API TRANSLATION METHODS ==================
	//
	
	public UpdateHabitHistoryNotesResponse updateHabitHistoryNotes(UpdateHabitHistoryNotesRequest request) throws DataAccessException {
		long historyId = request.getHistoryId();
		String notes = request.getNewNotes();

		this.historyDAO.setHistoryEntryNotes(historyId, notes);

		return UpdateHabitHistoryNotesResponse.newBuilder().build();
	}
}
