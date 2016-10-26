package org.ngrinder.infra.config;

import net.grinder.engine.agent.LocalScriptTestDriveService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ExternalBeanConfig {

	@Bean
	public LocalScriptTestDriveService localScriptTestDriveService() {
		return new LocalScriptTestDriveService();
	}

}
