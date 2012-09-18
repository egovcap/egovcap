package org.egovframe.egovcap.config.xml;

import java.util.ArrayList;
import java.util.List;

import org.cloudfoundry.runtime.service.relational.CloudDataSourceFactory;
import org.egovframe.egovcap.DbInitializer;
import org.egovframe.egovcap.ScriptInfo;
import org.egovframe.egovcap.executor.DefaultExecutor;
import org.egovframe.egovcap.executor.ScriptExecutor;
import org.egovframe.egovcap.executor.SqlExecutor;
import org.egovframe.egovcap.executor.TableExecutor;
import org.egovframe.egovcap.executor.VersionExecutor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

/**
 * @author Toby Lee
 *
 */
public class EgovCapDataSourceDefinitionParser implements BeanDefinitionParser {
	private static final String LOCATION_ATTRIBUTE = "location";
	private static final String SCRIPT_TAG_NAME = "script";
	private static final String SERVICE_NAME_ATTRIBUTE = "service-name";
	private static final String TABLE_ATTRIBUTE = "table";
	private static final String VERSION_ATTRIBUTE = "version";
	private static final String DEFAULT_BEAN_ID = "dataSource";
	private static final String SQL_ATTRIBUTE = "sql";

	public BeanDefinition parse(Element element, ParserContext parserContext) {
		// DataSource bean
		BeanDefinitionBuilder cdsfBdr = BeanDefinitionBuilder.genericBeanDefinition(CloudDataSourceFactory.class);
		String serviceName = element.getAttribute(SERVICE_NAME_ATTRIBUTE);
		if (StringUtils.hasText(serviceName)) cdsfBdr.addPropertyValue("serviceName", serviceName);
		BeanDefinition cdsfDef = cdsfBdr.getBeanDefinition();
		parserContext.getRegistry().registerBeanDefinition(resolveId(element, parserContext, cdsfDef), cdsfDef);
		
		// DbInitializer bean
		BeanDefinitionBuilder diBdr = BeanDefinitionBuilder.genericBeanDefinition(DbInitializer.class);
		ResourceLoader loader = new DefaultResourceLoader();
		List<Element> scriptElements = DomUtils.getChildElementsByTagName(element, SCRIPT_TAG_NAME);
		List<ScriptInfo> scripts = new ArrayList<ScriptInfo>();
		for(Element e : scriptElements) {
			ScriptInfo script = new ScriptInfo();
			script.setResource(loader.getResource(e.getAttribute(LOCATION_ATTRIBUTE)));
			script.setExecutor(selectExecutor(e));
			scripts.add(script);
		}
		diBdr.addPropertyValue("scripts", scripts);
		BeanDefinition diDef = diBdr.getBeanDefinition();
		parserContext.getRegistry().registerBeanDefinition(parserContext.getReaderContext().generateBeanName(diDef), diDef);
	
		return null;
	}

	private ScriptExecutor selectExecutor(Element e) {
		if ((e.hasAttribute(TABLE_ATTRIBUTE) ? 1 : 0) + 
			(e.hasAttribute(VERSION_ATTRIBUTE) ? 1 : 0) + 
			(e.hasAttribute(SQL_ATTRIBUTE) ? 1 : 0) > 1) {
			throw new IllegalArgumentException(TABLE_ATTRIBUTE + ", " + VERSION_ATTRIBUTE + ", " +
					SQL_ATTRIBUTE + " 중에서 하나만 지정할 수 있습니다.");
		}
				
		if (e.hasAttribute(TABLE_ATTRIBUTE)) {
			return new TableExecutor(e.getAttribute(TABLE_ATTRIBUTE));
		}
		else if (e.hasAttribute(VERSION_ATTRIBUTE)) {
			return new VersionExecutor(Integer.parseInt(e.getAttribute(VERSION_ATTRIBUTE)));
		}
		else if (e.hasAttribute(SQL_ATTRIBUTE)) {
			return new SqlExecutor(e.getAttribute(SQL_ATTRIBUTE));
		}
		else {
			return new DefaultExecutor();
		}
	}

	private String resolveId(Element element, ParserContext parserContext, BeanDefinition def) {
		String id = element.getAttribute("id");
		if (StringUtils.hasText(id)) 
			return id;
		else 
			return DEFAULT_BEAN_ID;
	}
}
