package arbitr;

import arbitr.calc.Calculator;
import com.kucoin.sdk.KucoinClientBuilder;
import com.kucoin.sdk.KucoinPublicWSClient;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings({"BusyWait", "InfiniteLoopStatement"})
@Log4j2
@SpringBootApplication
public class ArbitrApplication {
    private final static String KCS2BTC = "KCS-BTC";
    private final static String DOGE2BTC = "DOGE-BTC";
    private final static String DOGE2KCS = "DOGE-KCS";
    private final static Map<String, AtomicReference<BigDecimal>> mapCoins = Map.of(
            KCS2BTC, new AtomicReference<>(new BigDecimal(0)),
            DOGE2BTC, new AtomicReference<>(new BigDecimal(0)),
            DOGE2KCS, new AtomicReference<>(new BigDecimal(0)));


    private static final CSVFormat CSV_FORMAT = CSVFormat.DEFAULT.builder()
            .setHeader(Headers.class)
            .build();

    private enum Headers {
        DATE, TIMESTAMP, KCS2BTC, DOGE2BTC, DOGE2KCS, PROFIT_RATIO
    }
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    @SneakyThrows
    public static void main(String[] args) {
        SpringApplication.run(ArbitrApplication.class, args);

        KucoinPublicWSClient kucoinPublicWSClient = new KucoinClientBuilder().withBaseUrl("https://api.kucoin.com")
                .buildPublicWSClient();

        try (
                Writer writer = new FileWriter("share/arbitr.csv");
                CSVPrinter printer = new CSVPrinter(writer, CSV_FORMAT)
        ) {
            String requestId = kucoinPublicWSClient.onTicker(response -> {
                for (String key : mapCoins.keySet()) {
                    if (response.getTopic().toUpperCase().contains(key)) {

                        BigDecimal newValue = response.getData().getBestBid();
                        AtomicReference<BigDecimal> oldValue = mapCoins.get(key);

                        boolean isInitialized = mapCoins.values().stream()
                                .allMatch(value -> value.get().compareTo(BigDecimal.valueOf(0)) != 0);

                        if (!isInitialized) {
                            log.info("initialization");
                            oldValue.set(newValue);
                        } else if (oldValue.get().compareTo(newValue) != 0) {
                            oldValue.set(newValue);
                            BigDecimal dogeBtc = mapCoins.get(DOGE2BTC).get();
                            BigDecimal kcsBtc = mapCoins.get(KCS2BTC).get();
                            BigDecimal dogeKcs = mapCoins.get(DOGE2KCS).get();

                            Optional<BigDecimal> percent = Calculator.calculate(dogeBtc, kcsBtc, dogeKcs);


                            LocalDateTime dateTime = LocalDateTime.now();

                            try {
                                if (percent.isPresent() && (percent.get().compareTo(BigDecimal.valueOf(0)) < 0)) {
                                    log.info(mapCoins + " profit ratio: " + percent);
                                    printer.printRecord(dateTime.format(FORMATTER), Timestamp.valueOf(dateTime).getNanos(),
                                            kcsBtc, dogeBtc, dogeKcs, percent);
                                    printer.flush();
                                }
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }

                        }
                    }
                }

            }, "KCS-BTC", "DOGE-BTC", "DOGE-KCS");
            while (true) {
                Thread.sleep(1000);
                requestId = kucoinPublicWSClient.ping(requestId);
            }

        } catch (IOException e) {
            log.warn(e);
        }


    }




}
