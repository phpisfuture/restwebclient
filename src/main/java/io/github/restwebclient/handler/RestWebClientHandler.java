package io.github.restwebclient.handler;

import io.github.restwebclient.dto.MethodInfo;
import io.github.restwebclient.dto.ServerInfo;
import io.github.restwebclient.interfaces.RestHandler;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * @author weilai
 * @email 352342845@qq.com
 * @date 2020/8/19 2:56 下午
 */
public class RestWebClientHandler implements RestHandler {

    private WebClient client;

    private WebClient.RequestBodySpec request;

    @Override
    public void init(ServerInfo serverInfo) {
        this.client = WebClient.create(serverInfo.getUrl());
    }

    @Override
    public Object invokeRest(MethodInfo methodInfo) {
        this.request = this.client.method(methodInfo.getMethod()).uri(methodInfo.getUrl(), methodInfo.getParams()).accept(MediaType.APPLICATION_JSON);
        WebClient.ResponseSpec retrieve;

        // 判断是否带了body
        if (methodInfo.getBody() != null) {
            // 发出请求
            retrieve = this.request.body(methodInfo.getBody(), methodInfo.getBodyElementType()).retrieve();
        } else {
            retrieve = this.request.retrieve();
        }

        // 处理异常
        retrieve.onStatus(status -> status.value() == 404,
                response -> Mono.just(new RuntimeException("Not Found")));

        // 返回结果
        Object result;

        // 处理body
        if (methodInfo.isReturnFlux()) {
            result = retrieve.bodyToFlux(methodInfo.getReturnElementType());
        } else {
            result = retrieve.bodyToMono(methodInfo.getReturnElementType());
        }

        return result;
    }
}
