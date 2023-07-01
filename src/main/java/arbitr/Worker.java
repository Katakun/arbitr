package arbitr;

import com.kucoin.sdk.KucoinClientBuilder;
import com.kucoin.sdk.KucoinPublicWSClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
public class Worker implements Runnable {
    private static final CSVFormat CSV_FORMAT = CSVFormat.DEFAULT.builder()
            .setHeader(Headers.class)
            .build();
    private final Parser parser;
    private final PriceUpdater priceUpdater;
    private final String chain;

    public void run() {
        List<String> coinList = parser.parse(chain);
        Map<String, String> pairMap = SwapUtils.toPairMap(coinList);
        List<String> coinPairsFilteredlist = SwapUtils.getAndFilterPairs(pairMap);
        Swap[] swaps = SwapUtils.createSwaps(coinPairsFilteredlist, pairMap);
        try (
                Writer writer = new FileWriter("share/" + chain.replace("->", "-") + "_arbitr.csv");
                CSVPrinter printer = new CSVPrinter(writer, CSV_FORMAT)
        ) {
            KucoinPublicWSClient kucoinPublicWSClient = new KucoinClientBuilder().withBaseUrl("https://api.kucoin.com")
                    .buildPublicWSClient();
            String requestId = kucoinPublicWSClient.onTicker(
                    new OnTickerCallback(
                            printer,
                            priceUpdater,
                            swaps
                    ),
                    coinPairsFilteredlist.get(0),
                    coinPairsFilteredlist.get(1),
                    coinPairsFilteredlist.get(2)
            );
            pauseAndPing(kucoinPublicWSClient, requestId);

        } catch (IOException | InterruptedException e) {
            log.warn(e);
        }
    }

    @SuppressWarnings({"InfiniteLoopStatement", "BusyWait"})
    private void pauseAndPing(KucoinPublicWSClient kucoinPublicWSClient, String requestId) throws InterruptedException {
        while (true) {
            Thread.sleep(1000);
            requestId = kucoinPublicWSClient.ping(requestId);
        }
    }
}
