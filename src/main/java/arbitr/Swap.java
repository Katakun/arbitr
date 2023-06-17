package arbitr;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
@Getter
@Setter
@RequiredArgsConstructor
public class Swap {
    private final String leftCoin;
    private final String rightCoin;
    private BigDecimal ratio;
    private final BigDecimal fee;
    private  OrderType orderType;

    public String getCoinPair() {
        return leftCoin + "-" + rightCoin;
    }
}
