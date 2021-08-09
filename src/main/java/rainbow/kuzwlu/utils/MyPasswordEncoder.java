package rainbow.kuzwlu.utils;

import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @Author kuzwlu
 * @Description TODO
 * @Date 2020/12/13 21:48
 * @Email kuzwlu@gmail.com
 */
public class MyPasswordEncoder implements PasswordEncoder {
    @Override
    public String encode(CharSequence rawPassword) {
        return rawPassword.toString();
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return encodedPassword.equals(rawPassword.toString());
    }
}
