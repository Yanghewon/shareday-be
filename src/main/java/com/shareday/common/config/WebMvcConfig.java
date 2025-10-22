package com.shareday.common.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173")  // 정확한 React 주소
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*", "Authorization")  // Authorization 헤더 추가
                .exposedHeaders("Content-Disposition")
                .allowCredentials(true)
                .maxAge(3600);
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
    }
}
