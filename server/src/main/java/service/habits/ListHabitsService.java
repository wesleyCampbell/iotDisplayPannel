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

	/**
	 * Takes in an API request to fetch a specific habit entry
	 * and translates it to a format the DAO level
	 * can understand.
	 *
	 * @param request the API request to get the habit entry
	 * 
	 * @return API response containing the requested habit entry
	 */
	public GetHabitEntryResponse getHabitEntry(GetHabitEntryRequest request) throws DataAccessException {
		long habitId = request.getHabitId();

		HabitsCatalog habit = this.habitsDAO.getHabit(habitId);

		HabitEntry entry = generateHabitEntry(habit);

		return GetHabitEntryResponse.newBuilder()
			.setHabitData(entry)
			.build();
	}

	public GetActiveHabitsResponse getActiveHabits(GetActiveHabitsRequest request) throws DataAccessException {
		List<HabitEntry> habitEntries = new ArrayList<>();
		this.habitsDAO.getActiveHabits().forEach(
			habit -> habitEntries.add(generateHabitEntry(habit))
		);

		GetActiveHabitsResponse response = GetActiveHabitsResponse.newBuilder()
			.addAllHabitDataList(habitEntries)
			.build();

		return response;
	}

	public GetInactiveHabitsResponse getInactiveHabits(GetInactiveHabitsRequest request) throws DataAccessException {
		List<HabitEntry> habitEntries = new ArrayList<>();
		this.habitsDAO.getInactiveHabits().forEach(
			habit -> habitEntries.add(generateHabitEntry(habit))
		);

		GetInactiveHabitsResponse response = GetInactiveHabitsResponse.newBuilder()
			.addAllHabitDataList(habitEntries)
			.build();

		return response;
	}
}
