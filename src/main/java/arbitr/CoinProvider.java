package arbitr;

import com.kucoin.sdk.websocket.event.KucoinEvent;
import com.kucoin.sdk.websocket.event.TickerChangeEvent;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Log4j2
@RequiredArgsConstructor
@Component
public class CoinProvider {
    private String[] coinsInChain;
    private final Environment environment;
    private Swap[] swaps = new Swap[3];
    private final static int ROUNDING_MODE = 10;
    private final static BigDecimal FEE = new BigDecimal("0.001");

    @PostConstruct
    public void init() {
        String chainString = environment.getProperty("CHAIN");
        log.info("Chain = " + chainString);
        if (null == chainString || chainString.isEmpty()) {
            throw new IllegalArgumentException("Chain is empty");
        }
        coinsInChain = chainString.split("->");
    }

    public Optional<List<Swap>> extractNew(KucoinEvent<TickerChangeEvent> response) {
        String topic = response.getTopic();
        for (int i = 0; i < coinsInChain.length; i++) {
            if (topic.toUpperCase().contains(coinsInChain[i])) {
                BigDecimal newValue = response.getData().getBestBid();
                if (swaps[i] == null) {
                    String[] split = topic.split("-");
                    String leftCoin = split[0];
                    String rightCoin = split[1];
                    BigDecimal ratio = BigDecimal.ONE.divide(newValue, ROUNDING_MODE);

                    swaps[i] = new Swap(leftCoin, rightCoin, ratio, );
                }

            }
        }
    }


    public Optional<List<Swap>> extract(KucoinEvent<TickerChangeEvent> response) {
        log.info(coinsInChain);
        String topic = response.getTopic();
        for (String key : coinsInChain) {
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
