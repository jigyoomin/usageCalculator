package com.skcc.cloudzcp.usage;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.OkHttpClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@EnableAsync
public class SpringConfiguration {
    
    @Bean
    public RestTemplate restTemplate() {
        OkHttpClientHttpRequestFactory factory = new OkHttpClientHttpRequestFactory();
        factory.setConnectTimeout(10 * 1000);
        factory.setReadTimeout(30 * 1000);
        RestTemplate restTemplate = new RestTemplate(factory);
        return restTemplate;
    }
    
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.skcc.cloudzcp"))
                .paths(PathSelectors.ant("/**"))
                .build()
                .apiInfo(apiInfo())
                .tags(new Tag("usage", "사용량 API"))
                .tags(new Tag("healthCheck", "Health check"));
    }
    
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("ZCP IPF API").version("1.0.0").build();
    }
    
}
