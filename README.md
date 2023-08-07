# Arbitrage chain mointor

A program for monitoring intrabourse arbitrage is a computer system or application designed to automatically track price
disparities of the same asset on the same exchange or between different trading pairs within the same exchange. The
program identifies arbitrage opportunities (profit opportunities arising from temporary or structural price differences)
and provides information about such opportunities to traders or investors for further action. This enables efficient
utilization of price discrepancies on the same exchange or between different trading pairs to buy assets at a lower
price and sell them at a higher price, thus generating profits with minimal risk.

# Quick start

```commandline
git clone https://github.com/Katakun/arbitr.git
cd arbitr
docker-compose up -d
```

# How to use

To ensure that the program has started successfully, execute

```commandline
docker-compose logs -f
```

After starting Docker Compose, the program begins to monitor cryptocurrency prices on the Kucoin exchange specified in
the chains in the docker-compose configuration. Files containing the names of arbitrage chains will appear in the "
share" directory. Once the profit from arbitrage exceeds the exchange commission, these files will start showing rows
with prices and the percentage of arbitrage. Until that moment, the files will remain empty.
The result in a file should be like this:

```commandline
Data,Timestamp,TRX-USDT,KLV-TRX,KLV-USDT,Profit ratio
2023-07-05 11:07:52,1688555272124,0.03977,0.003099,0.077081,0.192432100
```

# How to build
You need to clone repository and be inside the *arbitr* directory
```commandline
mvn clean install
docker build -t arbitr .
```

# How to run own image
(you need share directory)
```commandline
docker run -d -e "CHAINS=USDT->ADA->BTC,USDC->DOGE->USDT,USDC->ETH->USDT,USDC->DOGE->BTC,USDT->XRP->USDC,USDT->BCH->BTC,USDT->SOL->USDC,USDC->LTC->USDT,USDT->BNB->USDC,USDC->ETH->BTC,BTC->LTC->USDT,BTC->ETH->USDT,USDC->XRP->BTC,BTC->DASH->USDT,USDC->LTC->BTC,USDT->LTC->USDC,BTC->BCH->USDT,BTC->ADA->USDT,BTC->EWT->USDT,USDT->OUSD->BTC,BTC->MATIC->USDT,BTC->XMR->USDT,BTC->DOGE->USDC,USDT->EWT->BTC,USDT->XMR->BTC,TRX->KLV->USDT,USDC->SOL->USDT,BTC->XRP->USDT,USDT->ETH->USDC,USDT->BDX->BTC,USDT->DOGE->USDC,USDT->DOGE->BTC,USDC->BNB->USDT,BTC->BNB->USDT,BTC->OUSD->USDT,BTC->XRP->USDC,USDC->BNB->BTC,BTC->BNB->USDC,USDT->XRP->BTC,BTC->LTC->USDC,USDT->BNB->BTC,USDC->XRP->USDT,USDT->BTC->USDC,USDT->MATIC->BTC,USDC->BTC->USDT,BTC->ETH->USDC,USDT->KLV->TRX,USDT->LTC->BTC,BTC->DOGE->USDT,USDT->ETH->BTC,BTC->BDX->USDT,USDT->DASH->BTC" -v $(pwd)/share:/app/share arbitr
```
Get the container id from command
```commandline
docker ps
```

After that check the logs
```commandline
docker logs a7cc -f
```
