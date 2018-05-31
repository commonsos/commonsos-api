package commonsos.domain.blockchain;

import lombok.extern.slf4j.Slf4j;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.math.BigInteger;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import static java.lang.String.format;

@Singleton
@Slf4j
public class NonceProvider {

  @Inject private Web3j web3j;

  private ConcurrentMap<String, AtomicLong> userNonce = new ConcurrentHashMap();

  BigInteger nonceFor(String address) {
    userNonce.computeIfAbsent(address, networkNonceProvider());

    long nonce = userNonce.get(address).getAndIncrement();
    log.info(format("Using nonce %d for user %s", nonce, address));

    return new BigInteger(String.valueOf(nonce));
  }

  private Function<String, AtomicLong> networkNonceProvider() {
    return address -> {
      EthGetTransactionCount ethGetTransactionCount = null;
      try {
        ethGetTransactionCount = web3j.ethGetTransactionCount(address, DefaultBlockParameterName.LATEST).send();
        log.info(String.format("Nonce initialized as %d for address %s", ethGetTransactionCount.getTransactionCount(), address));
        return new AtomicLong(ethGetTransactionCount.getTransactionCount().longValue());
      }
      catch (IOException e) {
        throw new RuntimeException("Failed to create nonce for address " + address, e);
      }
    };
  }
}
