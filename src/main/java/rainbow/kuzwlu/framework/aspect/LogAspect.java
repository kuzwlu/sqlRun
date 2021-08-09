package rainbow.kuzwlu.framework.aspect;

import com.alibaba.fastjson.JSON;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import rainbow.kuzwlu.framework.insterface.Log;
import rainbow.kuzwlu.utils.*;
import rainbow.kuzwlu.web.mapper.master.SysLogMapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @Author kuzwlu
 * @Description TODO
 * @Date 2021/2/8 13:28
 * @Email kuzwlu@gmail.com
 */
@Aspect
@Component
public class LogAspect {

    @Autowired
    private SysLogMapper sysLogMapper;

    private Logger logger = LoggerFactory.getLogger(LogAspect.class);

    //定义切点 @Pointcut
    //在注解的位置切入代码
    @Pointcut("@annotation( rainbow.kuzwlu.framework.insterface.Log)")
    public void logPoinCut() {
    }

    //切面 配置通知
    @AfterReturning("logPoinCut()")
    public void saveSysLog(JoinPoint joinPoint) {
        HttpServletRequest request = HttpContextUtils.getHttpServletRequest();

        //从切面织入点处通过反射机制获取织入点处的方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //获取切入点所在的方法
        Method method = signature.getMethod();

        //获取操作
        Log myLog = method.getAnnotation(Log.class);
        String value = "";
        if (myLog != null) {
            value = myLog.value();
        }

        //获取请求的类名
        String className = joinPoint.getTarget().getClass().getName();
        //获取请求的方法名
        String methodName = method.getName();

        //请求的参数
        Object[] args = joinPoint.getArgs();
        Object[] objects = Arrays.stream(args).filter(o -> {
            if (o instanceof UsernamePasswordAuthenticationToken || o instanceof HttpServletRequest || o instanceof HttpServletResponse) {
                return false;
            }
            return true;
        }).toArray();
        //将参数所在的数组转换成json
        String params = JSON.toJSONString(objects);

        LogUtils.saveLogToDB(request,sysLogMapper,null,value,className+"."+methodName,params);

    }

}
