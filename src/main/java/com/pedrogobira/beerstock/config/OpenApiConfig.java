package com.pedrogobira.beerstock.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI(@Value("${application.name}") String name,
                                 @Value("${application.description}") String description,
                                 @Value("${application.version}") String version) {
        return new OpenAPI().info(new Info()
                .title(name)
                .description(description)
                .version(version)
                .license(new License().name("GPLv3").url("https://www.gnu.org/licenses/gpl-3.0.txt")));
    }
}
