package arbitr.calc;


import arbitr.OrderType;
import arbitr.Swap;
import arbitr.SwapUtils;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SwapUtilsTest {

    @Test
    public void createSwapTest() {
        List<String> coinPairsFilteredlist = List.of("DOGE-KCS", "DOGE-BTC", "KCS-BTC");
        Map<String, String> pairMap = new LinkedHashMap<>();
        pairMap.put("KCS-DOGE", "DOGE-KCS");
        pairMap.put("DOGE-BTC", "BTC-DOGE");
        pairMap.put("BTC-KCS", "KCS-BTC");

        Swap[] swaps = SwapUtils.createSwaps(coinPairsFilteredlist, pairMap);

        assertEquals("KCS", swaps[0].getFromCoin());
        assertEquals("DOGE", swaps[0].getToCoin());
        assertEquals(new BigDecimal("0.001"), swaps[0].getFee());
        assertFalse(swaps[0].isReversNeed());
        assertEquals(OrderType.ASK, swaps[0].getOrderType());
        assertEquals("DOGE-KCS", swaps[0].getTicker());
        assertNull(swaps[0].getPrice());

        assertEquals("DOGE", swaps[1].getFromCoin());
        assertEquals("BTC", swaps[1].getToCoin());
        assertEquals(new BigDecimal("0.001"), swaps[1].getFee());
        assertTrue(swaps[1].isReversNeed());
        assertEquals(OrderType.BID, swaps[1].getOrderType());
        assertEquals("DOGE-BTC", swaps[1].getTicker());
        assertNull(swaps[1].getPrice());

        assertEquals("BTC", swaps[2].getFromCoin());
        assertEquals("KCS", swaps[2].getToCoin());
        assertEquals(new BigDecimal("0.001"), swaps[2].getFee());
        assertFalse(swaps[2].isReversNeed());
        assertEquals(OrderType.ASK, swaps[2].getOrderType());
        assertEquals("KCS-BTC", swaps[2].getTicker());
        assertNull(swaps[2].getPrice());
    }

    @Test
    public void removeDuplicateTest() {
        List<String> testList = List.of("USDC-USDT", "USDT-USDC", "LTC-USDC", "LTC-USDT");
        List<String> result = List.of("USDC-USDT", "LTC-USDC", "LTC-USDT");
        assertEquals(result, SwapUtils.removeDuplicate(testList));
    }
}
