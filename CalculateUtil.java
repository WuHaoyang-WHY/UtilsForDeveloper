import com.google.common.base.Preconditions;
import com.google.common.math.Stats;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;

public class CalculateUtil {

    /**
     * 计算比率
     *
     * @param numerator   分子
     * @param denominator 分母
     * @return
     */
    public static String divide(int numerator, int denominator) {
        if (denominator == 0) {
            return "0.00%";
        }
        double tempResult = (double) numerator / (double) denominator;
        DecimalFormat df = new DecimalFormat("0.00%");
        return df.format(tempResult);
    }

    public static int percentCompare(String percentValue1, String percentValue2) {
        Preconditions.checkArgument(Objects.nonNull(percentValue1) && Objects.nonNull(percentValue2), "参数不能为空");
        BigDecimal dataA = new BigDecimal(percentValue1.replace("%", ""));
        BigDecimal dataB = new BigDecimal(percentValue2.replace("%", ""));
        return dataA.compareTo(dataB);
    }

    /**
     * 三倍标准差的方式判断异常点
     * 注 ： 目前只针对上涨的情况标记为异常点
     *
     * @param target       目标值
     * @param standardList 标准数组
     * @return 是否为异常点
     */
    public static boolean isAbnormal(Number target, List<? extends Number> standardList) {
        double avg = Stats.of(standardList).mean();
        double standardDeviation = Stats.of(standardList).populationStandardDeviation();
        return target.doubleValue() > avg + 3 * standardDeviation;
    }
}