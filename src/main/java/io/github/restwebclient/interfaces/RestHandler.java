package io.github.restwebclient.interfaces;

import io.github.restwebclient.dto.MethodInfo;
import io.github.restwebclient.dto.ServerInfo;

/**
 * rest请求调用handler
 * 
 * @author weilai
 *
 */
public interface RestHandler {

	/**
	 * 初始化服务器信息
	 * 
	 * @param serverInfo
	 */
	void init(ServerInfo serverInfo);

	/**
	 * 调用rest请求, 返回接口
	 * 
	 * @param methodInfo
	 * @return
	 */
	Object invokeRest(MethodInfo methodInfo);

}
