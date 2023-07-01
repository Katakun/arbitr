package arbitr;

import com.kucoin.sdk.websocket.event.KucoinEvent;
import com.kucoin.sdk.websocket.event.TickerChangeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;

@Log4j2
@RequiredArgsConstructor
@Component
public class PriceUpdater {

    public Optional<Swap[]> updatePriceInSwaps(KucoinEvent<TickerChangeEvent> response, Swap[] swaps) {
        if (updatePrice(response, swaps).isPresent()) {
            return Optional.of(swaps);
        }
        return Optional.empty();
    }

    private Optional<Swap[]> updatePrice(KucoinEvent<TickerChangeEvent> response, Swap[] swaps) {
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
                    int notInitializedSwapAmount = getNotInitializedSwapAmount(swaps);
                    log.info("initializing " + notInitializedSwapAmount);
                    if (notInitializedSwapAmount > 0) {
                        return Optional.empty();
                    }
                    return Optional.of(swaps);
                } else if (oldPrice.compareTo(newPrice) != 0) {
                    swap.setPrice(newPrice);
                    if (getNotInitializedSwapAmount(swaps) > 0) {
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

    private int getNotInitializedSwapAmount(Swap[] swaps) {
        int nullCount = 0;
        for (Swap swap : swaps) {
            if (swap.getPrice() == null) {
                nullCount++;
            }
        }
        return nullCount;
    }
}
