package tw.edu.fju.miniclinic.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import tw.edu.fju.miniclinic.interceptor.LoginRequiredInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginRequiredInterceptor loginInterceptor; // 注入守門員

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                //這些路徑必須登入才能進去
                .addPathPatterns(
                        "/dashboard",
                        "/dashboard/**",
                        "/api/auth/me",
                        "/api/appointments/*/status",
                        "/password",
                        "/password/**"
                )
                //這些路徑完全公開放行
                .excludePathPatterns(
                        "/login",
                        "/logout"
                );
    }
}