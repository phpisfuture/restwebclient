package io.github.restwebclient.interfaces;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/8/19 2:43 下午
 */
public interface ProxyCreator {

    /**
     * 创建代理类
     * @param type
     * @return
     */
    Object createProxy(Class<?> type);
}
