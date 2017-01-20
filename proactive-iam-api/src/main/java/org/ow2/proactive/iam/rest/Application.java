/*
 * ProActive Parallel Suite(TM):
 * The Open Source library for parallel and distributed
 * Workflows & Scheduling, Orchestration, Cloud Automation
 * and Big Data Analysis on Enterprise Grids & Clouds.
 *
 * Copyright (c) 2007 - 2017 ActiveEon
 * Contact: contact@activeeon.com
 *
 * This library is free software: you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation: version 3 of
 * the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * If needed, contact us to obtain a release under GPL Version 2 or 3
 * or a different license than the AGPL.
 */
package org.ow2.proactive.iam.rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.google.common.base.Predicate;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;


/**
 * @author ActiveEon Team
 */
//@EnableWebMvc
//@Configuration("classpath:applicationContext.xml")
//@Configuration
//@ComponentScan(basePackages = "org.ow2.proactive.iam")
@SpringBootApplication
//@EnableAutoConfiguration(exclude = { MultipartAutoConfiguration.class })
//@EnableSwagger2
//@EnableAutoConfiguration
//@PropertySource("classpath:application.properties")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /*
     * @Bean
     * public ViewResolver internalResourceViewResolver() {
     * InternalResourceViewResolver bean = new InternalResourceViewResolver();
     * bean.setViewClass(JstlView.class);
     * bean.setPrefix("/WEB-INF/");
     * bean.setSuffix(".jsp");
     * return bean;
     * }
     */

    /*
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.favorPathExtension(false)
                  .favorParameter(true)
                  .parameterName("format")
                  .ignoreAcceptHeader(true)
                  .useJaf(false)
                  .defaultContentType(MediaType.APPLICATION_JSON)
                  .mediaType("json", MediaType.APPLICATION_JSON);
    }
    */

    /*@Bean
    public MultipartResolver multipartResolver() {
        return new CommonsMultipartResolver();
    }*/

    /*
     * The following code is for Swagger documentation
     */
    @Bean
    public Docket iamApi() {
        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo())
                                                      .groupName("proactive-iam")
                                                      .select()
                                                      .paths(allowedPaths())
                                                      .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("ProActive IAM REST API")
                                   .description("The purpose of ProActive IAM is ...\n")
                                   .licenseUrl("https://github.com/ow2-proactive/proactive-iam/blob/master/LICENSE")
                                   .version("1.0")
                                   .build();
    }

    private Predicate<String> allowedPaths() {
        return PathSelectors.regex("/users.*");
    }
}
