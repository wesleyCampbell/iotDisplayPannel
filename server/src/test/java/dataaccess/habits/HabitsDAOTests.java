package dataaccess.habits;

import org.junit.jupiter.api.*;

import org.jooq.*;
import org.jooq.impl.*;
import static org.jooq.impl.DSL.*;
import static jooq.habits.Tables.*;

import java.sql.*;

import dataaccess.DAOTests;
import dataaccess.exception.*;

public class HabitsDAOTests extends DAOTests {
	//
	// ====================== GLOBALS =====================
	//
	
	private static final int habitNum = 5;
	private static final String DB_NAME = "habits";

	//
	// ======================= CONSTRUCTORS =====================
	//
	
	private HabitsDAO habitsDAO;

	public HabitsDAOTests() {
		super(DB_NAME, () -> System.out.println("<<<<<>>>>>>TEST"));
		
		try {
			this.habitsDAO = new HabitsDAO(this.dbManager);
		} catch (DataAccessException ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	//
	// ================ TEST INITIALIZATION =================
	//
	
	/**
	 * Helper function that inserts n number of habits into the database
	 *
	 * @param num The number of habits to insert
	 * @param active Whether the habits should be active or inactive
	 */
	private void insertHabitsNum(int num, boolean active) {
		try (Connection conn = dbManager.getConn()) {
			DSLContext ctx = DSL.using(conn, SQLDialect.MARIADB);

			for (int i = 0; i < num; i++) {
				String name = String.format("habit%s%d",
						active ? "Active" : "Inactive", i);
				String desc = String.format("%s description here", name);

				ctx.insertInto(HABITS_CATALOG,
						HABITS_CATALOG.NAME,
						HABITS_CATALOG.DESCRIPTION,
						HABITS_CATALOG.ACTIVE)
					.values(name, desc, active)
					.execute();
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	//
	// ========================== SELECTION TESTS =============================
	//
	
	@Test
	public void selectOneHabitTest_Correct() {
				
	}

	@Test
	public void selectOneHabitTest_Incorrect() {
				
	}

	@Test
	public void selectAllHabitsTest() {

	}

	@Test
	public void selectActiveHabitsTest() {

	}

	@Test
	public void selectInactiveHabitsTest() {

	}

	//
	// ========================== DELETION TESTS =============================
	//
	
	@Test
	public void deleteHabitTest() {

	}

	@Test
	public void clearHabitsTest() {

	}

	//
	// =========================== INSERTION TESTS ===========================
	//
	
	@Test
	public void insertHabitTest() {

	}
}
