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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@RequiredArgsConstructor
public class OnTickerCallback implements KucoinAPICallback<KucoinEvent<TickerChangeEvent>> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final CSVPrinter printer;
    private final SwapProvider swapProvider;

    @Override
    public void onResponse(KucoinEvent<TickerChangeEvent> response) throws KucoinApiException {
        Optional<Swap[]> optionalSwaps = swapProvider.extract(response);

        optionalSwaps.ifPresent(swaps -> {
            Optional<BigDecimal> percentOptional = Calculator.calculate(swaps);
            LocalDateTime dateTime = LocalDateTime.now();
            percentOptional.ifPresent(percent -> {
                List<BigDecimal> ratios = Arrays.stream(swaps).map(swap -> swap.getRatio()).collect(Collectors.toList());
                save(printer, ratios, percent, dateTime);
            });
        });
    }

    private void save(
            CSVPrinter printer, List<BigDecimal> ratios,
            BigDecimal percent, LocalDateTime dateTime
    ) {
        try {
            printer.printRecord(dateTime.format(FORMATTER), Timestamp.valueOf(dateTime).getNanos(),
                    ratios.get(0), ratios.get(1), ratios.get(2), percent);
            printer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
