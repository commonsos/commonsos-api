package commonsos;

import lombok.extern.slf4j.Slf4j;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Slf4j @Singleton
public class Web3jProvider implements Provider<Web3j>{

  @Inject Configuration configuration;

  private Web3j instance;

  @Inject void init() {
    this.instance = Web3j.build(new HttpService(configuration.ethererumUrl()));
  }

  @Override public Web3j get() {
    log.info("Web3jProvider get " + instance);
    return instance;
  }
}
