package arbitr;

import com.kucoin.sdk.KucoinClientBuilder;
import com.kucoin.sdk.KucoinRestClient;
import com.kucoin.sdk.rest.response.MarketTickerResponse;
import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Log4j2
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
            for (Swap swap : swaps) {
                for (String coinPairString : coinPairsFilteredlist) {
                    if (coinPairString.contains(swap.getFromCoin()) && coinPairString.contains(swap.getToCoin())) {
                        getOrderType(coinPairString, pairMap).ifPresent(orderType -> {
                            log.info("from Coin: " + swap.getFromCoin() + " toCoin: " + swap.getToCoin() + " orderType: " + orderType);
                            swap.setOrderType(orderType);
                        });
                    }
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
        Map<String, String> pairMap = new LinkedHashMap<>();
        for (Swap swap : swaps) {
            String forwardPair = swap.getFromCoin() + "-" + swap.getToCoin();
            String backwardPair = swap.getToCoin() + "-" + swap.getFromCoin();
            pairMap.put(forwardPair, backwardPair);
        }
        return pairMap;
    }


}
