package com.domain.common.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.net.ssl.*;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * https 请求
 * @author kevin zhang
 */
public class HttpsRequest {

    private static Log log = LogFactory.getLog(HttpsRequest.class);

    private static class TrustAnyTrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(java.security.cert.X509Certificate[] arg0, String arg1) throws java.security.cert.CertificateException {

        }

        @Override
        public void checkServerTrusted(java.security.cert.X509Certificate[] arg0, String arg1) throws java.security.cert.CertificateException {

        }

        @Override
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {

            return null;
        }

    }

    private static class TrustAnyHostnameVerifier implements HostnameVerifier {

        public boolean verify(String hostname, SSLSession session) {

            return true;
        }
    }

    private static byte[] request(String url, String content, String charset, String method) throws Exception {

        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, new TrustManager[]{new TrustAnyTrustManager()}, new java.security.SecureRandom());

        URL console = new URL(url);
        HttpsURLConnection conn = (HttpsURLConnection) console.openConnection();
        conn.setSSLSocketFactory(sc.getSocketFactory());
        conn.setHostnameVerifier(new TrustAnyHostnameVerifier());
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setRequestMethod(method);
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/40.0.2214.94 Safari/537.36");
        conn.connect();

        DataOutputStream out = new DataOutputStream(conn.getOutputStream());
        out.write(content.getBytes(charset));
        out.flush();
        out.close();
        log.info(conn.getHeaderFields());
        InputStream is = conn.getInputStream();

        if (is != null) {
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                outStream.write(buffer, 0, len);
            }
            is.close();
            return outStream.toByteArray();
        }
        return null;
    }

    private static String map2Str(Map<String, Object> map, String charset) throws Exception {

        if (map == null || map.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (String key : map.keySet()) {
            sb.append(key);
            sb.append("=");
            sb.append(URLEncoder.encode(map.get(key).toString(), charset));
            sb.append("&");
        }
        String content = sb.toString();
        content = content.substring(0, content.length() - 1);
        return content;
    }

    /**
     * @param url
     * @param params
     * @param charset
     * @return
     * @throws Exception
     */
    public static String post(String url, Map<String, Object> params, String charset) throws Exception {

        byte bb[] = request(url, map2Str(params, charset), charset, "POST");
        if (bb != null) {
            return new String(bb, charset);
        }
        return null;
    }

    /**
     * @param url
     * @param params 参数，格式为：user=aa&pwd=bb
     * @param charset
     * @return
     * @throws Exception
     */
    public static String get(String url, Map<String, Object> params, String charset) throws Exception {

        byte bb[] = request(url, map2Str(params, charset), charset, "GET");
        if (bb != null) {
            return new String(bb, charset);
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        // Map<String, String> map = new HashMap<String, String>();
        System.out.println(post("https://boss.vvipone.com/api/workflow/smsBack.html", null, "utf-8"));

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("aa", "a1");
        map.put("bb", "中国");
        map.put("cc", "&");
        System.out.println(map2Str(map, "utf-8"));
    }
}
