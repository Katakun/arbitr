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
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
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
    private final PriceUpdater priceUpdater;
    private final Swap[] swaps;

    @Override
    public synchronized void onResponse(KucoinEvent<TickerChangeEvent> response) throws KucoinApiException {
        synchronized (OnTickerCallback.class) {
            Optional<Swap[]> optionalSwaps = priceUpdater.updatePriceInSwaps(response, swaps);
            optionalSwaps.ifPresent(swaps -> {
                ZonedDateTime dateTime = ZonedDateTime.now().withZoneSameInstant(ZoneOffset.UTC);
                Instant instant = dateTime.toInstant();
                long milliseconds = instant.toEpochMilli();
                Calculator.calculate(swaps).ifPresent(percent -> {
                    List<BigDecimal> ratios = Arrays.stream(swaps).map(Swap::getPrice).collect(Collectors.toList());
                    save(printer, ratios, percent, dateTime, milliseconds);
                });
            });
        }
    }

    private void save(
            CSVPrinter printer, List<BigDecimal> ratios,
            BigDecimal percent, ZonedDateTime dateTime, long milliseconds
    ) {
        try {
            printer.printRecord(dateTime.format(FORMATTER), milliseconds,
                    ratios.get(0), ratios.get(1), ratios.get(2), percent);
            printer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
