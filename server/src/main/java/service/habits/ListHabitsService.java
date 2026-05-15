package service.habits;

import dataaccess.habits.*;
import dataaccess.exception.*;

import model.jooq.habits.tables.pojos.*;

import java.util.ArrayList;
import java.util.List;

import api.habits.*;

public class ListHabitsService {
	//
	// ==================== CONSTRUCTORS =====================
	//
	
	private HabitsDAO habitsDAO;

	public ListHabitsService(HabitsDAO habitsDAO) {
		this.habitsDAO = habitsDAO;
	}
	
	//
	// ===================== API TRANSLATION METHODS ==============
	//
	
	/**
	 * Generates an API Habit entry from a given DAO habit 
	 * representation
	 *
	 * @param habit The habit to translate
	 *
	 * @return the API version of the habit
	 */
	private HabitEntry generateHabitEntry(HabitsCatalog habit) {
		return HabitEntry.newBuilder()
			.setHabitId(habit.getHabitId().longValue())
			.setName(habit.getName())
			.setDescription(habit.getDescription())
			.setActive(habit.getActive())
			.build();
	}
	
	/**
	 * Takes in an API request to fetch the entire database
	 * catalog and translates it to a format the DAO level
	 * can understand. 
	 *
	 * @param request The request to get the habit catalog
	 *
	 * @return The habit catalog returned from DAO level wrapped in API response
	 */
	public GetHabitCatalogResponse getHabitCatalog(GetHabitCatalogRequest request) throws DataAccessException {
		List<HabitEntry> habitEntries = new ArrayList<>();
		this.habitsDAO.getHabitCatalog().forEach(
			habit -> habitEntries.add(generateHabitEntry(habit))
		);

		GetHabitCatalogResponse response = GetHabitCatalogResponse.newBuilder()
			.addAllHabitDataList(habitEntries)
			.build();

		return response;
	}

}
