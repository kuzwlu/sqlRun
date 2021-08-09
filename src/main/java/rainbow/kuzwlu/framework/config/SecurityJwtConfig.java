package rainbow.kuzwlu.framework.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import rainbow.kuzwlu.framework.security.*;
import rainbow.kuzwlu.utils.MyPasswordEncoder;

import javax.annotation.Resource;

/**
 * @Author kuzwlu
 * @Description TODO
 * @Date 2020/12/13 15:14
 * @Email kuzwlu@gmail.com
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(jsr250Enabled = true, prePostEnabled = true, securedEnabled = true)
public class SecurityJwtConfig extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {

    @Resource
    private JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Resource
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Resource
    private JwtAuthenticationFailureHandler jwtAuthenticationFailureHandler;

    @Resource
    private JwtAuthenticationSuccessHandler jwtAuthenticationSuccessHandler;

    @Resource
    private JwtLogoutSuccessHandler jwtLogoutSuccessHandler;

    @Resource
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Resource
    private UserDetailsServiceImpl userDetailsService;

//    @Override
//    public void configure(WebSecurity webSecurity) throws Exception {
//        //解决静态资源被拦截的问题
//        webSecurity.ignoring().antMatchers("/static/**","/favicon.ico");
//    }

//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
//    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(this.userDetailsService).passwordEncoder(passwordEncoder());//添加自定义的userDetailsService认证
    }

    // 装载BCrypt密码编码器
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new MyPasswordEncoder();
    }


    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 使用 JWT，关闭token
                .and()
                .httpBasic()
                // 未经过认证的用户访问受保护的资源
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .and()
                .authorizeRequests()
                //跨域预检请求
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // 任何用户都可以访问URL以"/resources/", equals "/signup", 或者 "/about"开头的URL。
                //.antMatchers("/").permitAll()
                .anyRequest().authenticated()
                .and()

                .formLogin()
                .loginPage("/login")
                //.loginProcessingUrl("/login").defaultSuccessUrl("/index", true).failureUrl("/login?error")
                .successHandler(jwtAuthenticationSuccessHandler)
                // 认证失败
                .failureHandler(jwtAuthenticationFailureHandler)
                .permitAll()

                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessHandler(jwtLogoutSuccessHandler)
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                .permitAll();
        // 记住我
//        http.rememberMe().rememberMeParameter("remember-me")
//                .userDetailsService(userDetailsService).tokenValiditySeconds(300);

        // 禁用缓存
        http.headers().frameOptions().disable().cacheControl();

        http.exceptionHandling()
                // 已经认证的用户访问自己没有权限的资源处理
                .accessDeniedHandler(jwtAccessDeniedHandler)
                .and().addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }

    private CorsConfiguration buildConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addExposedHeader("Authorization");
        return corsConfiguration;
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", buildConfig());
        return new CorsFilter(source);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 设置允许跨域的路由
        registry.addMapping("/**")
                // 设置允许跨域请求的域名
                .allowedOriginPatterns("*")
                // 是否允许证书（cookies）
                .allowCredentials(true)
                // 设置允许的方法
                .allowedMethods("*")
                // 跨域允许时间
                .maxAge(3600);
    }

}
