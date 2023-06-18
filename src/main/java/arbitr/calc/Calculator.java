package arbitr.calc;

import arbitr.Swap;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.Optional;

@Log4j2
public class Calculator {

    public static Optional<BigDecimal> calculate(Swap[] swaps) {
        BigDecimal conversionRatio = getConversionRatio(swaps);
        log.info("conversionRatio = " + conversionRatio);
        BigDecimal conversionWithFee = getConversionWithFee(conversionRatio);
        BigDecimal percent = conversionWithFee.subtract(BigDecimal.valueOf(1)).multiply(BigDecimal.valueOf(100));
        return percent.compareTo(BigDecimal.valueOf(0)) > 0 ? Optional.of(percent) : Optional.empty();
    }

    @NotNull
    static BigDecimal getConversionWithFee(BigDecimal conversionRatio) {
        return conversionRatio.subtract(BigDecimal.valueOf(0.003)); // 1.04 104% - 0.3% = 103.7% -> 1.037
    }

    @NotNull
    static BigDecimal getConversionRatio(Swap[] swaps) {
        BigDecimal result = BigDecimal.ONE;
        for (Swap swap : swaps) {
            result = result.multiply(swap.getRatio());
        }
        return result;
    }
}
