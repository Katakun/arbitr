package arbitr;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class Swap {
    private final String fromCoin;
    private final String toCoin;
    private final BigDecimal fee;
    private final boolean isReversNeed; // if price exist in tickers -> false
    private final OrderType orderType;
    private final String ticker;
    private BigDecimal price;

    public Swap(String fromCoin, String toCoin,
                BigDecimal fee, Boolean isReversNeed,
                OrderType orderType, String ticker) {
        this.fromCoin = fromCoin;
        this.toCoin = toCoin;
        this.fee = fee;
        this.isReversNeed = isReversNeed;
        this.orderType = orderType;
        this.ticker = ticker;
    }

    public Swap(
            String fromCoin, String toCoin, BigDecimal fee,
            boolean isReversNeed, OrderType orderType, String ticker, BigDecimal price
    ) {
        this.fromCoin = fromCoin;
        this.toCoin = toCoin;
        this.fee = fee;
        this.isReversNeed = isReversNeed;
        this.orderType = orderType;
        this.price = price;
        this.ticker = ticker;
    }

    public String toString() {
        return fromCoin + ">" + toCoin;
    }
}
