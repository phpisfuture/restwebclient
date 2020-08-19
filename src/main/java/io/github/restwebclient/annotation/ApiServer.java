package io.github.restwebclient.annotation;

import java.lang.annotation.*;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/8/19 2:38 下午
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ApiServer{

    String value() default "";
}
