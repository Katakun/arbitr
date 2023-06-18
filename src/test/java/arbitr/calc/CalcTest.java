package arbitr.calc;

import arbitr.Swap;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class CalcTest {
    DecimalFormat decimalFormat = new DecimalFormat("#.0000000000");

    @Test
    public void calculatePositiveTest() {
        Swap[] swaps = {
                new Swap("a", "b", new BigDecimal("0.001"), BigDecimal.valueOf(2)),
                new Swap("b", "c", new BigDecimal("0.001"), new BigDecimal("1").divide(new BigDecimal(9), 15, RoundingMode.CEILING)),
                new Swap("c", "a", new BigDecimal("0.001"), new BigDecimal("1").divide(new BigDecimal(3), 15, RoundingMode.CEILING))};

        Optional<BigDecimal> result = Calculator.calculate(swaps);

        assertEquals(Optional.empty(), result);
    }

    @Test
    public void calculateRatioTest() {
        Swap[] swaps = {
                new Swap("a", "b", new BigDecimal("0.001"), BigDecimal.valueOf(0.000002551)),
                new Swap("b", "c", new BigDecimal("0.001"), BigDecimal.valueOf(1).divide(BigDecimal.valueOf(0.0002638), 10, RoundingMode.HALF_UP)),
                new Swap("c", "a", new BigDecimal("0.001"), BigDecimal.valueOf(1).divide(BigDecimal.valueOf(0.009656), 10, RoundingMode.HALF_UP))};

        BigDecimal result = Calculator.getConversionRatio(swaps);
        assertEquals("1.0014710750", decimalFormat.format(result));
    }

    @Test
    public void conversionWithFeeTest() {
        BigDecimal conversionWithFee = Calculator.getConversionWithFee(BigDecimal.valueOf(1.004));

        assertEquals(new BigDecimal("1.001"), conversionWithFee);
    }

    @Test
    public void calculateNegativeTest() {
        Swap[] swaps = {
                new Swap("a", "b", BigDecimal.valueOf(0.001), BigDecimal.ONE),
                new Swap("b", "c", BigDecimal.valueOf(0.001), BigDecimal.ONE),
                new Swap("c", "a", BigDecimal.valueOf(0.001), BigDecimal.ONE)};

        BigDecimal conversionRatio = Calculator.getConversionRatio(swaps);
        BigDecimal conversionWithFee = Calculator.getConversionWithFee(conversionRatio);
        Optional<BigDecimal> result = Calculator.calculate(swaps);

        assertEquals(new BigDecimal("0.997"), conversionWithFee);
        assertEquals(Optional.empty(), result);
    }

    @Test
    public void calculateTest() {
        Swap[] swaps = {
                new Swap("a", "b", new BigDecimal("0.001"), new BigDecimal(9)),
                new Swap("b", "c", new BigDecimal("0.001"), new BigDecimal("1").divide(new BigDecimal(2), 15, RoundingMode.CEILING)),
                new Swap("c", "a", new BigDecimal("0.001"), new BigDecimal("1").divide(new BigDecimal(3), 15, RoundingMode.CEILING))};

        Optional<BigDecimal> result = Calculator.calculate(swaps);

        assertEquals("49.7000000000", decimalFormat.format(result.orElseThrow()));
    }

    @Test
    public void getConversionRatioPositiveTest() {
        Swap[] swaps = {
                new Swap("a", "b", new BigDecimal("0.001"), new BigDecimal(9)),
                new Swap("b", "c", new BigDecimal("0.001"), new BigDecimal("1").divide(new BigDecimal(2), 10, RoundingMode.HALF_UP)),
                new Swap("c", "a", new BigDecimal("0.001"), new BigDecimal("1").divide(new BigDecimal(3), 10,RoundingMode.HALF_UP))};

        BigDecimal result = Calculator.getConversionRatio(swaps);

        assertEquals(new BigDecimal("1.5000000000"), result);
    }

}
