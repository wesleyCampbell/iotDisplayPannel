package service.habits;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import api.habits.*;

import dataaccess.exception.*;
import dataaccess.habits.*;
import model.jooq.habits.tables.pojos.HabitsStats;

public class GetHabitHistoryService {
	//
	// ========================= CONSTRUCTORS =====================
	//
	
	private StatsDAO statsDAO;

	public GetHabitHistoryService(StatsDAO statsDAO) {
		this.statsDAO = statsDAO;
	}

	//
	// ========================== DATA CALCULATION METHODS ===========================
	//

	/**
	 * Given two dates, this method will calculate the current streak between them.
	 * It calculates the daily streak, meaning that it will compare the last completed
	 * day to the current date. If there is more than one day difference between the last
	 * completed day and the current day, will return 0.
	 * Additionally, if either lastFail or lastComplete are null, will return -1.
	 *
	 * @param lastFail The last failure date
	 * @param lastComplete The last completed date
	 *
	 * @return the length of the daily streak. 
	 */
	private int calculateDayStreak(LocalDate lastFail, LocalDate lastComplete) {
		// If the lastFail or lastComplete objects are null, 
		// there is not enough history to calculate the streak.
		if (lastFail == null || lastComplete == null) {
			return 0;
		}

		LocalDate today = LocalDate.now();
		// If the last completion was not yesterday or today, there is no current streak
		if (ChronoUnit.DAYS.between(lastComplete, today) > 1) {
			return 0;
		}

		// casting down from long->int probably won't result in a bug. 
		// At least not for ~6 million years, at which point I probably won't
		// need to track my daily pushups. 
		return (int)ChronoUnit.DAYS.between(lastFail, lastComplete);
	}

	/**
	 * Given a Stats object, will extract all the streak statistics
	 * and will package them into a HabitStreak protobuf object.
	 *
	 * @param stats The HabitsStats object to read
	 *
	 * @return The HabitStreak data from the HabitsStats
	 */
	private HabitStreak extractStreakData(HabitsStats stats) {
		// Calculate the day streak stats
		LocalDate lastFailDate = stats.getLastFailureDate();
		LocalDate lastCompleteDate = stats.getLastCompletedDate();
		int currentDayStreak = calculateDayStreak(lastFailDate, lastCompleteDate);
		int longestDayStreak = stats.getLongestDayStreak();

		// extract the goal streak stats
		int currentGoalStreak = stats.getCurrentGoalStreak();
		int longestGoalStreak = stats.getLongestGoalStreak();

		long habitId = stats.getHabitId().longValue();

		return HabitStreak.newBuilder()
			.setHabitId(habitId)
			.setCurrentDayStreak(currentDayStreak)
			.setLongestDayStreak(longestDayStreak)
			.setCurrentGoalStreak(currentGoalStreak)
			.setLongestGoalStreak(longestGoalStreak)
			.build();
	}

	/**
	 * Given a Stats object, this method will extract all the goal statistics
	 * and will package them into a HabitGoal protobuf object
	 *
	 * @param stats The HabitsStats object to read
	 *
	 * @return The HabitGoal data
	 */
	private HabitGoal extractGoalData(HabitsStats stats) {
		long habitId = stats.getHabitId().longValue();

		GOAL_TYPE goalType;
		switch (stats.getGoalType()) {
			case Daily:
				goalType = GOAL_TYPE.GOAL_TYPE_DAILY;
				break;
			case Weekly:
				goalType = GOAL_TYPE.GOAL_TYPE_WEEKLY;
				break;
			case Monthly:
				goalType = GOAL_TYPE.GOAL_TYPE_MONTHLY;
				break;
			case Yearly:
				goalType = GOAL_TYPE.GOAL_TYPE_YEARLY;
				break;
			default:
				goalType = GOAL_TYPE.GOAL_TYPE_UNSPECIFIED;
		}

		int goalTarget = stats.getGoalTarget();

		return HabitGoal.newBuilder()
			.setHabitId(habitId)
			.setGoalType(goalType)
			.setGoalTarget(goalTarget)
			.build();
	}	

	//
	// ========================== API TRANSLATION METHODS ===================
	//
	
	/**
	 * Fetches the requested HabitStats data from the database and compiles it into the correct
	 * API response.
	 *
	 * @param request The Get Habit Stat Request object
	 *
	 * @return The queried data.
	 */
	public GetHabitStatResponse getHabitStats(GetHabitStatRequest request) throws DataAccessException {
		long habitId = request.getHabitId();

		HabitsStats stats = this.statsDAO.getHabitStats(habitId);

		HabitStreak streak = this.extractStreakData(stats);
		HabitGoal goal = this.extractGoalData(stats);
		
		HabitStatVerbose statsAPI = HabitStatVerbose.newBuilder()
			.setHabitId(habitId)
			.setGoal(goal)
			.setStreak(streak)
			.build();
		
		return GetHabitStatResponse.newBuilder()
			.setStats(statsAPI)
			.build();
	}

	/**
	 * Fetches the goal data related to a specific habit id
	 *
	 * @param request The request to get the habit goal data
	 *
	 * @return The habit goal data
	 */
	public GetHabitGoalResponse getHabitGoal(GetHabitGoalRequest request) throws DataAccessException {
		long habitId = request.getHabitId();

		HabitsStats stats = this.statsDAO.getHabitStats(habitId);

		HabitGoal goal = this.extractGoalData(stats);

		return GetHabitGoalResponse.newBuilder()
			.setGoalStats(goal)
			.build();
	}

	/**
	 * Fetches goal streak data coorelating to a specific habit id
	 *
	 * @param request The request to get the habit streak data
	 *
	 * @return The habit streak data
	 */
	public GetHabitStreakResponse getHabitStreak(GetHabitStreakRequest request) throws DataAccessException {
		long habitId = request.getHabitId();

		HabitsStats stats = this.statsDAO.getHabitStats(habitId);

		HabitStreak streak = this.extractStreakData(stats);

		return GetHabitStreakResponse.newBuilder()
			.setStreakStats(streak)
			.build();
	}
}
