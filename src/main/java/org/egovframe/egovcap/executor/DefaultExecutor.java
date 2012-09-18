package org.egovframe.egovcap.executor;

import javax.sql.DataSource;

public class DefaultExecutor extends AbstractExecutor {
	@Override
	protected boolean canRunScript(DataSource dataSource) {
		return true;
	}
}
