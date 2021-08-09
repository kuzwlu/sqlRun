package rainbow.kuzwlu.framework.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import rainbow.kuzwlu.framework.common.JsonResult;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author kuzwlu
 * @Description TODO
 * @Date 2020/12/13 15:51
 * @Email kuzwlu@gmail.com
 */
@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        log.info("[url:"+httpServletRequest.getRequestURI()+", msg:token令牌效验失败]");
        httpServletResponse.setContentType("application/json; charset=utf-8");
        httpServletResponse.getWriter().write(new ObjectMapper().writeValueAsString(JsonResult.newInstance().declareFailure(HttpStatus.UNAUTHORIZED, "token令牌效验失败")));
    }
}
