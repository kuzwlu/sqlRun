package rainbow.kuzwlu.framework.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import rainbow.kuzwlu.framework.common.JsonResult;
import rainbow.kuzwlu.utils.JwtTokenUtil;
import rainbow.kuzwlu.utils.LogUtils;
import rainbow.kuzwlu.utils.RedisUtil;
import rainbow.kuzwlu.web.mapper.master.SysLogMapper;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

/**
 * @Author kuzwlu
 * @Description TODO
 * @Date 2020/12/13 16:57
 * @Email kuzwlu@gmail.com
 */
@Component
@Slf4j
public class JwtLogoutSuccessHandler implements LogoutSuccessHandler {

    @Resource
    private JwtTokenUtil jwtTokenUtil;

    @Resource
    private SysLogMapper sysLogMapper;

    @Resource
    private RedisUtil redisUtil;

    @Override
    public void onLogoutSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        httpServletResponse.setContentType("application/json; charset=utf-8");
        String authHeader = httpServletRequest.getHeader(JwtTokenUtil.TOKEN_HEADER);
        Map<String, String[]> parameterMap = httpServletRequest.getParameterMap();
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        parameterMap.forEach((key,value) ->{
            StringBuffer stringBuffer = new StringBuffer();
            Arrays.stream(value).forEach(s -> stringBuffer.append(s));
            sb.append("\"").append(key).append("\"").append(":").append(stringBuffer.toString()).append(",");
        });
        sb.delete(sb.length()-1,sb.length());
        sb.append("}");
        if (authHeader != null && authHeader.startsWith(JwtTokenUtil.TOKEN_PREFIX)) {
            String authToken = authHeader.substring(JwtTokenUtil.TOKEN_PREFIX.length());
            String username = jwtTokenUtil.getUsernameFromToken(authToken);
            String s = redisUtil.get(username);
            if (s != null && s.equals(authToken)) {
                jwtTokenUtil.del(username);
                log.info("用户：{}\t退出登录成功", username);
                LogUtils.saveLogToDB(httpServletRequest,sysLogMapper,null, "用户登出成功","","["+sb.toString()+"]");
                httpServletResponse.getWriter().write(new ObjectMapper().writeValueAsString(JsonResult.newInstance().declareSuccess(HttpStatus.OK, "退出登录成功")));
                return;
            }
        }
        LogUtils.saveLogToDB(httpServletRequest,sysLogMapper,null ,"用户登出失败","","["+sb.toString()+"]");
        httpServletResponse.getWriter().write(new ObjectMapper().writeValueAsString(JsonResult.newInstance().declareFailure(HttpStatus.OK, "token令牌效验失败")));
    }
}
