package org.egovframe.egovcap;

import org.egovframe.egovcap.executor.ScriptExecutor;
import org.springframework.core.io.Resource;

public class ScriptInfo {
	private Resource resource;
	private ScriptExecutor executor;

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}

	public ScriptExecutor getExecutor() {
		return executor;
	}

	public void setExecutor(ScriptExecutor executor) {
		this.executor = executor;
	}
}
