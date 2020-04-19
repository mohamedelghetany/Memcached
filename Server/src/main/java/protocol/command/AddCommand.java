package protocol.command;

import cache.Cache;
import cache.CacheEntry;
import javax.annotation.Nonnull;

public class AddCommand extends SetCommand {
  public AddCommand(@Nonnull final Cache cache) {
    super(cache);
  }

  @Override
  public CommandResult executeInternal() {
    final CacheEntry entry = new CacheEntry(getKey(), getData(), getFlags(), getExpTime());
    final boolean addResult = getCache().add(entry);
    final String strResult = addResult ? STORED : NOT_STORED;

    return new CommandResult(getType(), strResult.getBytes());
  }

  @Override
  public CommandType getType() {
    return CommandType.ADD;
  }
}
