package arbitr;

import arbitr.calc.Calculator;
import com.kucoin.sdk.exception.KucoinApiException;
import com.kucoin.sdk.websocket.KucoinAPICallback;
import com.kucoin.sdk.websocket.event.KucoinEvent;
import com.kucoin.sdk.websocket.event.TickerChangeEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Log4j2
@RequiredArgsConstructor
public class OnTickerHandler implements KucoinAPICallback<KucoinEvent<TickerChangeEvent>> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final CSVPrinter printer;
    private final SwapProvider swapProvider;

    @Override
    public void onResponse(KucoinEvent<TickerChangeEvent> response) throws KucoinApiException {
        Optional<Swap[]> coins = swapProvider.extract(response);

        coins.ifPresent(swaps -> {
            BigDecimal ratio0 = swaps[0].getRatio();
            BigDecimal ratio1 = swaps[1].getRatio();
            BigDecimal ratio2 = swaps[2].getRatio();
            Optional<BigDecimal> percentOptional = Calculator.calculate(ratio0, ratio1, ratio2);
            LocalDateTime dateTime = LocalDateTime.now();

            percentOptional.ifPresent(percent -> save(printer, ratio0, ratio1, ratio2, percent, dateTime));
        });
    }

    private void save(
            CSVPrinter printer, BigDecimal dogeBtc, BigDecimal kcsBtc, BigDecimal dogeKcs,
            BigDecimal percent, LocalDateTime dateTime
    ) {
        try {
            printer.printRecord(dateTime.format(FORMATTER), Timestamp.valueOf(dateTime).getNanos(), kcsBtc, dogeBtc, dogeKcs, percent);
            printer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
