package dataaccess;

import dataaccess.exception.*;

import java.sql.*;
import static java.sql.Statement.RETURN_GENERATED_KEYS;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.google.gson.*;

import org.jooq.*;
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
		} catch (SQLException ex) {
			throw new DataAccessException(ex.getMessage(), ex);
		}
	}
}
