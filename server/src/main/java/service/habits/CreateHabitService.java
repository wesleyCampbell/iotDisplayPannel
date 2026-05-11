package service.habits;

import dataaccess.habits.*;
import dataaccess.exception.*;

import api.habits.*;

public class CreateHabitService {
	//
	// ========================= CONSTRUCTORS ===================
	//
	
	private HabitsDAO habitsDAO;

	public CreateHabitService(HabitsDAO habitsDAO) {
		this.habitsDAO = habitsDAO;
	}

	//
	// ======================== MEMBER METHODS ==================
	//
	
	 public CreateHabitEntryResponse createGame(CreateHabitEntryRequest request) throws DataAccessException {
		String name = request.getName();
		String description = request.getDescription();
		boolean active = request.getActive();

		this.habitsDAO.insertHabit(name, description, active);

		return CreateHabitEntryResponse.newBuilder().build();
	}
}
