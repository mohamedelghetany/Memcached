package protocol.command;

import cache.Cache;
import cache.CacheEntry;
import javax.annotation.Nonnull;
import protocol.exception.MemCacheException;

public class ReplaceCommand extends SetCommand {
  public ReplaceCommand(@Nonnull Cache cache) {
    super(cache);
  }

  @Override
  public CommandResult execute() throws MemCacheException {
    final CacheEntry entry = new CacheEntry(getKey(), getData(), getFlags(), getExpTime());
    final boolean setResult = getCache().replace(entry);
    final String strResult = setResult ? "STORED\r\n" : "NOT_STORED\r\n";

    return new CommandResult(getType(), strResult.getBytes());
  }

  @Override
  public CommandType getType() {
    return CommandType.REPLACE;
  }
}
