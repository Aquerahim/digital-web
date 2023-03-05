package com.phoenixacces.apps.swagger;

import com.google.common.collect.Lists;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableSwagger2
@ComponentScan(basePackages = {"com.phoenixacces.apps.controller"})
public class SwaggerConfiguration {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.phoenixacces.apps.controller"))
                .paths(PathSelectors.any())
                .build()
                .enable(true)
                .apiInfo(getApiInfo())
                .securityContexts(Lists.newArrayList(securityContext()))
                /*.securitySchemes(Lists.newArrayList(apiKey()))*/;
    }

    private ApiInfo getApiInfo() {
        return new ApiInfo(
                "Phoenix Acces Ltd",
                "API GateWay Di-Gital Web",
                "0.0.1-SNAPSHOT",
                "Terms of service",
                new Contact("Support Phoenix Acces", "", "support@phoenixacces.com"),
                "Apache License Version 2.0",
                "https://www.apache.org/licenses/LICENSE-2.0",
                Collections.<VendorExtension>emptyList()
        );
    }

    List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Lists.newArrayList(new SecurityReference("Authorization", authorizationScopes));
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.regex("/anyPath.*"))
                .build();
    }

    /*private ApiKey apiKey() {
        return new ApiKey("Authorization", "Authorization", "header");
    }

    @Bean
    SecurityConfiguration security() {
        return new SecurityConfiguration(
                null,
                null,
                null, // realm Needed for authenticate button to work
                null, // appName Needed for authenticate button to work
                "Bearer ",// apiKeyValue
                ApiKeyVehicle.HEADER,
                "Authorization", //apiKeyName
                null);
    }*/
}
