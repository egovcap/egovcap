package org.egovframe.egovcap.executor;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.egovframe.egovcap.support.DatabaseUtils;
import org.springframework.dao.EmptyResultDataAccessException;

public class VersionExecutor extends AbstractExecutor {
	private Log logger = LogFactory.getLog(getClass());
	private static final String VERSION_TABLE_NAME = "EGOV_CAP_VERSION";
	
	private int version;
	
	public VersionExecutor(int version) {
		this.version = version;
	}

	@Override
	protected boolean canRunScript(DataSource dataSource) {
		checkVersionTable(dataSource);
		int currentVersion = currentVersion(dataSource);
		if (currentVersion < this.version) {
			updateVersion(this.version, dataSource);
			return true;
		}
		else 
			return false;
	}

	private void updateVersion(int newVersion, DataSource dataSource) {
		logger.debug("버전 업데이트 : " + newVersion);
		DatabaseUtils.execute("update " + VERSION_TABLE_NAME + 
				" set version = " + newVersion, dataSource);
	}

	private int currentVersion(DataSource dataSource) {
		try {
			return DatabaseUtils.readIntValue(
				"select version from " + VERSION_TABLE_NAME, dataSource);
		}
		catch(EmptyResultDataAccessException e) {
			DatabaseUtils.execute("insert into " + VERSION_TABLE_NAME + 
					" values(0)", dataSource);
			return 0;
		}
	}

	private void checkVersionTable(DataSource dataSource) {
		if (DatabaseUtils.tableExists(VERSION_TABLE_NAME, dataSource)) return;
		
		DatabaseUtils.execute("create table " + VERSION_TABLE_NAME + " (" +
				"version int)", dataSource);
		logger.debug(VERSION_TABLE_NAME + " 생성");
	}
}
