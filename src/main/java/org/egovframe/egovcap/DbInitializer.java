package org.egovframe.egovcap;

import java.sql.SQLException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.egovframe.egovcap.executor.PopulateCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

public class DbInitializer {
	private final Log logger = LogFactory.getLog(getClass());
	
	private DataSource dataSource;
	private List<ScriptInfo> scripts;
	
	public void setScripts(List<ScriptInfo> scripts) {
		this.scripts = scripts;
	}

	@Autowired 
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@PostConstruct
	public void init() {
		for(final ScriptInfo script : scripts) {
			script.getExecutor().executeScript(this.dataSource, new PopulateCallback() {
				@Override
				public void populateScripts() {
					ResourceDatabasePopulator dp = new ResourceDatabasePopulator();
					dp.addScript(script.getResource());
					dp.setIgnoreFailedDrops(true);
					try {
						dp.populate(dataSource.getConnection());
					}
					catch(SQLException e) {
						logger.error(script.getResource().getFilename() + " 실행 실패 : " + e.getMessage(), e);
						throw new RuntimeException(e);
					}
					logger.info(script.getResource().getFilename() + " 실행 성공");
				}
			});
		}
	}
}
