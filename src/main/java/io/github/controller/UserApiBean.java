package io.github.controller;

import io.github.restwebclient.interfaces.ProxyCreator;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.stereotype.Component;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/8/19 2:41 下午
 */
@Component
public class UserApiBean implements FactoryBean<UserApi> {

    private final ProxyCreator proxyCreator;

    public UserApiBean(ProxyCreator proxyCreator) {
        this.proxyCreator = proxyCreator;
    }

    @Override
    public UserApi getObject() throws Exception {
        return (UserApi) proxyCreator.createProxy(this.getObjectType());
    }

    @Override
    public Class<?> getObjectType() {
        return UserApi.class;
    }
}
