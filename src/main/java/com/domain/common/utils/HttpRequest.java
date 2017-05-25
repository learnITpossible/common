package com.domain.common.utils;

import org.apache.commons.collections.MapUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpRequest {

    private static final Logger logger = LoggerFactory.getLogger(HttpRequest.class);

    static final int CONNECTION_TIMEOUT = 5000;

    static final int SO_TIMEOUT = 10000;

    private HttpClient httpClient;

    public HttpClient getHttpClient() {

        if (httpClient == null) {
            synchronized (this) {
                if (httpClient == null) {
                    HttpParams httpParams = new BasicHttpParams();
                    httpParams.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, CONNECTION_TIMEOUT);
                    httpParams.setParameter(CoreConnectionPNames.SO_TIMEOUT, SO_TIMEOUT);
                    HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);

                    // 设置访问协议
                    SchemeRegistry schreg = new SchemeRegistry();
                    schreg.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
                    schreg.register(new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));

                    // 多连接的线程安全的管理器
                    PoolingClientConnectionManager pccm = new PoolingClientConnectionManager(schreg);
                    pccm.setDefaultMaxPerRoute(20); //每个主机的最大并行链接数
                    pccm.setMaxTotal(200);          //客户端总并行链接最大数

                    httpClient = new DefaultHttpClient(pccm, httpParams);
                    ((DefaultHttpClient) httpClient).setHttpRequestRetryHandler((exception, executionCount, context) -> {

                        logger.debug("retry count: " + executionCount);
                        // Do not retry if over max retry count
                        return executionCount < 3 && exception instanceof SocketTimeoutException;
                    });
                }
            }
        }
        return httpClient;
    }

    public String get(String url, Map<String, String> params, String charset) throws Exception {

        List<NameValuePair> pairList = new ArrayList<>();
        if (MapUtils.isNotEmpty(params)) {
            for (String key : params.keySet()) {
                NameValuePair pair1 = new BasicNameValuePair(key, params.get(key));
                pairList.add(pair1);
            }
        }

        if (pairList.size() > 0) {
            String param = URLEncodedUtils.format(pairList, charset);
            if (!url.contains("?")) {
                url += "?" + param;
            } else {
                url += "&" + param;
            }
        }
        HttpClient httpClient = getHttpClient();
        try {

            HttpGet getMethod = new HttpGet(url);
            getMethod.setHeader("User-Agent", "Chrome");
            HttpResponse response = httpClient.execute(getMethod);

            String rt = EntityUtils.toString(response.getEntity(), charset);
            if (logger.isDebugEnabled()) {
                logger.debug("request: " + url + "\r\nresponse: " + rt);
            }
            return rt;
        } catch (Exception e) {
            throw e;
        }
    }

    public String post(String url, Map<String, Object> params, String charset) throws Exception {

        List<NameValuePair> pairList = new ArrayList<NameValuePair>();
        for (String key : params.keySet()) {
            NameValuePair pair1 = new BasicNameValuePair(key, String.valueOf(params.get(key)));
            pairList.add(pair1);
        }

        try {
            HttpClient httpClient = getHttpClient();
            HttpEntity requestHttpEntity = new UrlEncodedFormEntity(pairList, charset);
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(requestHttpEntity);
            httpPost.setHeader("User-Agent", "Chrome");
            HttpResponse response = httpClient.execute(httpPost);
            String rt = EntityUtils.toString(response.getEntity(), charset);
            if (logger.isDebugEnabled()) {
                logger.debug("request: " + url + "\r\nparams: " + params + "\r\nresponse: " + rt);
            }
            return rt;
        } catch (Exception e) {
            throw e;
        }
    }

}
