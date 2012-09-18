package org.egovframe.egovcap.executor;

import javax.sql.DataSource;

import org.egovframe.egovcap.support.DatabaseUtils;

public class SqlExecutor extends AbstractExecutor {
	private String sql;
	
	public SqlExecutor(String sql) {
		this.sql = sql;
	}

	@Override
	protected boolean canRunScript(DataSource dataSource) {
		return !DatabaseUtils.hasResult(sql, dataSource);
	}
}
