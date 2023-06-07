package arbitr.calc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;


public class CalcTest {

    @Test
    public void calculatePositiveTest() {
        BigDecimal dogeBtc = BigDecimal.valueOf(2);
        BigDecimal kcsBtc = BigDecimal.valueOf(9);
        BigDecimal dogeeKcs = BigDecimal.valueOf(3);

        BigDecimal convertionRatio = Calculator.getConvertionRatio(dogeBtc, kcsBtc, dogeeKcs);
        BigDecimal convertionWithFee = Calculator.getConvertionWithFee(convertionRatio);
        Optional<BigDecimal> result = Calculator.calculate(dogeBtc, kcsBtc, dogeeKcs);

        assertEquals(Optional.empty(), result);
    }

    @Test
    public void calculateRatioTest() {
        BigDecimal dogeBtc = BigDecimal.valueOf(0.000002551);
        BigDecimal kcsBtc = BigDecimal.valueOf(0.0002638);
        BigDecimal dogeeKcs = BigDecimal.valueOf(0.009656);
        BigDecimal result = Calculator.getConvertionRatio(dogeBtc, kcsBtc, dogeeKcs);

        assertEquals(new BigDecimal("1.0014710750"), result);
    }

    @Test
    public void conversionWithFeeTest() {
        BigDecimal convertionWithFee = Calculator.getConvertionWithFee(BigDecimal.valueOf(1.004));

        assertEquals(new BigDecimal("1.001"), convertionWithFee);
    }

    @Test
    public void calculateNegativeTest() {
        BigDecimal dogeBtc = BigDecimal.valueOf(1.0);
        BigDecimal kcsBtc = BigDecimal.valueOf(1.0);
        BigDecimal dogeeKcs = BigDecimal.valueOf(1.0);
        BigDecimal convertionRatio = Calculator.getConvertionRatio(dogeBtc, kcsBtc, dogeeKcs);
        BigDecimal convertionWithFee = Calculator.getConvertionWithFee(convertionRatio);
        Optional<BigDecimal> result = Calculator.calculate(dogeBtc, kcsBtc, dogeeKcs);

        assertEquals(new BigDecimal("0.9970000000"), convertionWithFee);
        assertEquals(Optional.empty(), result);
    }

    @Test
    public void calculateTest() {
        BigDecimal dogeBtc = BigDecimal.valueOf(9.0);
        BigDecimal kcsBtc = BigDecimal.valueOf(2.0);
        BigDecimal dogeeKcs = BigDecimal.valueOf(3.0);
        Optional<BigDecimal> result = Calculator.calculate(dogeBtc, kcsBtc, dogeeKcs);

        assertEquals(new BigDecimal("49.7000000000"), result.get());
    }

    @Test
    public void getConvertionRatioPositiveTest() {
        BigDecimal dogeBtc = BigDecimal.valueOf(9.0);
        BigDecimal kcsBtc = BigDecimal.valueOf(2.0);
        BigDecimal dogeeKcs = BigDecimal.valueOf(3.0);
        BigDecimal result = Calculator.getConvertionRatio(dogeBtc, kcsBtc, dogeeKcs);

        assertEquals(new BigDecimal("1.5000000000"), result);
    }

    @Test
    public void whenPerformingArithmetic_thenExpectedResult() {
        BigDecimal bd1 = new BigDecimal("4.0");
        BigDecimal bd2 = new BigDecimal("2.0");

        BigDecimal sum = bd1.add(bd2);
        BigDecimal difference = bd1.subtract(bd2);
        BigDecimal quotient = bd1.divide(bd2);
        BigDecimal product = bd1.multiply(bd2);

        assertTrue(sum.compareTo(new BigDecimal("6.0")) == 0);
        assertTrue(difference.compareTo(new BigDecimal("2.0")) == 0);
        assertTrue(quotient.compareTo(new BigDecimal("2.0")) == 0);
        assertTrue(product.compareTo(new BigDecimal("8.0")) == 0);
    }
}
