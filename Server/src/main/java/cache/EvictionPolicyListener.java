package cache;

import java.util.function.Consumer;
import javax.annotation.Nonnull;

public interface EvictionPolicyListener {

  void notify(@Nonnull final EvictionPolicyMessage message);

  class EvictionPolicyMessage {
    private final Cache cache;
    private final LinkedCacheEntry entry;

    public EvictionPolicyMessage(@Nonnull final Cache cache, @Nonnull final LinkedCacheEntry entry) {
      this.cache = cache;
      this.entry = entry;
    }

    public LinkedCacheEntry getEntry() {
      return entry;
    }

    public Cache getCache() {
      return cache;
    }
  }
}
