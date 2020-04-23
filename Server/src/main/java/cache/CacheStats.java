package cache;

import java.util.concurrent.atomic.AtomicInteger;
import org.apache.log4j.Logger;
import protocol.ServerProperties;

/**
 * Singleton class that contains Statistics about the performance
 * of a {@link cache.Cache}.
 *
 * To get an instance of this class, use {@link CacheStats#getInstance()}
 * Call {@link CacheStats#initialize()} to reset statistics
 *
 * This class also starts a {@link StatsReporter} thread once an instance created.
 * This thread reports Cache Stats every {@link ServerProperties#cacheStatsReporterIntervalInMS}.
 * Currently the reporter just Logs but that can be changed to publish to any additional monitoring tools
 *
 * Current statistics are:
 *
 * {@link CacheStats#hitCount} gets incremented every time a cache hit happen
 * {@link CacheStats#misCount} gets incremented every time a cache miss happen
 * {@link CacheStats#evictionCount} gets incremented every time an entry gets evicted
 */
public class CacheStats {

  private static CacheStats INSTANCE = null;
  private AtomicInteger hitCount = new AtomicInteger();
  private AtomicInteger misCount = new AtomicInteger();
  private AtomicInteger evictionCount = new AtomicInteger();

  private CacheStats() {
    final Thread statsReporterThread = new Thread(new StatsReporter());
    statsReporterThread.setName("CacheStats-StatsReporter");
    statsReporterThread.start();
  }

  public static CacheStats getInstance() {
    if (INSTANCE == null) {
      synchronized (CacheStats.class) {
        if (INSTANCE == null) {
          INSTANCE = new CacheStats();
        }
      }
    }

    return INSTANCE;
  }

  public void initialize() {
    hitCount.set(0);
    misCount.set(0);
    evictionCount.set(0);
  }

  public void reportCacheHit() {
    hitCount.addAndGet(1);
  }

  public void reportCacheMiss() {
    misCount.addAndGet(1);
  }

  public void reportEviction() {
    evictionCount.addAndGet(1);
  }

  public AtomicInteger getHitCount() {
    return hitCount;
  }

  public AtomicInteger getMisCount() {
    return misCount;
  }

  public AtomicInteger getEvictionCount() {
    return evictionCount;
  }

  public static class StatsReporter implements Runnable {
    private static final Logger logger = Logger.getLogger(StatsReporter.class);

    @Override
    public void run() {
      try {
        logger.info("Starting CacheStats reporter");

        while (true) {
          final String report = String.format("CacheStats - Hit Count: %d, Miss Count: %d, Evicted: %d",
              CacheStats.getInstance().getHitCount().get(),
              CacheStats.getInstance().getMisCount().get(),
              CacheStats.getInstance().getEvictionCount().get());

          logger.info(report);

          Thread.sleep(ServerProperties.cacheStatsReporterIntervalInMS.get());
        }
      } catch (InterruptedException e) {
        logger.warn("StatsReporter sleep interrupted");
      }
    }
  }
}
