package arbitr;

import com.kucoin.sdk.websocket.event.KucoinEvent;
import com.kucoin.sdk.websocket.event.TickerChangeEvent;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Log4j2
@RequiredArgsConstructor
@Component
public class CoinProvider {
    public final static String KCS2BTC = "KCS-BTC";
    public final static String DOGE2BTC = "DOGE-BTC";
    public final static String DOGE2KCS = "DOGE-KCS";
    private String[] coinsInChain;
    private final Environment environment;

    private final static Map<String, AtomicReference<BigDecimal>> coins = Map.of(
            KCS2BTC, new AtomicReference<>(new BigDecimal(0)),
            DOGE2BTC, new AtomicReference<>(new BigDecimal(0)),
            DOGE2KCS, new AtomicReference<>(new BigDecimal(0)));

    @PostConstruct
    public void init() {
        String chainString = environment.getProperty("CHAIN");
        log.info("Chain = " + chainString);
        if (null == chainString || chainString.isEmpty()) {
            throw new IllegalArgumentException("Chain is empty");
        }
        coinsInChain = chainString.split("->");
    }



    public Optional<Map<String, AtomicReference<BigDecimal>>> extract(KucoinEvent<TickerChangeEvent> response) {
        log.info(coinsInChain);
        String topic = response.getTopic();
        for (String key : coins.keySet()) {
            if (topic.toUpperCase().contains(key)) {
                // TODO change getBestBid()
                BigDecimal newValue = response.getData().getBestBid();
                AtomicReference<BigDecimal> oldValue = coins.get(key);

                boolean isInitialized = coins.values().stream()
                        .allMatch(value -> value.get().compareTo(BigDecimal.valueOf(0)) != 0);
                if (!isInitialized) {
                    log.info("initialization");
                    oldValue.set(newValue);
                    return Optional.empty();
                }
                if (oldValue.get().compareTo(newValue) != 0) {
                    oldValue.set(newValue);
                    return Optional.of(coins);
                } else {
                    return Optional.empty();
                }
            }
        }
        throw new RuntimeException("Can't handle " + topic);
    }
}
