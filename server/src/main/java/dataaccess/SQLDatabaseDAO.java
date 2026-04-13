package dataaccess;

import dataaccess.exception.*;

import java.sql.*;
import static java.sql.Statement.RETURN_GENERATED_KEYS;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.*;

public abstract class SQLDatabaseDAO {
	//
	// =================================== CONSTRUCTORS ==========================
	//
	
	protected Gson gson = new GsonBuilder().create();

	protected SQLDatabaseDAO(final String initStatement) throws DataAccessException {
		this.initializeDatabase(initStatement);
	}

	//
	// ===================================== DATABASE MANIPULATION ======================
	//
	
	/**
	 * Will take a string SQL statement that creates a database and will execute it.
	 *
	 * @param initStatement The DB create statement
	 */
	protected void initializeDatabase(final String initStatement) throws DataAccessException {
		// Open the SQL connection
		try (Connection conn = DatabaseManager.getConn()) {
			// Format the SQL Statement
			try (PreparedStatement ps = conn.prepareStatement(initStatement)) {
				ps.executeUpdate();
			}	
		} catch (Exception ex) {
			String err = String.format("Failed to execute creation statment `%s`.", initStatement);
			throw new DataAccessException(err, ex);
		}
	}

	/**
	 * Throws a DataAccessException stating that an invalid object type was attempted to be inserted into the database.
	 *
	 * @param type The invalid object type
	 */
	private <T> void throwUnsupportedDBType(Class<T> type) throws DataAccessException {
		String err = String.format("Unsupported database object type: %s", type.toString());
		throw new DataAccessException(err);
	}

	/**
	 * Helper function that formats an SQL statement string with an arbitrary number of parameters
	 *
	 * @param ps The preparedStatement to insert the parameters into
	 * @param statement The SQL statement
	 * @param params The array of parameters
	 */
	private void formatSQLStatement(PreparedStatement ps, String statement, Object[] params) throws SQLException, DataAccessException {
		for (int i = 0; i < params.length; i++) {
			Object param = params[i];
			switch (param) {
				case String s -> ps.setString(i + 1, s);
				case Integer n -> ps.setInt(i + 1, n);
				case Float n -> ps.setFloat(i + 1, n);
				default -> this.throwUnsupportedDBType(param.getClass());
			}
		}	
	}	

	/**
	 * Opens a DB connection to check if an element exists in the database
	 *
	 * @param checkStatement The string SQL statement, unformated
	 * @param params A set of object parameters to include in the checkStatement
	 *
	 * @return true if element exists in the DB, false otherwise
	 */
	protected boolean checkExists(String checkStatement, Object... params) throws DataAccessException {
		try (Connection conn = DatabaseManager.getConn()) {
			try (PreparedStatement ps = conn.prepareStatement(checkStatement)) {
				// Formats the create statement
				this.formatSQLStatement(ps, checkStatement, params);

				// Execute the create statement
				try (ResultSet rs = ps.executeQuery()) {
					boolean exists = rs.next();
					return exists;
				}
			}
		} catch (SQLException ex) {
			throw new DataAccessException("Failed to connect to DB", ex);
		}
	}

	/**
	 * Will execute an arbitrary SQL statement in the database.
	 * 
	 * @param statement The SQL statement to be executed
	 */
	protected void executeStatement(final String statement) throws DataAccessException {
		// Open the DB connection
		try (Connection conn = DatabaseManager.getConn()) {
			try (PreparedStatement ps = conn.prepareStatement(statement)) {
				ps.execute();
			}
		} catch (Exception ex) {
			String err = String.format("Failed to execute statement: %s", statement);
			throw new DataAccessException(err, ex);
		}
	}

	/**
	 * A functional interface that maps a SQL ResultSet row into a given object
	 * Used in the executeQuery() method
	 */
	@FunctionalInterface
	public interface RowMapper<T> {
		T mapRow(ResultSet rs) throws SQLException;
	}

	/**
	 * Executes a SQL query within the database with arbitrary parameters and return type.
	 *
	 * @param statement The SQL statement to execute
	 * @param mapper The function that maps the output of the DB query to a given object type
	 * @param params Arbitrary object parameters to pass into the statement
	 *
	 * @return A List of desired objects
	 */
	protected <T> List<T> executeQuery(final String statement, RowMapper<T> mapper, Object... params) throws DataAccessException {
		List<T> results = new ArrayList<>();

		// Open the DB connection
		try (Connection conn = DatabaseManager.getConn()) {
			// Format the SQL statement
			try (PreparedStatement ps = conn.prepareStatement(statement)) {
				this.formatSQLStatement(ps, statement, params);

				// Execute the query and put each match in the results array
				try (ResultSet rs = ps.executeQuery()) {
					while(rs.next()) {
						results.add(mapper.mapRow(rs));
					}
				}
				
				return results;
			}	
		} catch (SQLException ex) {
			String err = String.format("Error while executing query: %s", statement);
			throw new DataAccessException(err, ex);
		}
	}

	/**
	 * Executes a SQL update to the database.
	 *
	 * @param statement The SQL update statement to execute
	 * @param params Arbitrary object parameters to pass into the statement
	 *
	 * @return int status code
	 */
	protected int executeUpdate(final String statement, Object...params) throws DataAccessException {
		// Open the DB connection
		try (Connection conn = DatabaseManager.getConn()) {
			try (PreparedStatement ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
			this.formatSQLStatement(ps, statement, params);

			ps.executeUpdate();

			try (ResultSet rs = ps.getGeneratedKeys()) {
				if (rs.next()) {
					return rs.getInt(1);
				}
			}

			return 0;
			}
		} catch (Exception ex) {
			String err = String.format("Error executing update statement %s", statement);
			throw new DataAccessException(err, ex);
		}
	}
}
