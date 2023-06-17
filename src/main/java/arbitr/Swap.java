package arbitr;

import lombok.Value;

import java.math.BigDecimal;
@Value
public class Swap {
    private String leftCoin;
    private String rightCoin;
    private BigDecimal ratio;
    private BigDecimal fee;
    private OrderType orderType;
}
