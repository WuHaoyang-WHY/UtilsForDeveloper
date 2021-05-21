import org.apache.commons.lang.StringUtils;

public class BooleanUtil {

    /**
     * 将boolean转换为"Y"或"N",true为"Y",false为"N"
     *
     * @param enable bool值
     * @return 结果
     */
    public static String toYN(boolean enable) {
        return enable ? "Y" : "N";
    }


    /**
     * 将string转换为boolean
     * 1. Y/N, Y=true, N=false, other=false
     * 2. true/false,  "true"=true,"false"=false,other=false
     *
     * @param value 值
     * @return 结果
     */
    public static boolean toBoolean(String value) {
        if (StringUtils.equalsIgnoreCase(value, "Y")) {
            return true;
        }

        if (StringUtils.equalsIgnoreCase(value, "true")) {
            return true;
        }
        return false;
    }
}