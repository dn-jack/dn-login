package com.dongnao.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Map.Entry;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.springframework.util.StringUtils;

/**
 * http、https 请求工具类， 微信为https的请求
 * @author yehx
 *
 */
public class HttpUtil {
    
    private static final String DEFAULT_CHARSET = "UTF-8";
    
    private static final String _GET = "GET"; // GET
    
    private static final String _POST = "POST";// POST
    
    public static final int DEF_CONN_TIMEOUT = 30000;
    
    public static final int DEF_READ_TIMEOUT = 30000;
    
    /**
     * 初始化http请求参数
     * 
     * @param url
     * @param method
     * @param headers
     * @return
     * @throws Exception
     */
    private static HttpURLConnection initHttp(String url, String method,
            Map<String, String> headers) throws Exception {
        URL _url = new URL(url);
        HttpURLConnection http = (HttpURLConnection)_url.openConnection();
        // 连接超时
        http.setConnectTimeout(DEF_CONN_TIMEOUT);
        // 读取超时 --服务器响应比较慢，增大时间
        http.setReadTimeout(DEF_READ_TIMEOUT);
        http.setUseCaches(false);
        http.setRequestMethod(method);
        http.setRequestProperty("Content-Type",
                "application/x-www-form-urlencoded");
        http.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36");
        if (null != headers && !headers.isEmpty()) {
            for (Entry<String, String> entry : headers.entrySet()) {
                http.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        http.setDoOutput(true);
        http.setDoInput(true);
        http.connect();
        return http;
    }
    
    /**
     * 初始化http请求参数
     * 
     * @param url
     * @param method
     * @return
     * @throws Exception
     */
    private static HttpsURLConnection initHttps(String url, String method,
            Map<String, String> headers) throws Exception {
        TrustManager[] tm = {new MyX509TrustManager()};
        System.setProperty("https.protocols", "TLSv1");
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tm, new java.security.SecureRandom());
        // 从上述SSLContext对象中得到SSLSocketFactory对象
        SSLSocketFactory ssf = sslContext.getSocketFactory();
        URL _url = new URL(url);
        HttpsURLConnection http = (HttpsURLConnection)_url.openConnection();
        http.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        // 设置域名校验
        http.setHostnameVerifier(new HttpUtil().new TrustAnyHostnameVerifier());
        // 连接超时
        http.setConnectTimeout(DEF_CONN_TIMEOUT);
        // 读取超时 --服务器响应比较慢，增大时间
        http.setReadTimeout(DEF_READ_TIMEOUT);
        http.setUseCaches(false);
        http.setRequestMethod(method);
        http.setRequestProperty("Content-Type",
                "application/x-www-form-urlencoded");
        http.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36");
        if (null != headers && !headers.isEmpty()) {
            for (Entry<String, String> entry : headers.entrySet()) {
                http.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        http.setSSLSocketFactory(ssf);
        http.setDoOutput(true);
        http.setDoInput(true);
        http.connect();
        return http;
    }
    
    /**
     * 
     * @description 功能描述: get 请求
     * @return 返回类型:
     * @throws Exception
     */
    public static String get(String url, Map<String, String> params,
            Map<String, String> headers) throws Exception {
        HttpURLConnection http = null;
        if (isHttps(url)) {
            http = initHttps(initParams(url, params), _GET, headers);
        }
        else {
            http = initHttp(initParams(url, params), _GET, headers);
        }
        InputStream in = http.getInputStream();
        BufferedReader read = new BufferedReader(new InputStreamReader(in,
                DEFAULT_CHARSET));
        String valueString = null;
        StringBuffer bufferRes = new StringBuffer();
        while ((valueString = read.readLine()) != null) {
            bufferRes.append(valueString);
        }
        in.close();
        if (http != null) {
            http.disconnect();// 关闭连接
        }
        return bufferRes.toString();
    }
    
    public static String get(String url) throws Exception {
        return get(url, null);
    }
    
    public static String get(String url, Map<String, String> params)
            throws Exception {
        return get(url, params, null);
    }
    
    public static String post(String url, String params) throws Exception {
        HttpURLConnection http = null;
        if (isHttps(url)) {
            http = initHttps(url, _POST, null);
        }
        else {
            http = initHttp(url, _POST, null);
        }
        OutputStream out = http.getOutputStream();
        out.write(params.getBytes(DEFAULT_CHARSET));
        out.flush();
        out.close();
        
        InputStream in = http.getInputStream();
        BufferedReader read = new BufferedReader(new InputStreamReader(in,
                DEFAULT_CHARSET));
        String valueString = null;
        StringBuffer bufferRes = new StringBuffer();
        while ((valueString = read.readLine()) != null) {
            bufferRes.append(valueString);
        }
        in.close();
        if (http != null) {
            http.disconnect();// 关闭连接
        }
        return bufferRes.toString();
    }
    
    /**
     * 功能描述: 构造请求参数
     * 
     * @return 返回类型:
     * @throws Exception
     */
    public static String initParams(String url, Map<String, String> params)
            throws Exception {
        if (null == params || params.isEmpty()) {
            return url;
        }
        StringBuilder sb = new StringBuilder(url);
        if (url.indexOf("?") == -1) {
            sb.append("?");
        }
        sb.append(map2Url(params));
        return sb.toString();
    }
    
    /**
     * map构造url
     * 
     * @return 返回类型:
     * @throws Exception
     */
    public static String map2Url(Map<String, String> paramToMap)
            throws Exception {
        if (null == paramToMap || paramToMap.isEmpty()) {
            return null;
        }
        StringBuffer url = new StringBuffer();
        boolean isfist = true;
        for (Entry<String, String> entry : paramToMap.entrySet()) {
            if (isfist) {
                isfist = false;
            }
            else {
                url.append("&");
            }
            url.append(entry.getKey()).append("=");
            String value = entry.getValue();
            if (!StringUtils.isEmpty(value)) {
                url.append(URLEncoder.encode(value, DEFAULT_CHARSET));
            }
        }
        return url.toString();
    }
    
    /**
     * 检测是否https
     * 
     * @param url
     */
    private static boolean isHttps(String url) {
        return url.startsWith("https");
    }
    
    /**
     * https 域名校验
     * 
     * @param url
     * @param params
     * @return
     */
    public class TrustAnyHostnameVerifier implements HostnameVerifier {
        public boolean verify(String hostname, SSLSession session) {
            return true;// 直接返回true
        }
    }
    
    private static class MyX509TrustManager implements X509TrustManager {
        
        @Override
        public void checkClientTrusted(X509Certificate[] arg0, String arg1)
                throws CertificateException {
        }
        
        @Override
        public void checkServerTrusted(X509Certificate[] arg0, String arg1)
                throws CertificateException {
        }
        
        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
        
    }
    
    public static String doGet(String url, String cookie, String traceCode) {
        String result = "";
        //      HttpGet httpRequst = new HttpGet(URI uri);
        //      HttpGet httpRequst = new HttpGet(String uri);
        //      创建HttpGet或HttpPost对象，将要请求的URL通过构造方法传入HttpGet或HttpPost对象。
        HttpGet httpRequst = new HttpGet(url);
        httpRequst.setHeader("Cookie", cookie);
        httpRequst.setHeader("tracecode", traceCode);
        
        // 生成一个http客户端
        HttpClient httpClient = getHttpClient();
        //      new DefaultHttpClient().execute(HttpUriRequst requst);
        try {
            //使用DefaultHttpClient类的execute方法发送HTTP GET请求，并返回HttpResponse对象。
            HttpResponse httpResponse = httpClient.execute(httpRequst);//其中HttpGet是HttpUriRequst的子类
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                HttpEntity httpEntity = httpResponse.getEntity();
                result = EntityUtils.toString(httpEntity);//取出应答字符串
                // 一般来说都要删除多余的字符 
                result.replaceAll("\r", "");//去掉返回结果中的"\r"字符，否则会在结果字符串后面显示一个小方格  
            }
            else {
                httpRequst.abort();
            }
        }
        catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            result = e.getMessage().toString();
        }
        catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            result = e.getMessage().toString();
        }
        return result;
    }
    
    private static DefaultHttpClient getHttpClient() {
        try {
            // 禁止https证书验证  
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);//运行所有的hostname验证  
            
            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
            
            // 禁用Cookie2请求头  
            HttpClientParams.setCookiePolicy(params, CookiePolicy.RFC_2109);
            HttpClientParams.setCookiePolicy(params,
                    CookiePolicy.BROWSER_COMPATIBILITY);
            HttpClientParams.setCookiePolicy(params, CookiePolicy.NETSCAPE);
            
            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http",
                    PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));
            
            ClientConnectionManager ccm = new ThreadSafeClientConnManager(
                    params, registry);
            
            HttpConnectionParams.setConnectionTimeout(params, 3000);
            HttpConnectionParams.setSoTimeout(params, 5000);
            
            return new DefaultHttpClient(ccm, params);
        }
        catch (Exception e) {
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
            HttpConnectionParams.setSoTimeout(httpParams, 5000);
            
            return new DefaultHttpClient(httpParams);
        }
    }
}
