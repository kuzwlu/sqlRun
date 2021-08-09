package rainbow.kuzwlu.framework.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import rainbow.kuzwlu.exception.DataSourceException;
import rainbow.kuzwlu.exception.MybatisPlusException;
import rainbow.kuzwlu.exception.PropertiesException;
import rainbow.kuzwlu.exception.SqlException;

/**
 * @Author kuzwlu
 * @Description TODO
 * @Date 2020/12/13 15:18
 * @Email kuzwlu@gmail.com
 */
@Slf4j
@ControllerAdvice
public class DefaultExceptionHandler {

    /**
     * 业务异常
     * @param e
     * @return
     */
    @ResponseBody
    @ExceptionHandler({BizException.class, SqlException.class, DataSourceException.class, PropertiesException.class, MybatisPlusException.class})
    public JsonResult defaultExceptionHandler(Exception e) {
        StringBuilder sb = new StringBuilder(e.getMessage());
        sb.delete(0,1);
        return JsonResult.newInstance().declareFailure(HttpStatus.INTERNAL_SERVER_ERROR,sb.toString());
    }

    /**
     *  校验错误拦截处理
     *
     * @param exception 错误信息集合
     * @return 错误信息
     */
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public JsonResult validationBodyException(MethodArgumentNotValidException exception){
        BindingResult result = exception.getBindingResult();
        return JsonResult.newInstance().declareFailure(HttpStatus.INTERNAL_SERVER_ERROR,result.getFieldError().getDefaultMessage());
    }

}
