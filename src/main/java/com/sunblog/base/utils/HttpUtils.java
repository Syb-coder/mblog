package com.sunblog.base.utils;

import com.sunblog.base.lang.MtonsException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import java.io.IOException;
import java.util.Map;

/**
 * HTTP 工具类
 * <p>
 * 封装基于 Apache Commons HttpClient 的 POST 请求能力，统一设置 UTF-8 字符集，
 * 并在非 200 响应时抛出业务异常。
 * </p>
 *
 */
public class HttpUtils {

	/**
	 * 构建默认配置的 HttpClient 实例
	 * <p>统一指定 UTF-8 字符集，避免中文乱码</p>
	 *
	 * @return 配置好的 HttpClient
	 */
	private static HttpClient getClient() {
		HttpClient client = new HttpClient();
		client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8"); 
		return client;
	}
	
	/**
	 * 发送 POST 表单请求并返回响应体
	 *
	 * @param url    请求地址
	 * @param params 表单参数
	 * @return 响应体字符串
	 * @throws IOException 网络或 IO 异常时抛出
	 */
	public static String post(String url, Map<String, String> params) throws IOException {
		HttpClient client = getClient();
		
    	PostMethod post = new PostMethod(url);
    	
    	for (Map.Entry<String, String> p : params.entrySet()) {
    		post.addParameter(p.getKey(), p.getValue());
    	}
    	
    	int status = client.executeMethod(post);

    	if (status != HttpStatus.SC_OK) {
    		throw new MtonsException("该地址请求失败");
    	}
    	return post.getResponseBodyAsString();
	}
}
