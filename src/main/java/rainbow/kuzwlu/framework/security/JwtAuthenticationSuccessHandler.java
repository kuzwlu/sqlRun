package rainbow.kuzwlu.framework.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import rainbow.kuzwlu.framework.common.JsonResult;
import rainbow.kuzwlu.utils.JwtTokenUtil;
import rainbow.kuzwlu.utils.LogUtils;
import rainbow.kuzwlu.web.mapper.master.SysLogMapper;
import rainbow.kuzwlu.web.mapper.master.SysRoleMapper;
import rainbow.kuzwlu.web.model.master.SysRole;

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
 * @Date 2020/12/13 16:30
 * @Email kuzwlu@gmail.com
 */
@Component
@Slf4j
public class JwtAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Resource
    private SysRoleMapper sysRoleMapper;

    @Resource
    private SysLogMapper sysLogMapper;

    @Resource
    private JwtTokenUtil jwtTokenUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        UserSession userSession = (UserSession) authentication.getPrincipal();
        SysRole sysRole = sysRoleMapper.selectById(userSession.getRoleId());
        String token = jwtTokenUtil.generateToken(userSession,sysRole.getSign());
        jwtTokenUtil.setExpire(userSession.getUser(),token,JwtTokenUtil.EXPIRATION);
        log.info("[用户："+userSession.getUser()+"\t登录成功]");
        httpServletResponse.setContentType("application/json; charset=utf-8");
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
        LogUtils.saveLogToDB(httpServletRequest,sysLogMapper,authentication,"用户"+userSession.getUser()+"登录成功","","["+sb.toString()+"]");
        httpServletResponse.getWriter().write(new ObjectMapper().writeValueAsString(JsonResult.newInstance().declareSuccess(HttpStatus.OK,token)));
    }
}
