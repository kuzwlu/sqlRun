package rainbow.kuzwlu.framework.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import rainbow.kuzwlu.utils.JwtTokenUtil;
import rainbow.kuzwlu.utils.RedisUtil;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author kuzwlu
 * @Description TODO
 * @Date 2020/12/13 15:56
 * @Email kuzwlu@gmail.com
 */
@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Resource
    private UserDetailsServiceImpl userDetailsService;

    @Resource
    private JwtTokenUtil jwtTokenUtil;

    @Resource
    private RedisUtil redisUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        response.setContentType("application/json; charset=utf-8");
        String authHeader = request.getHeader(JwtTokenUtil.TOKEN_HEADER);
        if (authHeader != null && authHeader.startsWith(JwtTokenUtil.TOKEN_PREFIX)) {
            String authToken = authHeader.substring(JwtTokenUtil.TOKEN_PREFIX.length());
            String user = jwtTokenUtil.getUsernameFromToken(authToken);
            log.info("authenticate login user [user={}, url={}] ", user,request.getRequestURI());
            if (user != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                if (jwtTokenUtil.validateToken(user)) {
                    String s = redisUtil.get(user);
                    if (s.equals(authToken)) {
                        UserDetails userDetails = userDetailsService.loadUserByUsername(user);

                        if (userDetails != null) {
                            UsernamePasswordAuthenticationToken authentication =
                                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        }
                    }

                }
            }
        }
        chain.doFilter(request, response);
    }

}
