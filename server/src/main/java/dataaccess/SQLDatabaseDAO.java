package dataaccess;

import dataaccess.exception.*;

import java.sql.*;
import static java.sql.Statement.RETURN_GENERATED_KEYS;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.google.gson.*;

import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.*;
import static org.jooq.impl.DSL.*;

public abstract class SQLDatabaseDAO {
	//
	// =================================== CONSTRUCTORS ==========================
	//
	
	protected Gson gson = new GsonBuilder().create();

	protected DatabaseManager dbManager;

	protected SQLDatabaseDAO(DatabaseManager dbManager) throws DataAccessException {
		this.dbManager = dbManager;
	}

	/**
	 * Used to execute an arbitrary SQL statement on the database.
	 *
	 * @param block This is a function that represents a SQL statement. 
	 * It must use the jOOQ format to get a good result.
	 *
	 * @param The result of the statement.
	 */
	protected <T> T executeStatement(Function<DSLContext, T> block) throws DataAccessException {
		// Open the DB connection
		try (Connection conn = this.dbManager.getConn()) {
			// Make the DSL context and apply the statement operators
			DSLContext ctx = DSL.using(conn, SQLDialect.MARIADB);
			return block.apply(ctx);
		} catch (SQLIntegrityConstraintViolationException ex) {
			throw new ForeignConstraintException(ex.getMessage(), ex);
		} catch (SQLException ex) {
			throw new DataAccessException(ex.getMessage(), ex);
		}
	}

	/**
	 * Used to confirm that an entry exists in a more efficient manner.
	 *
	 * @param table The table to check
	 * @param column The column to check by
	 * @param columnValue The column value by which we are checking
	 *
	 * @return true if exists, false otherwise
	 */
	protected <R extends Record, T> boolean entryExists(
			Table<R> table,
			TableField<R, T> column,
			T columnValue) throws DataAccessException
	{
		return this.executeStatement(
			ctx -> ctx.fetchExists(
				DSL.selectFrom(table)
					.where(column.eq(columnValue))
			)
		);
	}
}
