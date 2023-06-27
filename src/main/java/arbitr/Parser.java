package arbitr;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static arbitr.Constants.CHAIN_LENGTH;
import static java.util.stream.Collectors.toList;

@Log4j2
@RequiredArgsConstructor
@Component
public class Parser {
    public List<String> parse(String chainString) {
        // CHAIN=XCN->BTC->USDC
        log.info("Chain = " + chainString);
        if (null == chainString || chainString.isEmpty()) {
            throw new IllegalArgumentException("Chain is empty");
        }
        // "XCN", "BTC", "USDC"
        List<String> list = Arrays.stream(chainString.split("->"))
                .map(String::toUpperCase).collect(toList());
        if (list.size() != CHAIN_LENGTH) {
            throw new IllegalArgumentException("Size of chain must be " + CHAIN_LENGTH);
        }
        return list;
    }
}
