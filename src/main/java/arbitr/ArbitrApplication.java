package arbitr;

import com.kucoin.sdk.KucoinClientBuilder;
import com.kucoin.sdk.KucoinPublicWSClient;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
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

    @SneakyThrows
    public static void main(String[] args) {
        SpringApplication.run(ArbitrApplication.class, args);

        KucoinPublicWSClient kucoinPublicWSClient = new KucoinClientBuilder().withBaseUrl("https://api.kucoin.com")
                .buildPublicWSClient();

        String ack = kucoinPublicWSClient.onTicker(response -> {
            for (String key : mapCoins.keySet()) {
                if (response.getTopic().toUpperCase().contains(key)) {

                    BigDecimal newValue = response.getData().getBestBid();
                    AtomicReference<BigDecimal> oldValue = mapCoins.get(key);

                    boolean isInitialized = mapCoins.values().stream()
                            .allMatch(value -> value.get().compareTo(BigDecimal.valueOf(0)) != 0);

                    if (!isInitialized) {
                        oldValue.set(newValue);
                    } else if (oldValue.get().compareTo(newValue) != 0) {
                        oldValue.set(newValue);
                        BigDecimal dogeBtc = mapCoins.get(DOGE2BTC).get();
                        BigDecimal kcsBtc = mapCoins.get(KCS2BTC).get();
                        BigDecimal dogeeKcs = mapCoins.get(DOGE2KCS).get();

                        BigDecimal profitRatio = dogeBtc.divide(kcsBtc, 10, RoundingMode.HALF_UP).divide(dogeeKcs, 10, RoundingMode.HALF_UP);

                        System.out.println(mapCoins + " profit ratio: " + profitRatio);
                    }
                }
            }


        }, "KCS-BTC", "DOGE-BTC", "DOGE-KCS");

        log.info("ack: " + ack);
        String requsetId = ack;
        while (true) {
            Thread.sleep(1000);
            requsetId = kucoinPublicWSClient.ping(requsetId);
        }
    }

}
