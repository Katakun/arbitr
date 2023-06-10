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

@Log4j2
@RequiredArgsConstructor
@Component
public class Runner implements CommandLineRunner {
    private static final CSVFormat CSV_FORMAT = CSVFormat.DEFAULT.builder()
            .setHeader(Headers.class)
            .build();

    private final Environment environment;

    @Override
    public void run(String... args) throws Exception {
        log.info("Chain = " + environment.getProperty("CHAIN"));
        KucoinPublicWSClient kucoinPublicWSClient = new KucoinClientBuilder().withBaseUrl("https://api.kucoin.com")
                .buildPublicWSClient();

        try (
                Writer writer = new FileWriter("share/arbitr.csv");
                CSVPrinter printer = new CSVPrinter(writer, CSV_FORMAT)
        ) {
            String requestId = kucoinPublicWSClient.onTicker(new OnTickerHandler(printer), "KCS-BTC", "DOGE-BTC", "DOGE-KCS");
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
