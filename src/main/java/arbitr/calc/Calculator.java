package arbitr.calc;

import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Log4j2
public class Calculator {

    public static Optional<BigDecimal> calculate(BigDecimal dogeBtc, BigDecimal kcsBtc, BigDecimal dogeeKcs) {
        BigDecimal convertionRatio = getConvertionRatio(dogeBtc, kcsBtc, dogeeKcs);
        log.info("convertionRatio = " + convertionRatio + " dogeBtc: " + dogeBtc + " kcsBtc:" + kcsBtc + " dogeeKcs:" + dogeeKcs);
        BigDecimal convertionWithFee = getConvertionWithFee(convertionRatio);
        BigDecimal percent = convertionWithFee.subtract(BigDecimal.valueOf(1)).multiply(BigDecimal.valueOf(100));
        return percent.compareTo(BigDecimal.valueOf(1)) > 0 ? Optional.of(percent) : Optional.empty();
    }

    @NotNull
    static BigDecimal getConvertionWithFee(BigDecimal convertionRatio) {
        return convertionRatio.subtract(BigDecimal.valueOf(0.003)); // 1.04 104% - 0.3% = 103.7% -> 1.037
    }

    @NotNull
    static BigDecimal getConvertionRatio(BigDecimal dogeBtc, BigDecimal kcsBtc, BigDecimal dogeeKcs) {
        return dogeBtc.divide(kcsBtc, 10, RoundingMode.HALF_UP).divide(dogeeKcs, 10, RoundingMode.HALF_UP);
    }
}
