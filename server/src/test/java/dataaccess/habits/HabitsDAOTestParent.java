package dataaccess.habits;

import java.util.function.Consumer;

import java.sql.*;

import static model.jooq.habits.Tables.*;
import model.jooq.habits.tables.pojos.HabitsCatalog;

import org.jooq.*;
import org.jooq.impl.*;

import dataaccess.DAOTests;
import dataaccess.DatabaseManager;

public abstract class HabitsDAOTestParent extends DAOTests {
	//
	// ======================== GLOBALS ======================
	//
	
	protected static final int habitNumTrue = 3;
	protected static final int habitNumFalse = 2;

	protected static final String DB_NAME = "habits";

	// 
	// ========================== CONSTRUCTORS =========================
	//
	
	public HabitsDAOTestParent(Consumer<DatabaseManager> initFunction) {
		super(DB_NAME, initFunction);
	}

	//
	// ======================== SHARED METHODS ==================
	//
	
	/**
	 * Helper function that inserts n number of habits into the database
	 *
	 * @param dbManager The database manager to make the connection with
	 * @param num The number of habits to insert
	 * @param active Whether the habits should be active or inactive
	 */
	protected static void insertHabitsNum(DatabaseManager dbManager, int num, boolean active) {
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
	
	/**
	 * Wrapper function for `insertHabitsNum` method that uses default
	 * values. Used for the test database autoconfiguration function
	 * passed into the parent.
	 */
	protected static void initTestTables(DatabaseManager dbManager) {
		insertHabitsNum(dbManager, habitNumTrue, true);
		insertHabitsNum(dbManager, habitNumFalse, false);
	}
}
