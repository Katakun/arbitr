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

import static arbitr.Constants.CHAIN_LENGTH;
import static java.util.stream.Collectors.toList;

@Log4j2
@RequiredArgsConstructor
@Component
public class SwapProvider {
    private final Environment environment;
    private Swap[] swaps;
    private int notInitializedSwapCount = CHAIN_LENGTH;

    @PostConstruct
    public void init() {
        // CHAIN=XCN->BTC->USDC
        String chainString = environment.getProperty("CHAIN");
        log.info("Chain = " + chainString);
        if (null == chainString || chainString.isEmpty()) {
            throw new IllegalArgumentException("Chain is empty");
        }
        // "XCN", "BTC", "USDC"
        List<String> coinsInChain = Arrays.stream(chainString.split("->"))
                .map(String::toUpperCase).collect(toList());
        swaps = new SwapArrayCreator().create(coinsInChain);
    }

    public Optional<Swap[]> extract(KucoinEvent<TickerChangeEvent> response) {
        if (updatePrice(response).isPresent()) {
            return Optional.of(swaps);
        }
        return Optional.empty();
    }

    private Optional<Swap[]> updatePrice(KucoinEvent<TickerChangeEvent> response) {
        // topic = /market/ticker:KCS-BTC
        String topic = response.getTopic().split(":")[1];
        for (Swap swap : swaps) {
            if (swap.getTicker().equals(topic)) {
                BigDecimal oldPrice = swap.getPrice();
                BigDecimal newPrice = swap.getOrderType() == OrderType.ASK
                        ? response.getData().getBestAsk()
                        : response.getData().getBestBid();
                if (oldPrice == null) {
                    swap.setPrice(newPrice);
                    log.info("initializing " + notInitializedSwapCount);
                    notInitializedSwapCount--;
                    if (notInitializedSwapCount > 0) {
                        return Optional.empty();
                    }
                    return Optional.of(swaps);
                } else if (oldPrice.compareTo(newPrice) != 0) {
                    swap.setPrice(newPrice);
                    if (notInitializedSwapCount > 0) {
                        return Optional.empty();
                    } else {
                        return Optional.of(swaps);
                    }
                } else {
                    return Optional.empty();
                }
            }
        }
        throw new IllegalStateException("Not found ticker in swaps: " + topic);
    }
}
