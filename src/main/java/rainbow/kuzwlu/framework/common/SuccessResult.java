package rainbow.kuzwlu.framework.common;

import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author kuzwlu
 * @Description TODO
 * @Date 2021/2/6 11:08
 * @Email kuzwlu@gmail.com
 */
public class SuccessResult {

    private static HttpStatus OK = HttpStatus.OK;

    public SuccessResult(){

    }

    public static JsonResult jsonSuccess(){
        return JsonResult.newInstance().declareSuccess(OK);
    }

    public static JsonResult jsonSuccess(String message, Map<String, Object> content){
        return JsonResult.newInstance().declareSuccess(OK,message,content);
    }

    public static JsonResult jsonSuccess(String message, String key ,Object value){
        Map<String, Object> content = new HashMap<>();
        content.put(key,value);
        return JsonResult.newInstance().declareSuccess(OK,message,content);
    }

    public static JsonResult jsonSuccess(String message){
        return JsonResult.newInstance().declareSuccess(OK,message);
    }

}
