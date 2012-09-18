package org.egovframe.egovcap.executor;

import javax.sql.DataSource;


public interface ScriptExecutor {
	void executeScript(DataSource dataSource, PopulateCallback callback);
}
