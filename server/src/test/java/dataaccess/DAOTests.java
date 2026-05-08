package dataaccess;

import org.junit.jupiter.api.*;

import org.flywaydb.core.Flyway;

import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;
import static org.jooq.impl.DSL.*;

import java.sql.*;
import java.sql.Statement;

import java.util.function.Consumer;
import java.util.List;

import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import dataaccess.exception.*;
import dataaccess.DatabaseManager.DatabaseProperties;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Testcontainers
public abstract class DAOTests {
	//
	// ==================== GLOBALS =======================
	//
	
	protected static final String DB_FILEPATH = "filesystem:db/%s";
	protected static final String DB_NAME = "test_database";
	protected static final String DB_USER = "root";
	protected static final String DB_PASSWORD = "test";

	@Container
	protected static MariaDBContainer<?> db = 
		new MariaDBContainer<>("mariadb:11.3")
			.withDatabaseName(DB_NAME)
			.withUsername(DB_USER)
			.withPassword(DB_PASSWORD);

	//
	// ==================== CONSTRUCTORS ===================
	//
	
	protected DatabaseManager dbManager;
	protected DSLContext ctx;
	protected String databaseName;

	protected Consumer<DatabaseManager> dbOperator;

	protected DAOTests(String databaseName, Consumer<DatabaseManager> dbOperator) {
		this.databaseName = databaseName;
		this.dbOperator = dbOperator;
	}

	//
	// ======================= TEST SETUP/CLEANUP ======================
	//
	
	/**
	 * Spins up a container for the test database and sets up the
	 * DatabaseManager interface for it.
	 */
	@BeforeAll
	protected void testSuiteSetup() {
		// We need to initialize the database for each test suite.
		try (Connection conn = DriverManager.getConnection(
					db.getJdbcUrl(),
					db.getUsername(),
					db.getPassword())) 
		{
			Statement st = conn.createStatement();
			
			st.execute(String.format(
						"CREATE DATABASE IF NOT EXISTS %s", this.databaseName));
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}

		// Generate the database properties
		DatabaseProperties dbProps = new DatabaseProperties(
				this.databaseName,
				DB_USER,
				DB_PASSWORD,
				db.getJdbcUrl()
		);

		// Apply the database migrations and create the bridge
		this.dbManager = new DatabaseManager(dbProps);
		this.applyDatabaseMigrations(this.dbManager);
	}

	private Connection conn;

	/**
	 * Runs before each indivudial test.
	 * Initiallizes a Connection to the database and
	 * resets the database to an initial condition
	 */
	@BeforeEach
	protected void testSetup() {
		// Initialize the db connection
		try {
			conn = this.dbManager.getConn();
		} catch (DataAccessException ex) {
			throw new RuntimeException(ex);
		}	

		this.ctx = DSL.using(conn, SQLDialect.MARIADB);

		// Get the db ready for the test.
		this.resetDB();
		this.dbOperator.accept(this.dbManager);
	}

	/**
	 * Runs after each individual test.
	 * Closes the connection to the database.
	 */
	@AfterEach
	protected void closeConnection() {
		try {
			conn.close();
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	//
	// =========================== HELPER FUNCTIONS ===================
	//

	/**
	 * Resets all tables in the database to their initial conditions.
	 */
	protected void resetDB() {
		// Can't truncate tables when this is true
		this.ctx.execute("SET FOREIGN_KEY_CHECKS = 0");

		// Iterate through each table and truncate it
		ctx.fetch("SHOW TABLES")
			.forEach(r -> {
				String table = r.get(0, String.class);
				ctx.execute("TRUNCATE TABLE " + table);
			});

		// reset it to previous values
		this.ctx.execute("SET FOREIGN_KEY_CHECKS = 1");
	}

	/** 
	 * Applies the Flyway database migrations that the production database
	 * uses to the memory database
	 * 
	 * @param dbManager The DatabaseManager connected to the database
	 */
	protected void applyDatabaseMigrations(DatabaseManager dbManager) {
		// extract db properties
		DatabaseProperties props = dbManager.getProperties();
		String url = props.connectionUrl().replace(DB_NAME, this.databaseName);
		String user = props.username();
		String password = props.password();

		Flyway flyway = Flyway.configure()
						.dataSource(url, user, password)
						.locations(String.format(DB_FILEPATH, this.databaseName))
						.load();
		
		flyway.migrate();
	}

	/**
	 * Checks to see if a certain entry exists in a given table.
	 *
	 * @param table The table to check
	 * @param column The column to check by
	 * @param columnValue The the column value to check by
	 *
	 * @return true if the entry exists, false otherwise
	 */
	protected <R extends Record, T> boolean entryExists(
			Table<R> table,
			TableField<R, T> column,
			T columnValue) 
	{
		try (Connection conn = dbManager.getConn()) {
			DSLContext ctx = DSL.using(conn, SQLDialect.MARIADB);

			return ctx.fetchExists(
				DSL.selectOne()
					.from(table)
					.where(column.eq(columnValue))
			);
		} catch (DataAccessException|SQLException ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	/**
	 * Returns how many entries are in in a table.
	 *
	 * @param table The table to check
	 *
	 * @return the number of entries
	 */
	protected <R extends Record> int getTableLength(Table<R> table) {
		try (Connection conn = dbManager.getConn()) {
			DSLContext ctx = DSL.using(conn, SQLDialect.MARIADB);

			return ctx.fetchCount(
				table
			);
		} catch (DataAccessException|SQLException ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}

	/** 
	 * Returns how many entries match a given constraint in a table
	 *
	 * @param table The table to check
	 * @param column The column to check by
	 * @param columnValue The column value to check by
	 *
	 * @return the number of entries
	 */
	protected <R extends Record, T> int getEntryNum(
			Table<R> table,
			TableField<R, T> column,
			T columnValue)
	{
		try (Connection conn = dbManager.getConn()) {
			DSLContext ctx = DSL.using(conn, SQLDialect.MARIADB);

			return ctx.fetchCount(
				table
				.where(column.eq(columnValue))
			);
		} catch (Exception ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
	}	

	/**
	 * Returns every entry in a given table.
	 *
	 * @param table The table to get
	 * 
	 * @return A List containing every entry in the table
	 */
	protected <R extends Record> List<R> getTableEntries(Table<R> table) {
		try (Connection conn = dbManager.getConn()) {
			DSLContext ctx = DSL.using(conn, SQLDialect.MARIADB);

			return ctx.selectFrom(table).fetch();
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
}
