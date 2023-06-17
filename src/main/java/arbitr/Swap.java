package arbitr;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
@Getter
@RequiredArgsConstructor
public class Swap {
    private final String fromCoin;
    private final String toCoin;
    private BigDecimal ratio;
    private final BigDecimal fee;
    private  OrderType orderType;

    public String getCoinPair() {
        switch (orderType) {
            case BID -> {
                return fromCoin + "-" + toCoin;
            }
            case ASK -> {
                return toCoin + "-" + fromCoin;
            }
            default -> throw new RuntimeException("Unknown orderType = " + orderType);
        }
    }

    public void setRatio(BigDecimal ratio) {
        this.ratio = ratio;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public String toString() {
        return "from: " + fromCoin + " to: " + toCoin + " orderType: " + orderType;
    }
}
