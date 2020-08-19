package io.github.restwebclient.bean;

import io.github.restwebclient.handler.JdkProxyCreatorHandler;
import io.github.restwebclient.interfaces.ProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/8/19 5:43 下午
 */
@Component
public class ProxyCreatorBean {

    @Bean
    ProxyCreator jdkProxyCreator() {
        return new JdkProxyCreatorHandler();
    }
}
