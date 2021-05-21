import java.util.HashMap;
import java.util.Map;

public class UrlUtil {

    /**
     * 去掉url中的路径，留下请求参数部分
     * @param strURL url地址
     * @return url请求参数部分
     */
    private static String truncateUrlPage(String strURL){
        String strAllParam = null;
        String[] arrSplit = strURL.split("[?]");
        if(arrSplit.length > 1 && arrSplit[1] != null){
           strAllParam = arrSplit[1];
        }
        return strAllParam;
    }

    /**
     * 解析出url参数中的键值对
     * 如 "index.jsp?Action=del&id=123"，解析出Action:del,id:123存入map中
     * @param URL url地址
     * @return url请求参数部分
     */
    public static Map<String, String> getUrlParams(String URL)
    {
        Map<String, String> mapRequest = new HashMap<String, String>(16);
        String strUrlParam = truncateUrlPage(URL);
        if(strUrlParam == null) {
            return mapRequest;
        }
        //每个键值为一组
        String[] arrSplit = strUrlParam.split("[&]");
        for(String strSplit : arrSplit) {
            String[] arrSplitEqual=null;
            arrSplitEqual = strSplit.split("[=]");
            //解析出键值
            if(arrSplitEqual.length > 1) {
                mapRequest.put(arrSplitEqual[0], arrSplitEqual[1]);
            }
        }
        return mapRequest;
    }
}

