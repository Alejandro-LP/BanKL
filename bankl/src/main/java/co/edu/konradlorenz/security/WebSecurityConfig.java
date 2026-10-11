package co.edu.konradlorenz.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebSecurityConfig implements WebMvcConfigurer {
    private final AuthInterceptor auth;
    public WebSecurityConfig(AuthInterceptor auth) { this.auth=auth; }
    @Override public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(auth).addPathPatterns("/clientes/**", "/cuentas/**", "/transferencias/**",
            "/prestamos/**", "/inversiones/**", "/notificaciones/**", "/beneficiarios/**", "/api/**");
    }
}
