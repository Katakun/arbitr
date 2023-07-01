package arbitr;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RequiredArgsConstructor
@Component
public class Runner implements CommandLineRunner {
    private final PriceUpdater priceUpdater;
    private final Parser parser;
    private final Environment environment;

    @Override
    public void run(String... args) throws InterruptedException {
        String chains = environment.getProperty("CHAINS");
        if (null == chains || chains.isEmpty()) {
            throw new IllegalArgumentException("Chain is empty");
        }
        String[] chainsArr = chains.split(",");
        List<Worker> workerList = new ArrayList<>();
        for (String chain : chainsArr) {
            workerList.add(new Worker(parser, priceUpdater, chain));
        }
        ExecutorService executor = Executors.newFixedThreadPool(
                workerList.size(), runnable -> new Thread(runnable, "arbitrThread"));
        executor.invokeAll(workerList);
        executor.shutdown();
    }
}
