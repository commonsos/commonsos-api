package commonsos;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;
import spark.embeddedserver.EmbeddedServer;
import spark.embeddedserver.EmbeddedServers;
import spark.embeddedserver.jetty.EmbeddedJettyFactory;
import spark.embeddedserver.jetty.EmbeddedJettyServer;
import spark.embeddedserver.jetty.JettyHandler;
import spark.embeddedserver.jetty.JettyServerFactory;
import spark.http.matching.MatcherFilter;
import spark.route.Routes;
import spark.staticfiles.StaticFilesConfiguration;

public class CookieSecuringEmbeddedJettyFactory extends EmbeddedJettyFactory {
  @Override
  public EmbeddedServer create(Routes routeMatcher, StaticFilesConfiguration staticFilesConfiguration, boolean hasMultipleHandler) {
    MatcherFilter matcherFilter = new MatcherFilter(routeMatcher, staticFilesConfiguration, false, hasMultipleHandler);
    matcherFilter.init(null);

    JettyHandler handler = new JettyHandler(matcherFilter);
    handler.setHttpOnly(true);

    return new EmbeddedJettyServer(new JettyServerFactory() {
      @Override
      public Server create(int maxThreads, int minThreads, int threadTimeoutMillis) {
        if (maxThreads > 0) {
          int min = (minThreads > 0) ? minThreads : 8;
          int idleTimeout = (threadTimeoutMillis > 0) ? threadTimeoutMillis : 60000;

          return new Server(new QueuedThreadPool(maxThreads, min, idleTimeout));
        } else {
          return new Server();
        }
      }

      @Override
      public Server create(ThreadPool threadPool) {
        return threadPool != null ? new Server(threadPool) : new Server();
      }
    }, handler);
  }

  static void register() {
    EmbeddedServers.add(EmbeddedServers.Identifiers.JETTY, new CookieSecuringEmbeddedJettyFactory());
  }
}
