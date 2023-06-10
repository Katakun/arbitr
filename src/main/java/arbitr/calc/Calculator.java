package arbitr.calc;

import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

@Log4j2
public class Calculator {

    public static Optional<BigDecimal> calculate(BigDecimal dogeBtc, BigDecimal kcsBtc, BigDecimal dogeKcs) {
        BigDecimal conversionRatio = getConversionRatio(dogeBtc, kcsBtc, dogeKcs);
        log.info("conversionRatio = " + conversionRatio + " dogeBtc: " + dogeBtc + " kcsBtc:" + kcsBtc + " dogeKcs:" + dogeKcs);
        BigDecimal conversionWithFee = getConversionWithFee(conversionRatio);
        BigDecimal percent = conversionWithFee.subtract(BigDecimal.valueOf(1)).multiply(BigDecimal.valueOf(100));
        return percent.compareTo(BigDecimal.valueOf(0)) > 0 ? Optional.of(percent) : Optional.empty();
    }

    @NotNull
    static BigDecimal getConversionWithFee(BigDecimal conversionRatio) {
        return conversionRatio.subtract(BigDecimal.valueOf(0.003)); // 1.04 104% - 0.3% = 103.7% -> 1.037
    }

    @NotNull
    static BigDecimal getConversionRatio(BigDecimal dogeBtc, BigDecimal kcsBtc, BigDecimal dogeKcs) {
        return dogeBtc.divide(kcsBtc, 10, RoundingMode.HALF_UP).divide(dogeKcs, 10, RoundingMode.HALF_UP);
    }
}
