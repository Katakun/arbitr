package arbitr;

import com.kucoin.sdk.KucoinClientBuilder;
import com.kucoin.sdk.KucoinRestClient;
import com.kucoin.sdk.rest.response.MarketTickerResponse;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static arbitr.Constants.CHAIN_LENGTH;
import static arbitr.Constants.FEE;

public class SwapUtils {


    // "XCN", "BTC", "USDC" -> <"XCN-BTC",  "BTC-XCN">
    //                         <"BTC-USDC", "USDC-BTC">
    //                         <"USDC-XCN", "XCN-USDC">
    public static Map<String, String> toPairMap(List<String> coinList) {
        Map<String, String> pairMap = new LinkedHashMap<>();
        for (int i = 0; i < coinList.size() - 1; i++) {
            String forwardPair = coinList.get(i) + "-" + coinList.get(i + 1);
            String backwardPair = coinList.get(i + 1) + "-" + coinList.get(i);
            pairMap.put(forwardPair, backwardPair);
        }
        String forwardPair = coinList.get(coinList.size() - 1) + "-" + coinList.get(0);
        String backwardPair = coinList.get(0) + "-" + coinList.get(coinList.size() - 1);
        pairMap.put(forwardPair, backwardPair);
        return pairMap;
    }

    public static List<String> getAndFilterPairs(Map<String, String> pairMap) {
        KucoinClientBuilder builder = new KucoinClientBuilder().withBaseUrl("https://api.kucoin.com");
        KucoinRestClient kucoinRestClient = builder.buildRestClient();
        try {
            List<MarketTickerResponse> tickers = kucoinRestClient.symbolAPI().getAllTickers().getTicker();
            List<String> coinPairsFilteredlist = tickers.stream()
                    .map(coinPairString -> coinPairString.getSymbol().toUpperCase())
                    .filter(coinPairString -> pairMap.containsKey(coinPairString) || pairMap.containsValue(coinPairString))
                    .toList();
            if (coinPairsFilteredlist.size() != CHAIN_LENGTH) {
                throw new IllegalStateException("The quantity of coin pair != " + CHAIN_LENGTH);
            }
            return coinPairsFilteredlist;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Swap[] createSwaps(List<String> coinPairsFilteredlist, Map<String, String> pairMap) {
        Swap[] swaps = new Swap[CHAIN_LENGTH];
        int swapNumber = 0;
        for (Map.Entry<String, String> entry : pairMap.entrySet()) {
            String[] coins = entry.getKey().split("-");
            if (coinPairsFilteredlist.contains(entry.getKey())) {
                swaps[swapNumber++] = new Swap(coins[0], coins[1], FEE, true, OrderType.BID, entry.getKey());
            } else if (coinPairsFilteredlist.contains(entry.getValue())) {
                swaps[swapNumber++] = new Swap(coins[0], coins[1], FEE, false, OrderType.ASK, entry.getValue());
            } else {
                throw new IllegalStateException(entry + " pair not in keys and not in values");
            }
        }
        return swaps;
    }
}
