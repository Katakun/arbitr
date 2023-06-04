package arbitr;

import com.kucoin.sdk.KucoinClientBuilder;
import com.kucoin.sdk.KucoinPublicWSClient;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SuppressWarnings({"BusyWait", "InfiniteLoopStatement"})
@Log4j2
@SpringBootApplication
public class ArbitrApplication {

	@SneakyThrows
	public static void main(String[] args) {
		SpringApplication.run(ArbitrApplication.class, args);

		KucoinPublicWSClient kucoinPublicWSClient = new KucoinClientBuilder().withBaseUrl("https://api.kucoin.com")
				.buildPublicWSClient();

		String ack = kucoinPublicWSClient.onTicker(response -> System.out.println("ask: " + response.getData().getBestAsk() + " volume ask: " + response.getData().getBestAskSize() +
				"  bid: " + response.getData().getBestBid() + " volume: bid " + response.getData().getBestBidSize()), "KCS-BTC", "DOGE-BTC", "DOGE-KCS");

		log.info("ack: " + ack);
		String requsetId = ack;
		while (true) {
			Thread.sleep(1000);
			requsetId = kucoinPublicWSClient.ping(requsetId);
		}
	}

}
