package arbitr.calc;

import arbitr.Swap;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.partitioningBy;

@Log4j2
public class Calculator {

    public static Optional<BigDecimal> calculate(Swap[] swaps) {
        BigDecimal conversionRatio = getConversionRatio(swaps);
        log.info("conversionRatio = " + conversionRatio);
        BigDecimal conversionWithFee = getConversionWithFee(conversionRatio, swaps);
        BigDecimal percent = conversionWithFee.subtract(BigDecimal.valueOf(1)).multiply(BigDecimal.valueOf(100));
        return percent.compareTo(BigDecimal.valueOf(0)) > 0 ? Optional.of(percent) : Optional.empty();
    }

    @NotNull
    static BigDecimal getConversionWithFee(BigDecimal conversionRatio, Swap[] swaps) {
        BigDecimal commonFee = BigDecimal.ZERO;
        for (Swap swap : swaps) {
            commonFee = commonFee.add(swap.getFee());
        }
        // TODO need to calculate just one time
        return conversionRatio.subtract(commonFee);
    }

    @NotNull
    static BigDecimal getConversionRatio(Swap[] swaps) {
        BigDecimal result = BigDecimal.ONE;
        Map<Boolean, List<Swap>> swapMap = Arrays.stream(swaps).collect(partitioningBy(Swap::isReversNeed));

        for (Swap swap : swapMap.get(false)) {
            result = result.multiply(swap.getPrice());
        }
        for (Swap swap : swapMap.get(true)) {
            result = result.divide(swap.getPrice(), 99999, RoundingMode.HALF_UP);
        }
        return result.round(new MathContext(10));
    }
}
