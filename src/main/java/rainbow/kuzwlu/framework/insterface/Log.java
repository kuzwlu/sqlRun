package rainbow.kuzwlu.framework.insterface;

/**
 * @Author kuzwlu
 * @Description TODO
 * @Date 2021/2/8 13:27
 * @Email kuzwlu@gmail.com
 */

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {

    String value() default "";

}
