package org.egovframe.egovcap.config.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(EgovCapBeanDefinitionRegistrar.class)
public @interface EgovCapDataSource {
	String id() default "dataSource"; 

	String serviceName() default "";

	Script[] scripts() default {};

	@Retention(RetentionPolicy.RUNTIME)
	@Target({})
	public @interface Script {
		String location();

		String sql() default "";

		String table() default "";

		int version() default 0;
	}
}
