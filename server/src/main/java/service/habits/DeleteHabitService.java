package service.habits;

import api.habits.*;

import dataaccess.exception.*;
import dataaccess.habits.*;

public class DeleteHabitService {
	//
	// ===================== CONSTRUCTORS ================
	//
	
	private HabitsDAO habitsDAO;

	public DeleteHabitService(HabitsDAO habitsDAO) {
		this.habitsDAO = habitsDAO;
	}

	//
	// ====================== API TRANSLATION METHODS =================
	// 
	
	public DeleteHabitEntryResponse deleteHabit(DeleteHabitEntryRequest request) throws DataAccessException {
		long habitId = request.getHabitId();

		this.habitsDAO.deleteHabit(habitId);

		return DeleteHabitEntryResponse.newBuilder().build();
	}
}
