/*
 * Copyright 2013-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cloud.context.scope.refresh;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.PropertyPlaceholderAutoConfiguration;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.context.environment.EnvironmentManager;
import org.springframework.cloud.context.scope.refresh.RefreshScopeConfigurationIntegrationTests.Application;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Dave Syer
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class)
public class RefreshScopeConfigurationIntegrationTests {

	@Autowired
	private org.springframework.cloud.context.scope.refresh.RefreshScope scope;

	@Autowired
	private EnvironmentManager environmentManager;

	@Autowired
	private Application application;

	@Autowired
	private ConfigurableListableBeanFactory beanFactory;

	@Test
	public void scopeOnBeanDefinition() throws Exception {
		assertEquals("refresh", beanFactory.getBeanDefinition("scopedTarget.application")
				.getScope());
	}

	/**
	 * See gh-43
	 */
	@Test
	@Ignore
	public void beanAccess() throws Exception {
		// Comment out this line and it works!
		application.hello();
		scope.refresh("application");
		String message = application.hello();
		assertEquals("Hello World", message);
	}

	@Configuration("application")
	// @Component("application")
	@RefreshScope
	@Import({ PropertyPlaceholderAutoConfiguration.class, RefreshAutoConfiguration.class })
	protected static class Application {

		String message = "Hello World";

		@RequestMapping("/")
		public String hello() {
			return message;
		}

		public static void main(String[] args) {
			SpringApplication.run(Application.class, args);
		}

	}

}
