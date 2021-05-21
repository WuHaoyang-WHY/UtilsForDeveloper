import lombok.extern.log4j.Log4j2;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;


public class NumUtil {

    /**
     * 计算百分比
     * @return result eg:0.9302
     */
    public static double calPercent(Object numerator, Object denominator) {
        double result = 0.0;
        if (null == numerator || null == denominator) {
            return result;
        }
        try {
            BigDecimal num = objToDecimal(numerator);
            BigDecimal den = objToDecimal(denominator);
            if (null == num || null == den || 0 == num.compareTo(BigDecimal.ZERO) || 0 == den.compareTo(BigDecimal.ZERO)) {
                return result;
            }
            result = num.divide(den, 4, RoundingMode.HALF_UP).doubleValue();
        } catch (Exception e) {
            log.error("NumUtil.calPercent error", e);
        }
        return result;
    }

    private static BigDecimal objToDecimal(Object obj) {
        BigDecimal ret = null;
        if( obj != null ) {
            if( obj instanceof BigDecimal ) {
                ret = (BigDecimal) obj;
            } else if( obj instanceof String ) {
                ret = new BigDecimal( (String) obj );
            } else if( obj instanceof BigInteger ) {
                ret = new BigDecimal( (BigInteger) obj );
            } else if( obj instanceof Number ) {
                ret = new BigDecimal( ((Number)obj).doubleValue() );
            } else {
                throw new ClassCastException("Not possible to coerce ["+obj+"] from class "+obj.getClass()+" into a BigDecimal.");
            }
        }
        return ret;
    }
}