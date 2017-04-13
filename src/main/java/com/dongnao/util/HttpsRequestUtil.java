package com.dongnao.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HttpsRequestUtil {
    
    private static final String METHOD_POST = "POST";
    
    private static final String DEFAULT_CHARSET = "utf-8";
    
    public static String doPost(String url, String params, String charset,
            int connectTimeout, int readTimeout) throws Exception {
        String ctype = "application/json;charset=" + charset;
        byte[] content = {};
        if (params != null) {
            content = params.getBytes(charset);
        }
        
        return doPost(url, ctype, content, connectTimeout, readTimeout);
    }
    
    public static Map<String, String> bddoPost(String url, String params,
            String charset, int connectTimeout, int readTimeout,
            String cookies, String tracecode, String P3P) throws Exception {
        String ctype = "application/x-www-form-urlencoded;charset=" + charset;
        byte[] content = {};
        if (params != null) {
            content = params.getBytes(charset);
        }
        
        return bddoPost(url,
                ctype,
                content,
                connectTimeout,
                readTimeout,
                cookies,
                tracecode,
                P3P);
    }
    
    public static Map<String, String> bddoPost(String url, String ctype,
            byte[] content, int connectTimeout, int readTimeout,
            String cookies, String tracecode, String P3P) throws Exception {
        HttpsURLConnection conn = null;
        OutputStream out = null;
        Map<String, String> rsp = null;
        try {
            try {
                SSLContext ctx = SSLContext.getInstance("TLS");
                ctx.init(new KeyManager[0],
                        new TrustManager[] {new DefaultTrustManager()},
                        new SecureRandom());
                SSLContext.setDefault(ctx);
                
                conn = bdgetConnection(new URL(url),
                        METHOD_POST,
                        ctype,
                        cookies,
                        tracecode,
                        P3P);
                conn.setHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });
                conn.setConnectTimeout(connectTimeout);
                conn.setReadTimeout(readTimeout);
            }
            catch (Exception e) {
                throw e;
            }
            try {
                out = conn.getOutputStream();
                out.write(content);
                rsp = getResponseAsStringForbd(conn);
            }
            catch (IOException e) {
                throw e;
            }
            
        }
        finally {
            if (out != null) {
                out.close();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        
        return rsp;
    }
    
    public static String doPost(String url, String ctype, byte[] content,
            int connectTimeout, int readTimeout) throws Exception {
        HttpsURLConnection conn = null;
        OutputStream out = null;
        String rsp = null;
        try {
            try {
                SSLContext ctx = SSLContext.getInstance("TLS");
                ctx.init(new KeyManager[0],
                        new TrustManager[] {new DefaultTrustManager()},
                        new SecureRandom());
                SSLContext.setDefault(ctx);
                
                conn = getConnection(new URL(url), METHOD_POST, ctype);
                conn.setHostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });
                conn.setConnectTimeout(connectTimeout);
                conn.setReadTimeout(readTimeout);
            }
            catch (Exception e) {
                throw e;
            }
            try {
                out = conn.getOutputStream();
                out.write(content);
                rsp = getResponseAsString(conn);
            }
            catch (IOException e) {
                throw e;
            }
            
        }
        finally {
            if (out != null) {
                out.close();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }
        
        return rsp;
    }
    
    private static class DefaultTrustManager implements X509TrustManager {
        
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
    
    private static HttpsURLConnection bdgetConnection(URL url, String method,
            String ctype, String cookies, String tracecode, String P3P)
            throws IOException {
        HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
        conn.setRequestMethod(method);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestProperty("Accept", "text/xml,text/javascript,text/html");
        conn.setRequestProperty("User-Agent", "stargate");
        conn.setRequestProperty("Content-Type", ctype);
        conn.setRequestProperty("Cookie", cookies);
        conn.setRequestProperty("tracecode", tracecode);
        conn.setRequestProperty("P3P", P3P);
        return conn;
    }
    
    private static HttpsURLConnection getConnection(URL url, String method,
            String ctype) throws IOException {
        HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
        conn.setRequestMethod(method);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setRequestProperty("Accept", "text/xml,text/javascript,text/html");
        conn.setRequestProperty("User-Agent", "stargate");
        conn.setRequestProperty("Content-Type", ctype);
        return conn;
    }
    
    protected static String getResponseAsString(HttpURLConnection conn)
            throws IOException {
        
        String charset = getResponseCharset(conn.getContentType());
        InputStream es = conn.getErrorStream();
        if (es == null) {
            return getStreamAsString(conn.getInputStream(), charset);
        }
        else {
            String msg = getStreamAsString(es, charset);
            if (msg == null) {
                throw new IOException(conn.getResponseCode() + ":"
                        + conn.getResponseMessage());
            }
            else {
                throw new IOException(msg);
            }
        }
    }
    
    protected static Map<String, String> getResponseAsStringForbd(
            HttpURLConnection conn) throws IOException {
        
        Map<String, List<String>> map = conn.getHeaderFields();
        for (String key : map.keySet()) {
            System.out.println(key + "--->" + map.get(key));
        }
        
        List<String> cookies1 = map.get("Set-Cookie");
        StringBuffer sb1 = new StringBuffer();
        if (cookies1 != null) {
            for (String cookie : cookies1) {
                sb1.append(cookie).append(";");
            }
        }
        
        String charset = getResponseCharset(conn.getContentType());
        InputStream es = conn.getErrorStream();
        if (es == null) {
            Map<String, String> retMap = new HashMap<String, String>();
            String msg = getStreamAsString(conn.getInputStream(), charset);
            retMap.put("result", msg);
            retMap.put("cookie", sb1.toString());
            return retMap;
        }
        else {
            String msg = getStreamAsString(es, charset);
            if (msg == null) {
                throw new IOException(conn.getResponseCode() + ":"
                        + conn.getResponseMessage());
            }
            else {
                throw new IOException(msg);
            }
        }
    }
    
    private static String getStreamAsString(InputStream stream, String charset)
            throws IOException {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    stream, charset));
            StringWriter writer = new StringWriter();
            
            char[] chars = new char[256];
            int count = 0;
            while ((count = reader.read(chars)) > 0) {
                writer.write(chars, 0, count);
            }
            
            return writer.toString();
        }
        finally {
            if (stream != null) {
                stream.close();
            }
        }
    }
    
    private static String getResponseCharset(String ctype) {
        String charset = DEFAULT_CHARSET;
        
        if (ctype != null && !"".equals(ctype)) {
            String[] params = ctype.split(";");
            for (String param : params) {
                param = param.trim();
                if (param.startsWith("charset")) {
                    String[] pair = param.split("=", 2);
                    if (pair.length == 2) {
                        if (pair[1] != null && !"".equals(pair[1])) {
                            charset = pair[1].trim();
                        }
                    }
                    break;
                }
            }
        }
        
        return charset;
    }
}
