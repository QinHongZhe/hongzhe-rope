package com.starblues.rope.config;

import com.starblues.rope.rest.common.BaseResource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Swagger2 配置
 *
 * @author zhangzhuo
 * @version 1.0
 */
@Configuration
@EnableSwagger2
public class Swagger2Config {

    @Value("${server.port}")
    private String port;

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(this.apiInfo())
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build();
    }


    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("DataTransfer")
                .description("DataTransfer 系统接口")
                .termsOfServiceUrl("http://ip:" + port  + BaseResource.API + "**")
                .contact(new Contact("zhangzhuo", "", ""))
                .version("3.1.2")
                .build();
    }

}
