package org.egovframe.egovcap.config.annotation;

import static org.springframework.util.StringUtils.hasText;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.cloudfoundry.runtime.service.relational.CloudDataSourceFactory;
import org.egovframe.egovcap.DbInitializer;
import org.egovframe.egovcap.ScriptInfo;
import org.egovframe.egovcap.config.annotation.EgovCapDataSource.Script;
import org.egovframe.egovcap.executor.DefaultExecutor;
import org.egovframe.egovcap.executor.ScriptExecutor;
import org.egovframe.egovcap.executor.SqlExecutor;
import org.egovframe.egovcap.executor.TableExecutor;
import org.egovframe.egovcap.executor.VersionExecutor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultBeanNameGenerator;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

public class EgovCapBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
	private static final String DEFAULT_BEAN_ID = "dataSource";
	private static final String LOCATION_ATTRIBUTE = "location";
	private static final String TABLE_ATTRIBUTE = "table";
	private static final String VERSION_ATTRIBUTE = "version";
	private static final String SQL_ATTRIBUTE = "sql";
	
	@Override
	public void registerBeanDefinitions(AnnotationMetadata metaData,	BeanDefinitionRegistry registry) {
		BeanDefinitionBuilder cdsfBdr = BeanDefinitionBuilder.genericBeanDefinition(CloudDataSourceFactory.class);
		Map<String, Object> attrs = metaData.getAnnotationAttributes(EgovCapDataSource.class.getName());
		String serviceName = (String) attrs.get("serviceName");
		if (StringUtils.hasText(serviceName)) cdsfBdr.addPropertyValue("serviceName", serviceName);
		BeanDefinition cdsfDef = cdsfBdr.getBeanDefinition();
		String id = (String) attrs.get("id");
		registry.registerBeanDefinition(StringUtils.hasText(id) ? id : DEFAULT_BEAN_ID, cdsfDef);
		
		// DbInitializer bean
		BeanDefinitionBuilder diBdr = BeanDefinitionBuilder.genericBeanDefinition(DbInitializer.class);
		ResourceLoader loader = new DefaultResourceLoader();
		
		AnnotationAttributes[] scriptAttrs = (AnnotationAttributes[]) attrs.get("scripts");
		List<ScriptInfo> scripts = new ArrayList<ScriptInfo>();
		for(AnnotationAttributes sattrs : scriptAttrs) {
			ScriptInfo scriptInfo = new ScriptInfo();
			scriptInfo.setResource(loader.getResource((String)sattrs.get("location")));
			scriptInfo.setExecutor(selectExecutor(sattrs));
			scripts.add(scriptInfo);
		}
		diBdr.addPropertyValue("scripts", scripts);
		BeanDefinition diDef = diBdr.getBeanDefinition();
		BeanNameGenerator beanNameGenerator = new DefaultBeanNameGenerator();
		registry.registerBeanDefinition(beanNameGenerator.generateBeanName(diDef, registry), diDef);
	}

	private ScriptExecutor selectExecutor(AnnotationAttributes sattrs) {
		String table = (String) sattrs.get(TABLE_ATTRIBUTE);
		String sql = (String) sattrs.get(SQL_ATTRIBUTE);
		int version = (Integer) sattrs.get(VERSION_ATTRIBUTE);
		
		if ((hasText(table) ? 1 : 0) + 
			(hasText(sql) ? 1 : 0) + 
			(version > 0 ? 1 : 0) > 1) {
			throw new IllegalArgumentException(TABLE_ATTRIBUTE + ", " + VERSION_ATTRIBUTE + ", " +
					SQL_ATTRIBUTE + " 중에서 하나만 지정할 수 있습니다.");
		}
				
		if (hasText(table)) {
			return new TableExecutor(table);
		}
		else if (version > 0) {
			return new VersionExecutor(version);
		}
		else if (hasText(sql)) {
			return new SqlExecutor(sql);
		}
		else {
			return new DefaultExecutor();
		}
	}
}
