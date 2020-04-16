package cache;

import java.util.concurrent.atomic.AtomicInteger;
import org.apache.log4j.Logger;
import protocol.ServerProperties;

public class CacheStats {
  private static CacheStats INSTANCE = null;
  private AtomicInteger hitCount = new AtomicInteger();
  private AtomicInteger misCount = new AtomicInteger();

  private CacheStats() {
    final Thread statsReporterThread = new Thread(new StatsReporter());
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
  }

  public void reportCacheHit() {
    hitCount.addAndGet(1);
  }

  public void reportCaheMiss() {
    misCount.addAndGet(1);
  }

  public AtomicInteger getHitCount() {
    return hitCount;
  }

  public AtomicInteger getMisCount() {
    return misCount;
  }

  public static class StatsReporter implements Runnable {
    private static final Logger logger = Logger.getLogger(StatsReporter.class);

    @Override
    public void run() {
      try {
        logger.info("Starting CacheStats reporter");

        while (true) {
          final String report = String.format("CacheStats - Hit Count: %d, Miss Count: %d",
              CacheStats.getInstance().getHitCount().get(),
              CacheStats.getInstance().getMisCount().get());

          logger.info(report);

          Thread.sleep(ServerProperties.cacheStatsReporterIntervalInMS.get());
        }
      } catch (InterruptedException e) {
        logger.warn("StatsReporter sleep interrupted");
      }
    }
  }
}
