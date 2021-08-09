package rainbow.kuzwlu.framework.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import rainbow.kuzwlu.framework.common.JsonResult;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author kuzwlu
 * @Description TODO
 * @Date 2020/12/13 15:15
 * @Email kuzwlu@gmail.com
 */
@Slf4j
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
        log.error("[url:"+httpServletRequest.getRequestURI()+", msg:"+e.getMessage()+"]");
        httpServletResponse.setContentType("application/json; charset=utf-8");
        httpServletResponse.getWriter().write(new ObjectMapper().writeValueAsString(JsonResult.newInstance().declareFailure(HttpStatus.FORBIDDEN,e.getMessage())));
    }
}
