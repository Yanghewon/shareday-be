package com.shareday.common.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // application.yml 에서 주입 (쉼표로 여러 Origin 지정 가능)
    @Value("${app.cors.allowed-origins:http://localhost:3000}")
    private String[] allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET","POST","PUT","PATCH","DELETE","OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("Content-Disposition") // 파일 다운로드 시 유용
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RequestLoggingInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/swagger-ui/**", "/v3/api-docs/**", // swagger
                        "/actuator/**",                        // actuator
                        "/static/**", "/favicon.ico"           // 정적 리소스
                );
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 정적 리소스 (src/main/resources/static)
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");

        // 필요 시 문서 폴더 매핑 (예: /docs/** → classpath:/docs/)
        registry.addResourceHandler("/docs/**")
                .addResourceLocations("classpath:/docs/");
    }

    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        // Jackson 커스터마이즈 (null 필드 제외 등) — Boot 기본값에 살짝 추가
        for (HttpMessageConverter<?> c : converters) {
            if (c instanceof MappingJackson2HttpMessageConverter mc) {
                ObjectMapper om = mc.getObjectMapper().copy();
                om.setSerializationInclusion(JsonInclude.Include.NON_NULL); // null 필드 제외
                mc.setObjectMapper(om);
            }
        }
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        // 필요 시 Pageable 기본값/커스텀 리졸버 추가 가능
        // (Spring Data Web 의존성 있으면 기본 제공됨)
    }
}