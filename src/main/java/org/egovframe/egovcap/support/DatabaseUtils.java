package org.egovframe.egovcap.support;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

public class DatabaseUtils {
	public static boolean tableExists(String tableName, DataSource dataSource) {
		Connection c = null;
		ResultSet rs = null;
		
		try {
			c = dataSource.getConnection();
			rs = c.getMetaData().getTables(null, null, tableName, null);
			if (rs.next()) return true;
			else return false;
		}
		catch(SQLException e) {
			throw new RuntimeException(e);
		}
		finally {
			if (rs != null) try { rs.close(); } catch(SQLException e) {}
			if (c != null) try { c.close(); } catch(SQLException e) {}
		}
	}

	public static boolean hasResult(String sql, DataSource dataSource) {
		JdbcTemplate jt = new JdbcTemplate(dataSource);
		
		return jt.query(sql, new ResultSetExtractor<Boolean>() {
			@Override
			public Boolean extractData(ResultSet rs) throws SQLException, DataAccessException {
				if (rs.next()) return true;
				else return false;
			}
		});
	}

	public static void execute(String sql, DataSource dataSource) {
		JdbcTemplate jt = new JdbcTemplate(dataSource);

		jt.execute(sql);
	}

	public static int readIntValue(String sql, DataSource dataSource) {
		JdbcTemplate jt = new JdbcTemplate(dataSource);
		
		return jt.queryForInt(sql);
	}
}
