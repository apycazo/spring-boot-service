package es.jander.codex.sbs.etc;

import com.fasterxml.classmate.TypeResolver;
import es.jander.codex.sbs.App;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.AlternateTypeRules;
import springfox.documentation.schema.WildcardType;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SpringFoxConfig
{
    @Value("${info.version:unknown}")
    private String version;
    @Value("${info.contact.name:unknown}")
    private String name;
    @Value("${info.contact.website:unknown}")
    private String website;
    @Value("${info.contact.email:unknown}")
    private String email;
    @Value("${spring.application.name}")
    private String appName;
    @Value("${info.about}")
    private String about;
    @Value("${info.license.name}")
    private String licenseName;
    @Value("${info.license.url}")
    private String licenseURL;

    @Autowired
    private TypeResolver typeResolver;

    @Bean
    public Docket swaggerApi()
    {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage(App.class.getPackage().getName()))
                .paths(PathSelectors.regex("/api/.*"))
                .build()
                .pathMapping("/")
                .genericModelSubstitutes(ResponseEntity.class)
                .alternateTypeRules(
                        AlternateTypeRules.newRule(
                                typeResolver.resolve(
                                        DeferredResult.class,
                                        typeResolver.resolve(ResponseEntity.class, WildcardType.class)),
                                typeResolver.resolve(WildcardType.class)))
                .useDefaultResponseMessages(false)
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo()
    {
        return new ApiInfo(
                appName,
                about,
                version,
                null,
                new Contact(name, website, email),
                licenseName,
                licenseURL);
    }
}
