package arbitr;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class Runner implements CommandLineRunner {
    private final PriceUpdater priceUpdater;
    private final Parser parser;
    private final Environment environment;

    @Override
    public void run(String... args) {
        String chain = environment.getProperty("CHAIN");
        if (null == chain || chain.isEmpty()) {
            throw new IllegalArgumentException("Chain is empty");
        }
        new Worker(parser, priceUpdater, chain).run();
    }
}
