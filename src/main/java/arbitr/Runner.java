package arbitr;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
        System.out.println("Chains amount: " + chainsArr.length);
        List<Worker> workerList = new ArrayList<>();
        for (String chain : chainsArr) {
            workerList.add(new Worker(parser, priceUpdater, chain));
        }

        final int corePoolSize = workerList.size();
        final int maximumPoolSize = workerList.size();
        final int keepAliveTime = 0;
        final BlockingQueue<Runnable> taskQueue = new LinkedBlockingQueue<>();
        final ThreadFactory threadFactory = Executors.defaultThreadFactory();
        final RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();

        final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime, TimeUnit.SECONDS,
                taskQueue,
                threadFactory,
                handler);

        workerList.forEach(threadPool::execute);
        threadPool.shutdown();
    }
}
