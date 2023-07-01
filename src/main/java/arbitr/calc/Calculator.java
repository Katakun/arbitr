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
import java.util.concurrent.locks.ReentrantLock;

import static java.util.stream.Collectors.partitioningBy;

@Log4j2
public class Calculator {

    private static final ReentrantLock lock = new ReentrantLock();

    public static Optional<BigDecimal> calculate(Swap[] swaps) {
        lock.lock();
        try {
            BigDecimal conversionRatio = getConversionRatio(swaps);
            log.info("convRatio: " + conversionRatio + " chain: " + Arrays.toString(swaps) + " thread: " + Thread.currentThread().getId());
            BigDecimal conversionWithFee = getConversionWithFee(conversionRatio, swaps);
            BigDecimal percent = conversionWithFee.subtract(BigDecimal.valueOf(1)).multiply(BigDecimal.valueOf(100));
            return percent.abs().compareTo(BigDecimal.valueOf(0.5)) > 0 ? Optional.of(percent) : Optional.empty();
        } finally {
            lock.unlock();
        }
    }

    @NotNull
    static BigDecimal getConversionWithFee(BigDecimal conversionRatio, Swap[] swaps) {
        lock.lock();
        try {
            BigDecimal commonFee = BigDecimal.ZERO;
            for (Swap swap : swaps) {
                commonFee = commonFee.add(swap.getFee());
            }
            return conversionRatio.compareTo(BigDecimal.ONE) > 0 ? conversionRatio.subtract(commonFee) : conversionRatio.add(commonFee);
        } finally {
            lock.unlock();
        }
    }

    @NotNull
    static BigDecimal getConversionRatio(Swap[] swaps) {
        lock.lock();
        try {
            BigDecimal result = BigDecimal.ONE;
            Map<Boolean, List<Swap>> swapMap = Arrays.stream(swaps).collect(partitioningBy(Swap::isReversNeed));

            for (Swap swap : swapMap.get(true)) {
                result = result.multiply(swap.getPrice());
            }
            for (Swap swap : swapMap.get(false)) {
                result = result.divide(swap.getPrice(), 99999, RoundingMode.HALF_UP);
            }
            return result.round(new MathContext(10));
        } finally {
            lock.unlock();
        }

    }
}
