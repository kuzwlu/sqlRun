package rainbow.kuzwlu.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import rainbow.kuzwlu.framework.security.UserSession;
import rainbow.kuzwlu.web.mapper.master.SysLogMapper;
import rainbow.kuzwlu.web.model.master.SysLog;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * @Author kuzwlu
 * @Description TODO
 * @Date 2021/2/8 16:30
 * @Email kuzwlu@gmail.com
 */
public class LogUtils {

    private static Logger logger = LoggerFactory.getLogger(LogUtils.class);

    public static void saveLogToDB(HttpServletRequest request,  SysLogMapper sysLogMapper, Authentication authentication, String operation, String method, String params){
        SysLog sysLog = new SysLog();
        String authorization = request.getHeader(JwtTokenUtil.TOKEN_HEADER);
        String username = "";
        if (authorization == null) {
            if (authentication == null) {
                return;
            }else {
                username = ((UserSession) authentication.getPrincipal()).getUser();
            }
        }else{
            //获取用户名
            String authToken = authorization.substring(JwtTokenUtil.TOKEN_PREFIX.length());
            username = new JwtTokenUtil().getUsernameFromToken(authToken);
        }

        sysLog.setUsername(username);

        sysLog.setOperation(operation);
        //获取用户ip地址
        sysLog.setIp(IPUtils.getIpAddr(request));
        sysLog.setUserAgent(request.getHeader("User-Agent"));

        sysLog.setRequestUrl(request.getRequestURI());

        sysLog.setRequestMethod(request.getMethod());

        sysLog.setMethod(method);

        sysLog.setParams(params);

        sysLog.setCreateDate(LocalDateTime.now());

        sysLogMapper.insert(sysLog);

        logger.info(sysLog.toString());
    }

}
