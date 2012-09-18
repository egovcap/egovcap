package org.egovframe.egovcap.executor;

import javax.sql.DataSource;

import org.egovframe.egovcap.support.DatabaseUtils;

public class TableExecutor extends AbstractExecutor {
	private String table;
	
	public TableExecutor(String table) {
		this.table = table;
	}

	@Override
	protected boolean canRunScript(DataSource dataSource) {
		return !DatabaseUtils.tableExists(table, dataSource);
	}
}
