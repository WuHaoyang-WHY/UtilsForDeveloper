import com.alibaba.fastjson.JSON;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.net.HttpHeaders;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

@Log4j2
public class HttpUtil {
    /**
     * 接收Json格式响应数据
     */
    public static final  String ACCEPT_CONTENT_TYPE     = "application/json";
    /**
     * 默认的请求内容字符集
     */
    private static final String DEFAULT_CONTENT_CHARSET = "UTF-8";
    /**
     * 以Json格式提交请求时需要设置的content-type头
     */
    private static final String CONTENT_TYPE_JSON       = "application/json";
    private static final int    LOG_RESPONSE_MAX_WIDTH  = 1024;

    /**
     * 跟踪一次http请求的上下文对象
     */
    private static class HttpRequestTraceContext {
        // http method
        private String method;
        private String url;
        private String params;
        // 此次http请示标识
        private String requestId;
        // 此次http请求的计时器
        private Timer  timer;
        // http请求响应结果
        private String response;

        public HttpRequestTraceContext(String method, String url, String params) {
            this.timer = new Timer().start();
            this.requestId = String.valueOf(timer.getStartTime());
            this.method = method;
            this.url = url;
            this.params = params;
        }

        public String getRequestId() {
            return requestId;
        }

        public void setResponse(String response) {
            this.response = response;
        }

        /**
         * 执行Http请求后打印响应结果和耗时
         */
        public void logAfterRequest() {
            timer.end();
        }

        /**
         * 记录Http请求耗时
         */
        private static class Timer {
            private long startTime;
            private long endTime;

            public long getStartTime() {
                return startTime;
            }

            public Timer start() {
                startTime = System.currentTimeMillis();
                return this;
            }

            public Timer end() {
                endTime = System.currentTimeMillis();
                return this;
            }

            public long getConsumedTime() {
                return endTime - startTime;
            }
        }
    }

    /**
     * 参数转换为键值对
     *
     * @param data 请求参数
     * @return
     */
    private static List<NameValuePair> toNameValuePairs(Map<String, Object> data) {
        List<NameValuePair> pairs = Lists.newArrayList();
        if (data == null || data.isEmpty()) {
            return pairs;
        }
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (entry.getValue() instanceof List<?>) {
                for (Object element : (List<?>) entry.getValue()) {
                    if (element == null) {
                        continue;
                    }

                    pairs.add(new NameValuePair(entry.getKey(), element.toString()));
                }
            } else {
                if (entry.getValue() == null) {
                    continue;
                }

                pairs.add(new NameValuePair(entry.getKey(), entry.getValue().toString()));
            }
        }
        return pairs;
    }

    /**
     * 执行Http请求
     *
     * @param method http method对象
     * @param encode http content charset
     * @return 成功时返回请求结果
     * @throws IOException
     */
    private static String execute(HttpMethod method, Map<String, String> headers, String encode,
                                  HttpRequestTraceContext context) throws Exception {

        return execute(method, headers, encode, context, false);
    }

    private static String execute(HttpMethod method, String encode,
                                  HttpRequestTraceContext context) throws Exception {
        return execute(method, null, encode, context);
    }

    private static String execute(HttpMethod method, Map<String, String> headers, String encode,
                                  HttpRequestTraceContext context, boolean isLargeResponseBody) throws Exception {
        method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, encode);
        method.setRequestHeader(HttpHeaders.ACCEPT, ACCEPT_CONTENT_TYPE);

        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                method.setRequestHeader(header.getKey(), header.getValue());
            }
        }

        String result = "";
        try {
            int statusCode = new HttpClient().executeMethod(method);
            if (isLargeResponseBody) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(method.getResponseBodyAsStream()));
                StringBuilder builder = new StringBuilder();
                String str = "";
                while ((str = reader.readLine()) != null) {
                    builder.append(str);
                }
                result = builder.toString();
            } else {
                result = method.getResponseBodyAsString();
            }
            if (statusCode != HttpStatus.SC_OK) {
                throw new RuntimeException(String.format("unexpected http status code: %s %s",
                        statusCode, method.getStatusText()));
            }
            return result;
        } catch (Exception ex) {
            throw new Exception(ex);
        } finally {
            method.releaseConnection();
            // 请求结束
            context.setResponse(result);
            context.logAfterRequest();
        }
    }

    /**
     * 以GET方法提交请求(无header)
     *
     * @param url    请求地址
     * @param params 请求参数
     * @return 成功时返回请求结果
     * @throws Exception
     */
    public static String get(String url, Map<String, Object> params) throws Exception {
        return get(url, null, params);
    }

    public static String get(String url, List<NameValuePair> params) throws Exception {
        return get(url, null, params);
    }

    /**
     * 以GET方法提交请求(有header)
     *
     * @param url    请求地址
     * @param params 请求参数
     * @return 成功时返回请求结果
     * @throws Exception
     */
    public static String get(String url, Map<String, String> headers,
                             Map<String, Object> params) throws Exception {
        List<NameValuePair> pairs = toNameValuePairs(params);
        GetMethod getMethod = new GetMethod(url);
        if (!pairs.isEmpty()) {
            getMethod.setQueryString(Iterables.toArray(pairs, NameValuePair.class));
        }
        return execute(getMethod, headers, DEFAULT_CONTENT_CHARSET,
                new HttpRequestTraceContext(getMethod.getName(), url, JSON.toJSONString(params)));

    }

    public static String get(String url, Map<String, String> headers,
                             List<NameValuePair> params) throws Exception {
        GetMethod getMethod = new GetMethod(url);
        if (!params.isEmpty()) {
            getMethod.setQueryString(Iterables.toArray(params, NameValuePair.class));
        }
        return execute(getMethod, headers, DEFAULT_CONTENT_CHARSET,
                new HttpRequestTraceContext(getMethod.getName(), url, JSON.toJSONString(params)));
    }

    /**
     * 以Json格式提交请求
     *
     * @param url  请求地址
     * @param data 请求数据
     * @return 成功时返回请求结果
     * @throws Exception
     */
    public static String postAsJson(String url, String data) throws Exception {
        PostMethod postMethod = new PostMethod(url);
        postMethod.setRequestEntity(
                new StringRequestEntity(data, CONTENT_TYPE_JSON, DEFAULT_CONTENT_CHARSET));
        return execute(postMethod, DEFAULT_CONTENT_CHARSET,
                new HttpRequestTraceContext(postMethod.getName(), url, data));
    }

    /**
     * 以Json格式提交请求
     *
     * @param url  请求地址
     * @param data 请求数据
     * @return 成功时返回请求结果
     * @throws Exception
     */
    public static String postAsJson(String url, Map<String, String> headers,
                                    String data) throws Exception {
        PostMethod postMethod = new PostMethod(url);
        postMethod.setRequestEntity(
                new StringRequestEntity(data, CONTENT_TYPE_JSON, DEFAULT_CONTENT_CHARSET));
        return execute(postMethod, headers, DEFAULT_CONTENT_CHARSET,
                new HttpRequestTraceContext(postMethod.getName(), url, data));
    }

    /**
     * 对于大的response body使用该方法
     *
     * @param url
     * @param headers
     * @param data
     * @return
     * @throws Exception
     */
    public static String postAsJsonWithLargeResponse(String url, Map<String, String> headers,
                                                     String data) throws Exception {
        PostMethod postMethod = new PostMethod(url);
        postMethod.setRequestEntity(
                new StringRequestEntity(data, CONTENT_TYPE_JSON, DEFAULT_CONTENT_CHARSET));
        return execute(postMethod, headers, DEFAULT_CONTENT_CHARSET,
                new HttpRequestTraceContext(postMethod.getName(), url, data), true);
    }

    /**
     * 以POST方法提交请求(无header)
     *
     * @param url    请求地址
     * @param params 请求参数
     * @return 成功时返回请求结果
     * @throws Exception
     */
    public static String post(String url, Map<String, Object> params) throws Exception {
        List<NameValuePair> pairs = toNameValuePairs(params);
        PostMethod postMethod = new PostMethod(url);
        if (!pairs.isEmpty()) {
            postMethod.setRequestBody(Iterables.toArray(pairs, NameValuePair.class));
        }

        return execute(postMethod, DEFAULT_CONTENT_CHARSET,
                new HttpRequestTraceContext(postMethod.getName(), url, JSON.toJSONString(params)));
    }

    /**
     * 以POST方法提交请求(有header)
     *
     * @param url    请求地址
     * @param params 请求参数
     * @return 成功时返回请求结果
     * @throws Exception
     */
    public static String post(String url, Map<String, String> headers,
                              Map<String, Object> params) throws Exception {
        List<NameValuePair> pairs = toNameValuePairs(params);
        PostMethod postMethod = new PostMethod(url);
        if (!pairs.isEmpty()) {
            postMethod.setRequestBody(Iterables.toArray(pairs, NameValuePair.class));
        }

        return execute(postMethod, headers, DEFAULT_CONTENT_CHARSET,
                new HttpRequestTraceContext(postMethod.getName(), url, JSON.toJSONString(params)));
    }
}
