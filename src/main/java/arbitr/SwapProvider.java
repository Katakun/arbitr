package arbitr;

import com.kucoin.sdk.websocket.event.KucoinEvent;
import com.kucoin.sdk.websocket.event.TickerChangeEvent;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Log4j2
@RequiredArgsConstructor
@Component
public class SwapProvider {
    private final Environment environment;
    private Swap[] swaps;
    private final static int ROUNDING_MODE = 10;
    private int notInitializedSwapCount = 3;

    @PostConstruct
    public void init() {
        String chainString = environment.getProperty("CHAIN");
        log.info("Chain = " + chainString);
        if (null == chainString || chainString.isEmpty()) {
            throw new IllegalArgumentException("Chain is empty");
        }
        List<String> coinsInChain = Arrays.stream(chainString.split("->")).map(coinString -> coinString.toUpperCase()).collect(toList());
        swaps = new SwapArrayCreator().create(coinsInChain);
    }

    public Optional<Swap[]> extract(KucoinEvent<TickerChangeEvent> response) {
        String topic = response.getTopic();
        boolean updated = updateRatio(response, topic.split(":")[1]);
        if (notInitializedSwapCount == 0 && updated) {
            return Optional.of(swaps);
        } else {
            return Optional.empty();
        }
    }

    private boolean updateRatio(KucoinEvent<TickerChangeEvent> response, String topic) {
        for (Swap swap : swaps) {
            if (topic.toUpperCase().equals(swap.getCoinPair())) {
                TickerChangeEvent data = response.getData();
                BigDecimal newValue = swap.getOrderType() == OrderType.BID ? data.getBestBid() : data.getBestAsk();
                BigDecimal oldValue = swap.getRatio();
                if (oldValue == null) {
                    swap.setRatio(newValue);
                    notInitializedSwapCount--;
                    log.info("initialization");
                    return false;
                } else if (!oldValue.equals(newValue)) {
                    swap.setRatio(newValue);
                    return true;
                } else {
                    return false;
                }
            }
        }
        throw new IllegalStateException();
    }

}
