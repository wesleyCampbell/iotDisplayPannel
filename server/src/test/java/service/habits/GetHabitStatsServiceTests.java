package service.habits;

import dataaccess.habits.*;
import dataaccess.exception.*;

import api.habits.*;
import model.jooq.habits.enums.HabitsStatsGoalType;
import model.jooq.habits.tables.pojos.*;

import org.jooq.types.ULong;
import org.junit.jupiter.api.*;

import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class GetHabitStatsServiceTests {
	//
	// ======================== GLOBALS =======================
	//
	
	private static final int HABIT_NUM_FULL = 3;
	private static final int HABIT_NUM_NULL = 3;

	private static final LocalDate[] HABIT_NULL_FAILS = {
		null,
		LocalDate.now().minusDays(4),
		null
	};

	private static final LocalDate[] HABIT_NULL_COMPS = {
		LocalDate.now(),
		null,
		null
	};

	private static final HabitsStatsGoalType[] GOAL_TYPES = {
		HabitsStatsGoalType.Weekly,
		HabitsStatsGoalType.Yearly,
		HabitsStatsGoalType.Daily
	};
	
	//
	// ======================== TEST INITIALIZATION ============	
	//
	
	private static StatsDAO statsDAO;
	private static GetHabitStatsService service;
	private static List<HabitsStats> stats = new ArrayList<>();
	private static List<Integer> currentStreaks = new ArrayList<>();

	/**
	 * Initializes the DAO and service objects.
	 * Additionally, prepares all of the mockito test logic
	 */
	@BeforeAll
	public static void initTests() throws DataAccessException {
		statsDAO = mock(StatsDAO.class);
		service = new GetHabitStatsService(statsDAO);
		LocalDate today = LocalDate.now();

		// Prepares mock StatsDAO to return the correct
		// mock data when requested
		for (int i = 0; i < HABIT_NUM_FULL; i++) {
			int dayOffset = (i + 1) * 2;
			// make the last completed date today or yesterday
			int dayModifier = i % 2 == 0 ? 1 : 0;
			LocalDate completionDate = today.minusDays(dayModifier);

			HabitsStats stat = new HabitsStats(
				ULong.valueOf(i + 1), 			  // habit id
				today.minusDays(dayOffset),  	  // Last failed date
				completionDate,  	  			  // Last completed date
				i * 3 + 10,     	  			  // Longest day streak (arbitrary)
				GOAL_TYPES[i],  	  			  // Goal type
				i * 4 + 11,   		  			  // Goal target (arbitrary)
				i * 3 + 8,      	  			  // Longest goal streak (arbitrary)
				i * 3 + 3,			  			  // Current goal streak (arbitrary)
				LocalDateTime.now()   			  // Modification timestamp (arbitrary)
			);

			stats.add(stat);
			currentStreaks.add(dayOffset - dayModifier);  // -1 because it does not count fail date

			when(statsDAO.getHabitStats(i + 1)).thenReturn(stats.get(i));
		}

		for (int i = 0; i < HABIT_NUM_NULL; i++) {
			int id = HABIT_NUM_FULL + i + 1;
			LocalDate failDate = HABIT_NULL_FAILS[i];
			LocalDate compDate = HABIT_NULL_COMPS[i];

			stats.add(new HabitsStats(
				ULong.valueOf(id),
				failDate,
				compDate,
				34,
				GOAL_TYPES[0],
				32,
				42,
				31,
				LocalDateTime.now()
			));
			currentStreaks.add(0);

			when(statsDAO.getHabitStats(id)).thenReturn(stats.get(id - 1));
		}
	}
	
	//
	// ====================== UNIT TESTS =====================
	//
	
	/**
	 * Helper function that determines the equality between an API 
	 * response for goal data and a full DAO object.
	 * If not equal, will throw an assertion error for junit.
	 *
	 * @param habitDAO The full DAO representation of the data
	 * @param goalData The stats data from the API
	 */
	private void isEqualGoal(HabitsStats habitDAO, HabitGoal goalData) {
		Assertions.assertEquals(
			habitDAO.getGoalTarget(),
			goalData.getGoalTarget()
		);

		// Assert goal type equality
		GOAL_TYPE goalType = goalData.getGoalType();
		GOAL_TYPE expectedGoalType;
		switch (habitDAO.getGoalType()) {
			case Yearly:
				expectedGoalType = GOAL_TYPE.GOAL_TYPE_YEARLY;
				break;
			case Monthly:
				expectedGoalType = GOAL_TYPE.GOAL_TYPE_MONTHLY;
				break;
			case Weekly:
				expectedGoalType = GOAL_TYPE.GOAL_TYPE_WEEKLY;
				break;
			case Daily:
				expectedGoalType = GOAL_TYPE.GOAL_TYPE_DAILY;
				break;
			default:
				expectedGoalType = GOAL_TYPE.GOAL_TYPE_UNSPECIFIED;
		}
		Assertions.assertEquals(expectedGoalType, goalType);
	}

	/**
	 * Helper function that determines the equality between an API
	 * response for Streak data and a full DAO object.
	 * If not equal, will throw an assertion error for junit.
	 *
	 * @param habitDAO The full DAO representation of the data
	 * @param streakData the streakd ata from the API
	 */
	private void isEqualStreak(HabitsStats habitDAO, HabitStreak streakData) {
		// Assert streak data equality
		Assertions.assertEquals(
				habitDAO.getCurrentGoalStreak(),
				streakData.getCurrentGoalStreak()
		);	
		Assertions.assertEquals(
				habitDAO.getLongestGoalStreak(),
				streakData.getLongestGoalStreak()
		);
		Assertions.assertEquals(
			habitDAO.getLongestDayStreak(),
			streakData.getLongestDayStreak()
		);
	}
	
	/**
	 * Helper function that determines equality between an API response
	 * and a DAO object. If not equal, will throw an assertion error.
	 *
	 * @param habitDAO The DAO representation of the data
	 * @param habitAPI the API representation of the data
	 */
	private void isEqualData(HabitsStats habitDAO, HabitStatVerbose habitAPI) {
		HabitGoal goalData = habitAPI.getGoal();
		HabitStreak streakData = habitAPI.getStreak();

		isEqualGoal(habitDAO, goalData);
		isEqualStreak(habitDAO, streakData);

	}
	
	/**
	 * Tests that the service fetches the correct data from the DAO level.
	 * Verifies that the calculations and logic are correct for streak
	 * calculation.
	 */
	@Test
	public void GetHabitStatsTest() throws DataAccessException {
		for (int i = 0; i < HABIT_NUM_FULL + HABIT_NUM_NULL; i++) {
			ULong id = ULong.valueOf(i + 1);

			GetHabitStatRequest request = GetHabitStatRequest.newBuilder()
				.setHabitId(id.longValue()).build();

			GetHabitStatResponse response = service.getHabitStats(request);

			HabitsStats stat = stats.get(i);
			HabitStatVerbose statAPI = response.getStats();
			
			isEqualData(stat, statAPI);

			// Test the streak calculation
			Assertions.assertEquals(
					currentStreaks.get(i),
					statAPI.getStreak().getCurrentDayStreak()
			);
		}
	}

	/**
	 * Tests that the service fetches the correct Goal data from the 
	 * DAO level.
	 */
	@Test 
	public void GetHabitGoalTest() throws DataAccessException {
		for (int i = 0; i < HABIT_NUM_FULL + HABIT_NUM_NULL; i++) {
			long id = i + 1;

			GetHabitGoalRequest request = GetHabitGoalRequest.newBuilder()
				.setHabitId(id).build();

			GetHabitGoalResponse response = service.getHabitGoal(request);

			HabitsStats stat = stats.get(i);
			HabitGoal goalStats = response.getGoalStats();

			isEqualGoal(stat, goalStats);
		}
	}

	/**
	 * Tests that the service fetches the correct Streak data from
	 * the DAO level.
	 */
	@Test
	public void getHabitStreakTest() throws DataAccessException {
		for (int i = 0; i < HABIT_NUM_FULL + HABIT_NUM_NULL; i++) {
			long id = i + 1;

			GetHabitStreakRequest request = GetHabitStreakRequest.newBuilder()
				.setHabitId(id).build();

			GetHabitStreakResponse response = service.getHabitStreak(request);

			HabitsStats stat = stats.get(i);
			HabitStreak streakStats = response.getStreakStats();

			isEqualStreak(stat, streakStats);
		}
	}
}
