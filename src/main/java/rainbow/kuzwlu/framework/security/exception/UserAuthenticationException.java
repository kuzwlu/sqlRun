package rainbow.kuzwlu.framework.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @Author kuzwlu
 * @Description TODO
 * @Date 2020/12/13 20:24
 * @Email kuzwlu@gmail.com
 */
public class UserAuthenticationException extends AuthenticationException {

    public UserAuthenticationException(String msg, Throwable t) {
        super(msg, t);
    }

    public UserAuthenticationException(String msg) {
        super(msg);
    }

}
