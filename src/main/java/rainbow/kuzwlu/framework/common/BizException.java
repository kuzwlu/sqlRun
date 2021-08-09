package rainbow.kuzwlu.framework.common;

/**
 * @Author kuzwlu
 * @Description TODO
 * @Date 2020/12/13 13:53
 * @Email kuzwlu@gmail.com
 */
public class BizException extends RuntimeException{

    public BizException(String msg){
        super(msg);
    }

    public BizException(String msg, Throwable cause){
        super(msg,cause);
    }

    public BizException(Throwable cause){
        super(cause);
    }

}
