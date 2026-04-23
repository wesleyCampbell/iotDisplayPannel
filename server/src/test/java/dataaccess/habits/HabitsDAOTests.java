package dataaccess.habits;

import org.junit.jupiter.api.*;

import org.jooq.*;
import org.jooq.impl.*;
import static org.jooq.impl.DSL.*;
import static jooq.habits.Tables.*;

import java.sql.*;

import dataaccess.DAOTests;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class HabitsDAOTests extends DAOTests {
	private static HabitsDAO habitsDAO;
	private static HabitsDatabaseManager dbManager;

	private static final int habitNum = 5;

	//
	// ================ TEST INITIALIZATION =================
	//
	
	@BeforeAll
	public static void initTests() {
		dbManager = Assertions.assertDoesNotThrow(() -> new HabitsDatabaseManager());	
		habitsDAO = dbManager.getHabitsDAO();

		try {
			clearTable(dbManager, HABITS_HISTORY);  // Necesary because of foreign constraints
			clearTable(dbManager, HABITS_CATALOG);
		} catch (Exception ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
		
		try (Connection conn = dbManager.getConn()) {
			DSLContext ctx = DSL.using(conn, SQLDialect.MYSQL);

			for (int i = 0; i < habitNum; i++) {
				ctx.insertInto(HABITS_CATALOG, HABITS_CATALOG.NAME, HABITS_CATALOG.DESCRIPTION)
					.values("habit" + Integer.toString(i), 
							"habit" + Integer.toString(i) + " description")
					.execute();
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	//
	// ========================== TESTS =============================
	//
	
	@Test
	@Order(1)
	public void selectOneHabitTest() {
		
	}
}
