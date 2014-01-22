package com.simple.httpclient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

public class CustomerHttpClient {
	private static HttpClient customerHttpClient;

	private CustomerHttpClient() {
	}

	public static synchronized HttpClient getHttpClient() {
		if (null == customerHttpClient) {
			PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager();
			RequestConfig defaultRequestConfig = RequestConfig.custom()
					.setSocketTimeout(4000).setConnectTimeout(2000)
					.setConnectionRequestTimeout(5000)
					.setStaleConnectionCheckEnabled(true)
					.setExpectContinueEnabled(true).build();
			customerHttpClient = HttpClients.custom()
					.setConnectionManager(manager)
					.setDefaultRequestConfig(defaultRequestConfig).build();
		}
		return customerHttpClient;
	}

	@SuppressWarnings("finally")
	public static String get(String url) throws Exception {
		BufferedReader in = null;
		String content = null;
		try {
			// 定义HttpClient
			HttpClient client = getHttpClient();
			// 实例化HTTP方法
			HttpGet request = new HttpGet();
			request.setURI(new URI(url));
			HttpResponse response = client.execute(request);
			in = new BufferedReader(new InputStreamReader(response.getEntity()
					.getContent()));
			StringBuffer sb = new StringBuffer("");
			String line = "";
			String lineSeparator = System.getProperty("line.separator");
			while ((line = in.readLine()) != null) {
				sb.append(line + lineSeparator);
			}
			in.close();
			content = sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();// 最后要关闭BufferedReader
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return content;
		}
	}
}
