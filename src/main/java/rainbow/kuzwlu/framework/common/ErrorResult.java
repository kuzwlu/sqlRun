package rainbow.kuzwlu.framework.common;

import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author kuzwlu
 * @Description TODO
 * @Date 2021/2/6 11:17
 * @Email kuzwlu@gmail.com
 */
public class ErrorResult {

    private static HttpStatus OK = HttpStatus.OK;

    public ErrorResult(){

    }

    public static JsonResult jsonError(){
        return JsonResult.newInstance().declareFailure(OK);
    }

    public static JsonResult jsonError(String message, Map<String, Object> content){
        return JsonResult.newInstance().declareFailure(OK,message,content);
    }

    public static JsonResult jsonError(String message, String key ,Object value){
        Map<String, Object> content = new HashMap<>();
        content.put(key,value);
        return JsonResult.newInstance().declareFailure(OK,message,content);
    }

    public static JsonResult jsonError(String message){
        return JsonResult.newInstance().declareFailure(OK,message);
    }

}
