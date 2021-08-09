package rainbow.kuzwlu.framework.common;

import lombok.Data;
import org.springframework.http.HttpStatus;
import rainbow.kuzwlu.utils.TimeUtil;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author kuzwlu
 * @Description TODO
 * @Date 2020/12/13 15:19
 * @Email kuzwlu@gmail.com
 */
@Data
public class JsonResult implements Serializable {

    private Integer code;

    private boolean success = false;

    private String message;

    private Map<String, Object> content;

    private String time = TimeUtil.stampToDate(System.currentTimeMillis());

    public static JsonResult newInstance(){
        return new JsonResult();
    }

    public JsonResult declareFailure(HttpStatus code,String message, Map<String, Object> content){
        if (this.content == null || this.content.isEmpty()) {
            this.content = new HashMap<>();
        }
        this.content.putAll(content);
        return declareFailure(code,message);
    }

    public JsonResult declareFailure(HttpStatus code,String message){
        this.message = message;
        return declareFailure(code);
    }

    public JsonResult declareFailure(HttpStatus code){
        this.code = code.value();
        this.success = false;
        return this;
    }


    public JsonResult declareSuccess(HttpStatus code){
        this.code = code.value();
        this.success = true;
        return this;
    }

    public JsonResult declareSuccess(HttpStatus code,String message){
        this.message = message;
        return declareSuccess(code);
    }

    public JsonResult declareSuccess(HttpStatus code,String message,Map<String, Object> content){
        if (this.content == null || this.content.isEmpty()) {
            this.content = new HashMap<>();
        }
        this.content.putAll(content);
        return declareSuccess(code,message);
    }

    public JsonResult addContent(String key, Object val) {
        if (this.content == null || this.content.isEmpty()) {
            this.content = new HashMap<>();
        }
        this.content.put(key, val);
        return this;
    }

}
