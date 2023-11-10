package com.heima.wemedia.config;

import com.heima.wemedia.interceptor.WmTokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Description 自媒体web配置
 *
 * @author lebrwcd
 * @version 1.0
 * @date 2023/11/9
 */
@Configuration
public class WmMVCConfig implements WebMvcConfigurer {

    @Autowired
    private WmTokenInterceptor wmTokenInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 配置拦截器，拦截所有路径
        registry.addInterceptor(wmTokenInterceptor).addPathPatterns("/**");
    }
}
