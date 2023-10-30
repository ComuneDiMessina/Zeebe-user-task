package it.almaviva.eai.zeebe.usertask.swagger;

import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    public static final String AUTHORIZATION_HEADER = "X-Auth-Token";

    @Bean
    public Docket api() {
        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .forCodeGeneration(true)
                .securityContexts(Lists.newArrayList(securityContext()))
                .securitySchemes(Lists.newArrayList(apiKey()));

        docket = docket.select()
                .apis(RequestHandlerSelectors.basePackage("it.almaviva.eai.zeebe.usertask.controller"))
                .paths(PathSelectors.any())
                .paths(Predicates.not(PathSelectors.regex("/error.*")))
                .build() .protocols(Sets.newHashSet("http", "https"));

        return docket;
    }

    ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("ZeebeUserTaskAPI")
                .description("A Zeebe worker to manage manual/user tasks in a workflow. It shows all jobs of type user as a task/todo-list. A user can complete the tasks with requested data")
                .version(SwaggerConfig.class.getPackage().getImplementationVersion())
                .build();
    }

    private ApiKey apiKey() {
        return new ApiKey(AUTHORIZATION_HEADER, AUTHORIZATION_HEADER, "header");
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .forPaths(PathSelectors.any())
                .build();
    }

    List<SecurityReference> defaultAuth() {
        return Lists.newArrayList(
                new SecurityReference(AUTHORIZATION_HEADER,  new AuthorizationScope[0]));
    }
}