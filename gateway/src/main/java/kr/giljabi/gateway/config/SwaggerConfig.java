package kr.giljabi.gateway.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class SwaggerConfig {
	
	@Bean
	  public OpenAPI openAPI(@Value("${springdoc.version}") String springdocVersion) {
	  	Info info = new Info()
				.title("GATEWAY-WEBSOCKET-NETTY API")
				.version(springdocVersion)
				.description("GATEWAY-WEBSOCKET-NETTY 상세소개");

	    Components bearerAuth = new Components().addSecuritySchemes(
				"bearerAuth"
				, new SecurityScheme().name("bearerAuth").type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")
		);

		OpenAPI openApi = new OpenAPI()
				.addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
				.components(bearerAuth)
				.info(info);

		return openApi;
	  }

}

