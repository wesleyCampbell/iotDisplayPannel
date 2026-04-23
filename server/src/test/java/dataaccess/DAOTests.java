package dataaccess;

import org.junit.jupiter.api.*;

import org.jooq.*;
import org.jooq.impl.*;
import static org.jooq.impl.DSL.*;
import static jooq.habits.Tables.*;

import java.sql.*;

import dataaccess.exception.*;

public abstract class DAOTests {
	protected static void clearTable(DatabaseManager dbManager, Table<?> table) throws DataAccessException {
		try (Connection conn = dbManager.getConn()) {
			DSLContext ctx = DSL.using(conn, SQLDialect.MYSQL);
			ctx.deleteFrom(table).execute();
		} catch (SQLException ex) {
			throw new DataAccessException(ex.getMessage(), ex);
		}
	}
}
