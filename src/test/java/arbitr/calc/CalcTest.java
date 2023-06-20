package arbitr.calc;

import arbitr.OrderType;
import arbitr.Swap;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static arbitr.Constants.FEE;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class CalcTest {
    @Test
    public void calculatePositiveTest() {
        Swap[] swaps = {
                new Swap("a", "b", FEE, true, OrderType.BID, "", BigDecimal.valueOf(1)),
                new Swap("a", "b", FEE, false, OrderType.BID, "", BigDecimal.valueOf(1)),
                new Swap("a", "b", FEE, true, OrderType.BID, "", BigDecimal.valueOf(1))
        };

        Optional<BigDecimal> result = Calculator.calculate(swaps);

        assertEquals(Optional.empty(), result);
    }

    @Test
    public void calculateRatioTest() {
        Swap[] swaps = {
                new Swap("a", "b", FEE, false, OrderType.BID, "", BigDecimal.valueOf(0.000002551)),
                new Swap("a", "b", FEE, true, OrderType.BID, "", BigDecimal.valueOf(0.0002638)),
                new Swap("a", "b", FEE, true, OrderType.BID, "", BigDecimal.valueOf(0.009656))
        };

        BigDecimal result = Calculator.getConversionRatio(swaps);
        assertEquals(new BigDecimal("1.001471075"), result);
    }

    @Test
    public void conversionWithFeeTest() {
        Swap[] swaps = {
                new Swap("a", "b", FEE, false, OrderType.BID, "", BigDecimal.valueOf(0.000002551)),
                new Swap("a", "b", FEE, true, OrderType.BID, "", BigDecimal.valueOf(0.0002638)),
                new Swap("a", "b", FEE, true, OrderType.BID, "", BigDecimal.valueOf(0.009656))
        };

        BigDecimal conversionWithFee = Calculator.getConversionWithFee(BigDecimal.valueOf(1.004), swaps);

        assertEquals(new BigDecimal("1.001"), conversionWithFee);
    }

    @Test
    public void calculateNegativeTest() {
        Swap[] swaps = {
                new Swap("a", "b", FEE, false, OrderType.BID, "", BigDecimal.ONE),
                new Swap("a", "b", FEE, false, OrderType.BID, "", BigDecimal.ONE),
                new Swap("a", "b", FEE, false, OrderType.BID, "", BigDecimal.ONE)
        };
        BigDecimal conversionRatio = Calculator.getConversionRatio(swaps);
        BigDecimal conversionWithFee = Calculator.getConversionWithFee(conversionRatio, swaps);
        Optional<BigDecimal> result = Calculator.calculate(swaps);

        assertEquals(new BigDecimal("0.997"), conversionWithFee);
        assertEquals(Optional.empty(), result);
    }


    @Test
    public void getConversionRatioPositiveTest() {
        Swap[] swaps = {
                new Swap("a", "b", FEE, false, OrderType.BID, "", BigDecimal.valueOf(9)),
                new Swap("a", "b", FEE, true, OrderType.BID, "", BigDecimal.valueOf(2)),
                new Swap("a", "b", FEE, true, OrderType.BID, "", BigDecimal.valueOf(3))
        };
        BigDecimal result = Calculator.getConversionRatio(swaps);

        assertEquals(new BigDecimal("1.500000000"), result);
    }
}
