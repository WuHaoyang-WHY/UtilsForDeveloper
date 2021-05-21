import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;

/**
 * json与类的转换工具
 *
 */

public class JsonUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public static <T> T jsonToObject(String json, Class<?> collectionClass, Class<?>... elementClasses) {
        if (StringUtils.isBlank(json)) {
            return null;
        }
        JavaType javaType = OBJECT_MAPPER.getTypeFactory().constructParametricType(collectionClass, elementClasses);
        try {
            return OBJECT_MAPPER.readValue(json, javaType);
        } catch (Exception e) {
            log.warn("Parse Json to Object error", e);
            return null;
        }
    }

    public static String object2Json(Object object, boolean containNull) {
        return JSONObject.toJSONString(object);
    }

}