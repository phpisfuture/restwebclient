package io.github.restwebclient.handler;

import io.github.restwebclient.annotation.ApiServer;
import io.github.restwebclient.dto.MethodInfo;
import io.github.restwebclient.dto.ServerInfo;
import io.github.restwebclient.interfaces.ProxyCreator;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/8/19 2:46 下午
 */
public class JdkProxyCreatorHandler implements ProxyCreator {

    @Override
    public Object createProxy(Class<?> type) {

        ApiServer apiServerAnnotation = type.getAnnotation(ApiServer.class);
        RestWebClientHandler webClient = new RestWebClientHandler();
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setUrl(apiServerAnnotation.value());
        webClient.init(serverInfo);
        return Proxy.newProxyInstance(type.getClassLoader(), new Class[]{type}, new InvocationHandler() {

            /**
             * @param proxy    代理类
             * @param method   被代理的方法
             * @param args     该方法的参数数组
             */
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                MethodInfo methodInfo = new MethodInfo();

                extractUrlAndMethod(method, methodInfo);

                extractRequestParamAndBody(method, args, methodInfo);

                // 提取返回对象信息
                extractReturnInfo(method, methodInfo);

                return webClient.invokeRest(methodInfo);
            }
        });
    }

    /**
     * 提取返回对象信息
     *
     * @param method
     * @param methodInfo
     */
    private void extractReturnInfo(Method method, MethodInfo methodInfo) {
        // 返回flux还是mono
        // isAssignableFrom 判断类型是否某个的子类
        // instanceof 判断实例是否某个的子类
        boolean isFlux = method.getReturnType()
                .isAssignableFrom(Flux.class);
        methodInfo.setReturnFlux(isFlux);

        // 得到返回对象的实际类型
        Class<?> elementType = extractElementType(method.getGenericReturnType());
        methodInfo.setReturnElementType(elementType);
    }

    /**
     * 得到请求的param和body
     *
     * @param method
     * @param args
     * @param methodInfo
     */
    private void extractRequestParamAndBody(Method method,
                                            Object[] args, MethodInfo methodInfo) {
        // 得到调用的参数和body
        Parameter[] parameters = method.getParameters();

        // 参数和值对应的map
        Map<String, Object> params = new LinkedHashMap<>();
        methodInfo.setParams(params);

        for (int i = 0; i < parameters.length; i++) {
            // 是否带 @PathVariable
            PathVariable annotationPath = parameters[i]
                    .getAnnotation(PathVariable.class);

            if (annotationPath != null) {
                params.put(annotationPath.value(), args[i]);
            }

            // 是否带了 RequestBody
            RequestBody annotationBody = parameters[i]
                    .getAnnotation(RequestBody.class);

            if (annotationBody != null) {
                methodInfo.setBody((Mono<?>) args[i]);
                // 请求对象的实际类型
                methodInfo.setBodyElementType(extractElementType(parameters[i].getParameterizedType()));
            }
        }
    }

    /**
     * 得到泛型类型的实际类型
     *
     * @param genericReturnType
     * @return
     */
    private Class<?> extractElementType(Type genericReturnType) {
        Type[] actualTypeArguments = ((ParameterizedType) genericReturnType).getActualTypeArguments();
        return (Class<?>) actualTypeArguments[0];
    }

    /**
     * 得到请求的URL和方法
     *
     * @param method
     * @param methodInfo
     */
    private void extractUrlAndMethod(Method method,
                                     MethodInfo methodInfo) {
        // 得到请求URL和请求方法
        Annotation[] annotations = method.getAnnotations();

        for (Annotation annotation : annotations) {
            // GET
            if (annotation instanceof GetMapping) {
                GetMapping a = (GetMapping) annotation;

                methodInfo.setUrl(a.value()[0]);
                methodInfo.setMethod(HttpMethod.GET);
            }
            // POST
            else if (annotation instanceof PostMapping) {
                PostMapping a = (PostMapping) annotation;

                methodInfo.setUrl(a.value()[0]);
                methodInfo.setMethod(HttpMethod.POST);
            }
            // DELETE
            else if (annotation instanceof DeleteMapping) {
                DeleteMapping a = (DeleteMapping) annotation;

                methodInfo.setUrl(a.value()[0]);
                methodInfo.setMethod(HttpMethod.DELETE);
            }

        }
    }
}
