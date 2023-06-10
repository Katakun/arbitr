package arbitr.calc;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class CalcTest {

    @Test
    public void calculatePositiveTest() {
        BigDecimal dogeBtc = BigDecimal.valueOf(2);
        BigDecimal kcsBtc = BigDecimal.valueOf(9);
        BigDecimal dogeKcs = BigDecimal.valueOf(3);

        Optional<BigDecimal> result = Calculator.calculate(dogeBtc, kcsBtc, dogeKcs);

        assertEquals(Optional.empty(), result);
    }

    @Test
    public void calculateRatioTest() {
        BigDecimal dogeBtc = BigDecimal.valueOf(0.000002551);
        BigDecimal kcsBtc = BigDecimal.valueOf(0.0002638);
        BigDecimal dogeKcs = BigDecimal.valueOf(0.009656);
        BigDecimal result = Calculator.getConversionRatio(dogeBtc, kcsBtc, dogeKcs);

        assertEquals(new BigDecimal("1.0014710750"), result);
    }

    @Test
    public void conversionWithFeeTest() {
        BigDecimal conversionWithFee = Calculator.getConversionWithFee(BigDecimal.valueOf(1.004));

        assertEquals(new BigDecimal("1.001"), conversionWithFee);
    }

    @Test
    public void calculateNegativeTest() {
        BigDecimal dogeBtc = BigDecimal.valueOf(1.0);
        BigDecimal kcsBtc = BigDecimal.valueOf(1.0);
        BigDecimal dogeKcs = BigDecimal.valueOf(1.0);
        BigDecimal conversionRatio = Calculator.getConversionRatio(dogeBtc, kcsBtc, dogeKcs);
        BigDecimal conversionWithFee = Calculator.getConversionWithFee(conversionRatio);
        Optional<BigDecimal> result = Calculator.calculate(dogeBtc, kcsBtc, dogeKcs);

        assertEquals(new BigDecimal("0.9970000000"), conversionWithFee);
        assertEquals(Optional.empty(), result);
    }

    @Test
    public void calculateTest() {
        BigDecimal dogeBtc = BigDecimal.valueOf(9.0);
        BigDecimal kcsBtc = BigDecimal.valueOf(2.0);
        BigDecimal dogeKcs = BigDecimal.valueOf(3.0);
        Optional<BigDecimal> result = Calculator.calculate(dogeBtc, kcsBtc, dogeKcs);

        assertEquals(new BigDecimal("49.7000000000"), result.orElseThrow());
    }

    @Test
    public void getConversionRatioPositiveTest() {
        BigDecimal dogeBtc = BigDecimal.valueOf(9.0);
        BigDecimal kcsBtc = BigDecimal.valueOf(2.0);
        BigDecimal dogeKcs = BigDecimal.valueOf(3.0);
        BigDecimal result = Calculator.getConversionRatio(dogeBtc, kcsBtc, dogeKcs);

        assertEquals(new BigDecimal("1.5000000000"), result);
    }

}
