package com.domain.common.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.params.ConnManagerPNames;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * 摘自网上代码，做了小调整.
 * @author Frank
 */
public class HttpUtil {

    private static final int CONN_TIMEOUT = 10 * 1000;

    private static final int SO_TIMEOUT = 20 * 1000;

    private static final String CHAR_SET = "UTF-8";

    private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);

    private static HttpClient httpClient;

    private static final String TAG = "HttpUtil: ";

    // private static HttpRoute GAODE_ROUTE = new HttpRoute(new HttpHost("http://restapi.amap.com"));
    // private static HttpRoute BAIDU_ROUTE = new HttpRoute(new HttpHost("http://api.map.baidu.com"));

    /**
     * 获得线程安全的HttpClient对象，能够适应多线程环境
     * @return
     */
    @SuppressWarnings("deprecation")
    public static HttpClient getHttpClient() {

        if (null == httpClient) {
            synchronized ("ssssssss") {
                HttpParams params = new BasicHttpParams();
                // 设置一些基本参数
                HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
                HttpProtocolParams.setContentCharset(params, "UTF-8");
                HttpProtocolParams.setUseExpectContinue(params, true);
                HttpProtocolParams.setUserAgent(params, "Mozilla/5.0(Linux;U;Android 2.2.1;en-us;Nexus One Build.FRG83) " + "AppleWebKit/553.1(KHTML,like Gecko) Version/4.0 Mobile Safari/533.1");
                params.setIntParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, 500);
                params.setParameter(ConnManagerPNames.MAX_CONNECTIONS_PER_ROUTE, new ConnPerRouteBean(500));
                // 超时设置
                /* 从连接池中取连接的超时时间 */
                // HttpConnectionParams.setConnectionTimeout(params, 1000);
                // ConnManagerParams.setTimeout(params, 1000);
                /* 连接超时 */
                HttpConnectionParams.setConnectionTimeout(params, CONN_TIMEOUT);
                /* 请求超时 */
                HttpConnectionParams.setSoTimeout(params, SO_TIMEOUT);

                // 设置我们的HttpClient支持HTTP和HTTPS两种模式
                SchemeRegistry schReg = new SchemeRegistry();
                schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
                schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

                // 使用线程安全的连接管理来创建HttpClient
                ThreadSafeClientConnManager conMgr = new ThreadSafeClientConnManager(params, schReg);
                httpClient = new DefaultHttpClient(conMgr, params);
            }
        }
        /*try {
            ThreadSafeClientConnManager conMgr = (ThreadSafeClientConnManager) httpClient.getConnectionManager();
            logger.info(String.format("Http client pool config live:%s MPR:%s MaxTotal:%s,BaiDu:%s,GaoDe:%s"
                    , conMgr.getConnectionsInPool()
                    , conMgr.getMaxForRoute(GAODE_ROUTE)
                    , conMgr.getMaxTotal()
                    , conMgr.getConnectionsInPool(BAIDU_ROUTE)
                    , conMgr.getConnectionsInPool(GAODE_ROUTE)));
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        return httpClient;
    }

    /**
     * 获得Post请求对象
     * @param uri    请求地址，也可以带参数
     * @param params 如果为null，则不添加由BasicNameValue封装的参数
     * @return
     */
    public static HttpPost getPost(String uri, List<BasicNameValuePair> params) {

        HttpPost post = new HttpPost(uri);
        try {
            if (params != null) {
                post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return post;
    }

    /**
     * 获得Post请求对象
     * @param uri
     * @param params
     * @param charSet
     * @return
     */
    public static HttpPost getPost(String uri, List<BasicNameValuePair> params, String charSet) {

        HttpPost post = new HttpPost(uri);
        try {
            if (params != null) {
                post.setEntity(new UrlEncodedFormEntity(params, charSet));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return post;
    }

    private static String getUrlWithoutQuery(URI uri) {

        if (uri == null) {
            return "";
        }

        StringBuffer sb = new StringBuffer();
        sb.append(uri.getScheme());
        sb.append("://");
        if (null != uri.getHost()) {
            sb.append(uri.getHost());
        }
        int port = uri.getPort();
        if (port != -1) {
            sb.append(":");
            sb.append(port);
        }
        sb.append(uri.getPath());
        return sb.toString();
    }

    @SuppressWarnings("unused")
    private static String getUrlWithoutQuery(String uri) {

        if (StringUtils.isBlank(uri)) {
            return "";
        }
        if (uri.indexOf("?") > 0) {
            uri = uri.substring(0, uri.indexOf("?"));
        }
        return uri;
    }

    /**
     * 用户使用的方法 功能：从服务器获得字符串
     * @param post
     * @return
     */
    public static String getString(HttpPost post) {

        try {
            HttpClient httpClient = getHttpClient();
            HttpResponse response;
            response = httpClient.execute(post);
            if (null == response) {  //  1.) 考虑 response 中 entity 中输入流是否存在
                logger.error(ExecuteContext.httpByBussiness.get() + " ============> inputStream of the response is null ");
                return null;
            }
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                post.abort();
                logger.error(TAG + " getString() failed!  Abort Connection ErrCode=" + response.getStatusLine().getStatusCode() + " Url:" + post.getURI());
                return null;
            }

            // String result = DHFEntityUtils.toString(response.getEntity());
            String result = EntityUtils.toString(response.getEntity());
            if (null == result || result.isEmpty()) {
                logger.info(ExecuteContext.httpByBussiness.get() + " ====> getString of the response is null ");
                return null;
            }
            return result;
        } catch (ClientProtocolException e) {
            logger.error(TAG + e.getMessage());
            return null;
        } catch (IOException e) {
            logger.error(TAG + e.getMessage());
            return null;
        } catch (Exception e) {
            logger.error(TAG + e.getMessage());
            return null;
        } finally {
            post.releaseConnection();
        }
    }

    /**
     * 用户使用的方法 功能：从服务器获得GZIP字符串
     * @param post
     * @return
     */
    public static String getGzipString(HttpPost post) {

        if (post == null) {
            logger.warn("getGzipString post is null!");
            return null;
        }
        try {
            HttpResponse response = getHttpClient().execute(post);

            if (null == response) {  //  1.) 考虑 response 中 entity 中输入流是否存在
                logger.error(TAG + " getGzipString response is null!");
                return null;
            }
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                post.abort();
                logger.error(TAG + " getGzipString() failed!  Abort Connection ErrCode=" + response.getStatusLine().getStatusCode() + " Url:" + post.getURI());
                return null;
            }

            HttpEntity entity = response.getEntity();

            if (entity != null) {
                InputStream instreams = entity.getContent();

                GZIPInputStream gInputStream = new GZIPInputStream(instreams);
                byte[] by = new byte[1024];
                StringBuffer strBuffer = new StringBuffer();
                int len = 0;
                while ((len = gInputStream.read(by)) != -1) {
                    strBuffer.append(new String(by, 0, len, "utf-8"));
                }

                String result = strBuffer.toString();

                return result;
            } else {
                logger.error(TAG + " getGzipString() failed!  response.getEntity() is null!");

            }

        } catch (ClientProtocolException e) {
            logger.error("getGzipString " + getUrlWithoutQuery(post.getURI()) + " get Error1:" + e.getMessage(), e);
        } catch (IOException e) {
            logger.error("getGzipString " + getUrlWithoutQuery(post.getURI()) + " get Error2:" + e.getMessage(), e);
        } catch (Exception e) {
            logger.error("getGzipString " + getUrlWithoutQuery(post.getURI()) + " get Error3:" + e.getMessage(), e);
        } finally {
            post.releaseConnection();
        }
        return "";
    }

    /**
     * @throws
     * @Title: getString
     * @Description: 需要做身份认证
     * @return: String
     */
    public static String getString(HttpPost post, String user, String password) {

        try {
            HttpClient httpClient = getHttpClient();
            CredentialsProvider provider = new BasicCredentialsProvider();
            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(user, password);
            provider.setCredentials(new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT), credentials);
            ((DefaultHttpClient) httpClient).setCredentialsProvider(provider);
            HttpResponse response;
            response = httpClient.execute(post);
            if (null == response) {  //  1.) 考虑 response 中 entity 中输入流是否存在
                logger.error(ExecuteContext.httpByBussiness.get() + " ============> inputStream of the response is null ");
                return null;
            }
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                post.abort();
                logger.error(TAG + " getString() failed!  Abort Connection ErrCode=" + response.getStatusLine().getStatusCode() + " Url:" + post.getURI());
                return null;
            }

            // String result = DHFEntityUtils.toString(response.getEntity());
            String result = EntityUtils.toString(response.getEntity());
            if (null == result || result.isEmpty()) {
                logger.info(ExecuteContext.httpByBussiness.get() + " ====> getString of the response is null ");
                return null;
            }
            return result;
        } catch (ClientProtocolException e) {
            logger.error(TAG + e.getMessage());
            return null;
        } catch (IOException e) {
            logger.error(TAG + e.getMessage());
            return null;
        } catch (Exception e) {
            logger.error(TAG + e.getMessage());
            return null;
        } finally {
            post.releaseConnection();
        }
    }

    /**
     * 用户使用的方法 功能：请求服务器，返回字符串
     * @param post         post 请求对象
     * @param requestLimit 请求失败限制次数
     * @return
     */
    public static String getString(HttpPost post, int requestLimit) {

        if (requestLimit < 1) {
            return null;
        }
        HttpResponse response;
        int currCount = 0; // 当前请求次数
        String result = null;

        while (currCount < requestLimit) {

            HttpClient httpClient = getHttpClient();
            currCount++;
            try {
                response = httpClient.execute(post);
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    return EntityUtils.toString(response.getEntity());
                } else {
                    post.abort();
                    logger.error(TAG + " ErrCode=" + response.getStatusLine().getStatusCode());
                }
            } catch (ClientProtocolException e) {
                logger.error(TAG + e.getMessage());
                if (currCount > requestLimit) {
                    break;
                }
                logger.info(TAG + " getString() failed!  try " + currCount + " times!");
            } catch (IOException e) {
                logger.error(TAG + e.getMessage());
                if (currCount > requestLimit) {
                    break;
                }
            } finally {
                post.releaseConnection();
            }
        }
        return result;
    }

    /**
     * 用户使用的方法 功能：请求服务器，返回字符串
     * @param uri          字符串形式的请求地址
     * @param requestLimit 最多允许的请求失败次数
     * @return
     */
    public static String getString(String uri, int requestLimit) {

        if (requestLimit < 1) {
            return null;
        }
        HttpResponse response;
        int currCount = 0; // 当前请求次数
        String result = null;
        HttpPost post = getPost(uri, null);
        while (currCount < requestLimit) {

            HttpClient httpClient = getHttpClient();
            currCount++;
            try {
                response = httpClient.execute(post);
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    return EntityUtils.toString(response.getEntity());
                } else {
                    post.abort();
                    logger.info(TAG + " url=" + uri + " ErrCode=" + response.getStatusLine().getStatusCode());
                }
            } catch (ClientProtocolException e) {
                logger.error(TAG + e.getMessage());
                if (currCount > requestLimit) {
                    break;
                }
                logger.error(TAG + " getString() failed!  try " + currCount + " times!");
            } catch (IOException e) {
                if (currCount > requestLimit) {
                    break;
                }
                logger.error(TAG + " getString() failed!  try " + currCount + " times!");
            } finally {
                if (post != null) post.releaseConnection();
            }
        }
        return result;
    }

    /**
     * 释放建立http请求占用的资源
     */
    public static void shutdown() {
        // 释放建立http请求占用的资源
        httpClient.getConnectionManager().shutdown();
        httpClient = null;
    }

    /**
     * 发送get请求
     * @param url
     * @param charSet
     * @return
     */
    public static String sendGet(String url, String charSet) {

        HttpClient httpClient = new DefaultHttpClient();
        String result = null;
        try {
            HttpGet get = new HttpGet(url);
            HttpResponse response = httpClient.execute(get);
            HttpEntity entity = response.getEntity();
            result = charSet != null ? EntityUtils.toString(entity, charSet) : EntityUtils.toString(entity);
        } catch (Exception e) {
            logger.error("http请求报错", e);
            return null;
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
        return result;
    }

    /**
     * 发送get请求
     * @param baseUrl
     * @param params
     * @return
     */
    public static String sendGet(String baseUrl, List<BasicNameValuePair> params) {

        return sendGet(baseUrl, params, "UTF-8");
    }

    /**
     * 发送get请求
     * @param baseUrl
     * @param params
     * @param charSet
     * @return
     */
    public static String sendGet(String baseUrl, List<BasicNameValuePair> params, String charSet) {

        String result = null;
        try {
            URIBuilder uri = new URIBuilder(baseUrl);
            for (BasicNameValuePair bnvp : params) {
                uri.addParameter(bnvp.getName(), bnvp.getValue());
            }

            URI url = uri.build();
            HttpGet get = new HttpGet(url);
            HttpResponse response = httpClient.execute(get);
            HttpEntity entity = response.getEntity();
            result = charSet != null ? EntityUtils.toString(entity, charSet) : EntityUtils.toString(entity);
        } catch (Exception e) {
            logger.error("http请求报错", e);
            return null;
        } finally {
            httpClient.getConnectionManager().shutdown();
        }
        return result;
    }

    /**
     * 获得线程安全的HttpClient对象，能够适应多线程环境
     * @param timeout
     * @param sotimeout
     * @param charset
     * @return HttpClient
     */
    public static synchronized HttpClient getHttpClient(Integer timeout, Integer sotimeout, String charset) {

        if (null == httpClient) {
            HttpParams params = new BasicHttpParams();
            // 设置一些基本参数
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, charset);
            HttpProtocolParams.setUseExpectContinue(params, true);
            HttpProtocolParams.setUserAgent(params, "Mozilla/5.0(Linux;U;Android 2.2.1;en-us;Nexus One Build.FRG83) " + "AppleWebKit/553.1(KHTML,like Gecko) Version/4.0 Mobile Safari/533.1");
            params.setIntParameter(ConnManagerPNames.MAX_TOTAL_CONNECTIONS, 500);
            params.setParameter(ConnManagerPNames.MAX_CONNECTIONS_PER_ROUTE, new ConnPerRouteBean(500));

            /* 连接超时 */
            HttpConnectionParams.setConnectionTimeout(params, timeout);
            /* 请求超时 */
            HttpConnectionParams.setSoTimeout(params, sotimeout);

            // 设置我们的HttpClient支持HTTP和HTTPS两种模式
            SchemeRegistry schReg = new SchemeRegistry();
            schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            schReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

            // 使用线程安全的连接管理来创建HttpClient
            ThreadSafeClientConnManager conMgr = new ThreadSafeClientConnManager(params, schReg);
            httpClient = new DefaultHttpClient(conMgr, params);

        }
        ThreadSafeClientConnManager conMgr = (ThreadSafeClientConnManager) httpClient.getConnectionManager();
        logger.info(String.format("Http connections in pool size:", conMgr.getConnectionsInPool()));
        return httpClient;
    }

    /**
     * 获得线程安全的HttpClient对象，能够适应多线程环境
     * @param timeout 超时时间
     */
    public static HttpClient getHttpClient(Integer timeout) {

        return getHttpClient(timeout, SO_TIMEOUT, CHAR_SET);
    }

    /**
     * 用户使用的方法 功能：从服务器获得字符串
     * @param post
     * @return
     */
    public static String getStringTimeout(HttpPost post, Integer timeout) {

        HttpClient httpClient = getHttpClient(timeout, timeout, CHAR_SET);
        HttpResponse response;
        try {
            response = httpClient.execute(post);
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                post.abort();
                logger.info(TAG + " getString() failed!  Abort Connection.");
                return null;
            }
            return EntityUtils.toString(response.getEntity());
        } catch (ClientProtocolException e) {
            logger.error(TAG + e.getMessage());
            return null;
        } catch (IOException e) {
            logger.error(TAG + e.getMessage());
            return null;
        } finally {
        }
    }

    /**
     * 用户使用的方法 功能：从服务器获得HttpResponse对象
     * @param post
     * @return
     * @author xiongpin
     */
    public static HttpResponse getResponse(HttpPost post) {

        try {
            HttpClient httpClient = getHttpClient();
            HttpResponse response;
            response = httpClient.execute(post);
            if (null == response) {  //  1.) 考虑 response 中 entity 中输入流是否存在
                logger.error(ExecuteContext.httpByBussiness.get() + " ============> inputStream of the response is null ");
                return null;
            }
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                post.abort();
                logger.error(TAG + " getString() failed!  Abort Connection ErrCode=" + response.getStatusLine().getStatusCode() + " Url:" + post.getURI());
                return null;
            }
            return response;
        } catch (ClientProtocolException e) {
            logger.error(TAG + e.getMessage());
            return null;
        } catch (IOException e) {
            logger.error(TAG + e.getMessage());
            return null;
        } catch (Exception e) {
            logger.error(TAG + e.getMessage());
            return null;
        } finally {
            post.releaseConnection();
        }
    }
}
