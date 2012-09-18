package org.egovframe.egovcap.executor;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractExecutor implements ScriptExecutor {
	private Log logger = LogFactory.getLog(getClass());
	
	@Override
	public final void executeScript(DataSource dataSource, PopulateCallback callback) {
		if (canRunScript(dataSource)) callback.populateScripts();
		else logger.debug("스크립트 실행 생략");
	}

	protected abstract boolean canRunScript(DataSource dataSource);
}
