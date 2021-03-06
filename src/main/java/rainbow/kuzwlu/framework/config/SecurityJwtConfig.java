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
//        //????????????????????????????????????
//        webSecurity.ignoring().antMatchers("/static/**","/favicon.ico");
//    }

//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
//    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(this.userDetailsService).passwordEncoder(passwordEncoder());//??????????????????userDetailsService??????
    }

    // ??????BCrypt???????????????
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
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // ?????? JWT?????????token
                .and()
                .httpBasic()
                // ????????????????????????????????????????????????
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .and()
                .authorizeRequests()
                //??????????????????
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                // ???????????????????????????URL???"/resources/", equals "/signup", ?????? "/about"?????????URL???
                //.antMatchers("/").permitAll()
                .anyRequest().authenticated()
                .and()

                .formLogin()
                .loginPage("/login")
                //.loginProcessingUrl("/login").defaultSuccessUrl("/index", true).failureUrl("/login?error")
                .successHandler(jwtAuthenticationSuccessHandler)
                // ????????????
                .failureHandler(jwtAuthenticationFailureHandler)
                .permitAll()

                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessHandler(jwtLogoutSuccessHandler)
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                .permitAll();
        // ?????????
//        http.rememberMe().rememberMeParameter("remember-me")
//                .userDetailsService(userDetailsService).tokenValiditySeconds(300);

        // ????????????
        http.headers().frameOptions().disable().cacheControl();

        http.exceptionHandling()
                // ????????????????????????????????????????????????????????????
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
        // ???????????????????????????
        registry.addMapping("/**")
                // ?????????????????????????????????
                .allowedOriginPatterns("*")
                // ?????????????????????cookies???
                .allowCredentials(true)
                // ?????????????????????
                .allowedMethods("*")
                // ??????????????????
                .maxAge(3600);
    }

}
