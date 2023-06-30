package arbitr.calc;

import arbitr.OrderType;
import arbitr.Swap;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
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

        assertEquals(new BigDecimal("1.003000000"), conversionWithFee);
        assertEquals(Optional.empty(), result);
    }


    @Test
    public void getConversionRatioReversePositiveTest() {
        int price1 = 2;
        int price2 = 16;
        int price3 = 4;

        Swap[] swapsForward = {
                new Swap("KCS", "DOGE", FEE, false, OrderType.BID, "", BigDecimal.valueOf(price1)),
                new Swap("DOGE", "BTC", FEE, true, OrderType.BID, "", BigDecimal.valueOf(price2)),
                new Swap("BTC", "KCS", FEE, false, OrderType.BID, "", BigDecimal.valueOf(price3))
        };

        Swap[] swapsBackward = {
                new Swap("BTC", "DOGE", FEE, false, OrderType.BID, "", BigDecimal.valueOf(price2)),
                new Swap("DOGE", "KCS", FEE, true, OrderType.BID, "", BigDecimal.valueOf(price1)),
                new Swap("KCS", "BTC", FEE, true, OrderType.BID, "", BigDecimal.valueOf(price3)),
        };

        BigDecimal resultForward = Calculator.getConversionRatio(swapsForward);
        BigDecimal resultBackward = Calculator.getConversionRatio(swapsBackward);

        assertEquals(new BigDecimal("2.000000000"), resultForward);
        assertEquals(new BigDecimal("0.5000000000"), resultBackward);

        assertEquals(resultBackward, BigDecimal.ONE.divide(resultForward, 10, RoundingMode.HALF_UP));
    }

    @Test
    public void getConversionRatioReversePositiveRealPriceTest() {
        Double price2 = 0.000002193;
        Double price1 = 0.009923;
        Double price3 = 0.0002205;

        Swap[] swapsForward = {
                new Swap("KCS", "DOGE", FEE, false, OrderType.BID, "", BigDecimal.valueOf(price1)),
                new Swap("DOGE", "BTC", FEE, true, OrderType.BID, "", BigDecimal.valueOf(price2)),
                new Swap("BTC", "KCS", FEE, false, OrderType.BID, "", BigDecimal.valueOf(price3))
        };

        Swap[] swapsBackward = {
                new Swap("BTC", "DOGE", FEE, false, OrderType.BID, "", BigDecimal.valueOf(price2)),
                new Swap("DOGE", "KCS", FEE, true, OrderType.BID, "", BigDecimal.valueOf(price1)),
                new Swap("KCS", "BTC", FEE, true, OrderType.BID, "", BigDecimal.valueOf(price3)),
        };

        BigDecimal resultForward = Calculator.getConversionRatio(swapsForward);
        BigDecimal resultBackward = Calculator.getConversionRatio(swapsBackward);
        System.out.println("resultForward " + resultForward);
        System.out.println("resultBackwars " + resultBackward);

        assertEquals(resultBackward.round(new MathContext(3)), BigDecimal.ONE.divide(resultForward, 3, RoundingMode.HALF_UP).round( new MathContext(3)));
    }
}
