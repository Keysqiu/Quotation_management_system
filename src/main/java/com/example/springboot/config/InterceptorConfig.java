package com.example.springboot.config;

import com.example.springboot.config.interceptor.JwtInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor())
                .addPathPatterns("/**") //拦截所有请求，通过判断token是否合法，来决定是否需要登录
                .excludePathPatterns("/user/login","/user/register","/provider/login","/provider/register","/**/export","/**/import","/file/**","/alipay/**","/goods/selectGood","/test/**","/mail/**"); //放行掉这些接口，不对这些接口进行token验证
    }

    @Bean
    public JwtInterceptor jwtInterceptor(){
      return  new JwtInterceptor();
    }
}
