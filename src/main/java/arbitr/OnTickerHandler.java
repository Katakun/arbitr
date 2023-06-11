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
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static arbitr.CoinProvider.*;

@Log4j2
@RequiredArgsConstructor
public class OnTickerHandler implements KucoinAPICallback<KucoinEvent<TickerChangeEvent>> {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final CSVPrinter printer;
    private final CoinProvider coinProvider;
    @Override
    public void onResponse(KucoinEvent<TickerChangeEvent> response) throws KucoinApiException {
        Optional<Map<String, AtomicReference<BigDecimal>>> coins = coinProvider.extract(response);
        if (coins.isPresent()) {
            BigDecimal dogeBtc = coins.get().get(DOGE2BTC).get();
            BigDecimal kcsBtc = coins.get().get(KCS2BTC).get();
            BigDecimal dogeKcs = coins.get().get(DOGE2KCS).get();

            Optional<BigDecimal> percent = Calculator.calculate(dogeBtc, kcsBtc, dogeKcs);
            LocalDateTime dateTime = LocalDateTime.now();

            percent.ifPresent(bigDecimal -> save(printer, dogeBtc, kcsBtc, dogeKcs, bigDecimal, dateTime, coins.get()));
        }
    }

    private void save(CSVPrinter printer, BigDecimal dogeBtc, BigDecimal kcsBtc, BigDecimal dogeKcs, BigDecimal
            percent, LocalDateTime dateTime, Map<String, AtomicReference<BigDecimal>> coins) {
        log.info(coins + " profit ratio: " + percent);
        try {
            printer.printRecord(dateTime.format(FORMATTER), Timestamp.valueOf(dateTime).getNanos(),
                    kcsBtc, dogeBtc, dogeKcs, percent);
            printer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
