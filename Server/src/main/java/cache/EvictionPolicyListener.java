package cache;

import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nonnull;

public interface EvictionPolicyListener extends Consumer<EvictionPolicyListener.EvictionPolicyMessage> {

  class EvictionPolicyMessage {
    private final Map<String, LinkedCacheEntry> cache;
    private final LinkedCacheEntry entry;

    public EvictionPolicyMessage(@Nonnull final Map<String, LinkedCacheEntry> cache, @Nonnull final LinkedCacheEntry entry) {
      this.cache = cache;
      this.entry = entry;
    }

    public LinkedCacheEntry getEntry() {
      return entry;
    }

    public Map<String, LinkedCacheEntry> getCache() {
      return cache;
    }
  }
}
