package arbitr;

import lombok.Getter;

import java.math.BigDecimal;
@Getter
public class Swap {
    private final String fromCoin;
    private final String toCoin;

    // Это не котировка, это переводной коэффициент
    // для OrderType.ASK ratio = 1/котировку
    // для OrderType.BID ratio = котировка
    private BigDecimal ratio;

    private final BigDecimal fee;
    private  OrderType orderType;

    public Swap(String fromCoin, String toCoin, BigDecimal fee) {
        this.fromCoin = fromCoin;
        this.toCoin = toCoin;
        this.fee = fee;
    }

    public Swap(String fromCoin, String toCoin, BigDecimal fee, BigDecimal ratio) {
        this.fromCoin = fromCoin;
        this.toCoin = toCoin;
        this.ratio = ratio;
        this.fee = fee;
    }

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
