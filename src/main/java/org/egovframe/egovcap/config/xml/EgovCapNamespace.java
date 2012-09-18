package org.egovframe.egovcap.config.xml;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;


public class EgovCapNamespace extends NamespaceHandlerSupport {

	public void init() {
		registerBeanDefinitionParser("data-source", new EgovCapDataSourceDefinitionParser());
	}

}
