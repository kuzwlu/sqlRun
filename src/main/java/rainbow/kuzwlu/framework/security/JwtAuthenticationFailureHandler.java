package rainbow.kuzwlu.framework.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import rainbow.kuzwlu.framework.common.JsonResult;
import rainbow.kuzwlu.utils.LogUtils;
import rainbow.kuzwlu.web.mapper.master.SysLogMapper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

/**
 * @Author kuzwlu
 * @Description TODO
 * @Date 2020/12/13 16:28
 * @Email kuzwlu@gmail.com
 */
@Slf4j
@Component
public class JwtAuthenticationFailureHandler implements AuthenticationFailureHandler {

    @Autowired
    private SysLogMapper sysLogMapper;

    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
        httpServletResponse.setContentType("application/json; charset=utf-8");
        JsonResult jsonResult = JsonResult.newInstance();
        jsonResult.declareFailure(HttpStatus.OK,"用户名或密码错误");
        String result = new ObjectMapper().writeValueAsString(jsonResult);
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
        LogUtils.saveLogToDB(httpServletRequest,sysLogMapper,null,"用户登录失败:用户名或密码错误","","["+sb.toString()+"]");
        httpServletResponse.getWriter().write(result);
    }
}
