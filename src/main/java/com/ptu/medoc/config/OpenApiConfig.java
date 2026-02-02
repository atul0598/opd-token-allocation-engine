package com.ptu.medoc.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI opdTokenOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("OPD Token Allocation API")
                        .description("API documentation for OPD Token Allocation Engine")
                        .version("1.0.0"));
    }

    @Bean
    public GroupedOpenApi tokenApis() {
        return GroupedOpenApi.builder()
                .group("opd-tokens")
                .pathsToMatch("/api/v1/**")
                .build();
    }
}
