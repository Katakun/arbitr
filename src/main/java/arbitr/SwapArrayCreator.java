package arbitr;

import com.kucoin.sdk.KucoinClientBuilder;
import com.kucoin.sdk.KucoinRestClient;
import com.kucoin.sdk.rest.response.MarketTickerResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SwapArrayCreator {
    private final static BigDecimal FEE = new BigDecimal("0.001");

    public Swap[] create(List<String> coinList) {
        Swap[] swaps = new Swap[3];
        swaps[0] = new Swap(coinList.get(0), coinList.get(1), FEE);
        swaps[1] = new Swap(coinList.get(1), coinList.get(2), FEE);
        swaps[2] = new Swap(coinList.get(2), coinList.get(0), FEE);


        Map<String, String> pairMap = toPairMap(swaps);
        KucoinClientBuilder builder = new KucoinClientBuilder().withBaseUrl("https://api.kucoin.com");
        KucoinRestClient kucoinRestClient = builder.buildRestClient();
        try {
            List<MarketTickerResponse> tickers = kucoinRestClient.symbolAPI().getAllTickers().getTicker();
            List<String> coinPairsFilteredlist = tickers.stream()
                    .map(coinPairString -> coinPairString.getSymbol().toUpperCase())
                    .filter(coinPairString -> getOrderType(coinPairString, pairMap).isPresent())
                    .toList();
            if (coinPairsFilteredlist.size() != 3) {
                throw new IllegalStateException("Coin pair != 3");
            }
            for (String coinPairString : coinPairsFilteredlist) {
                for (Swap swap : swaps) {
                    getOrderType(coinPairString, pairMap).ifPresent(swap::setOrderType);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return swaps;
    }

    private Optional<OrderType> getOrderType(String coinPair, Map<String, String> pairMap) {
        for (Map.Entry<String, String> entry : pairMap.entrySet()) {
            if (coinPair.equals(entry.getKey())) {
                return Optional.of(OrderType.BID);
            }
            if (coinPair.equals(entry.getValue())) {
                return Optional.of(OrderType.ASK);
            }
        }
        return Optional.empty();
    }

    private Map<String, String> toPairMap(Swap[] swaps) {
        Map<String, String> pairMap = new HashMap<>();
        for (Swap swap : swaps) {
            String forwardPair = swap.getLeftCoin() + "-" + swap.getRightCoin();
            String backwardPair = swap.getRightCoin() + "-" + swap.getLeftCoin();
            pairMap.put(forwardPair, backwardPair);
        }
        return pairMap;
    }


}
