package arbitr;

import com.kucoin.sdk.KucoinClientBuilder;
import com.kucoin.sdk.KucoinPublicWSClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import static arbitr.State.swaps;

@Log4j2
@RequiredArgsConstructor
@Component
public class Runner implements CommandLineRunner {
    private static final CSVFormat CSV_FORMAT = CSVFormat.DEFAULT.builder()
            .setHeader(Headers.class)
            .build();
    private final PriceUpdater priceUpdater;

    private final Parser parser;
    private final Environment environment;


    @Override
    public void run(String... args) throws Exception {
        List<String> coinList = parser.parse(environment.getProperty("CHAIN"));
        Map<String, String> pairMap = SwapUtils.toPairMap(coinList);
        List<String> coinPairsFilteredlist = SwapUtils.getAndFilterPairs(pairMap);
        swaps = SwapUtils.createSwaps(coinPairsFilteredlist, pairMap);
        KucoinPublicWSClient kucoinPublicWSClient = new KucoinClientBuilder().withBaseUrl("https://api.kucoin.com")
                .buildPublicWSClient();
        try (
                Writer writer = new FileWriter("share/arbitr.csv");
                CSVPrinter printer = new CSVPrinter(writer, CSV_FORMAT)
        ) {
            String requestId = kucoinPublicWSClient.onTicker(
                    new OnTickerCallback(
                            printer,
                            priceUpdater
                    ),
                    coinPairsFilteredlist.get(0),
                    coinPairsFilteredlist.get(1),
                    coinPairsFilteredlist.get(2)
            );
            pauseAndPing(kucoinPublicWSClient, requestId);

        } catch (IOException e) {
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
