package protocol.command;

import cache.Cache;
import cache.CacheEntry;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Assert;
import org.junit.Test;
import protocol.exception.CommandDecodingException;
import protocol.exception.MemCachedException;

public class ReplaceCommandTest {

  @Test(expected = IllegalArgumentException.class)
  public void testCanNotCreateReplaceCommandWithNullCache() {
    new ReplaceCommand(null);
  }

  @Test
  public void testReplaceCommandDecodeCorrectly() throws MemCachedException {
    final ReplaceCommand command = new ReplaceCommand(new MockCache());
    final byte[] bytes = "foo 2 1 3\r\nbar\r\n".getBytes();
    final ByteBuf buf = Unpooled.buffer(bytes.length);
    buf.writeBytes(bytes);

    command.decode(buf);

    Assert.assertEquals("foo", command.getKey());
    Assert.assertEquals(3, command.getDataSize());
    Assert.assertEquals(1, command.getExpTime());
    Assert.assertEquals(2, command.getFlags());
    Assert.assertEquals("bar", new String(command.getData()));
  }

  @Test(expected = CommandDecodingException.class)
  public void testReplaceCommandDecodeThrowsCommandDecodingExceptionWhenBufferIsEmpty() throws MemCachedException {
    final ReplaceCommand command = new ReplaceCommand(new MockCache());
    final ByteBuf buf = Unpooled.buffer(0);
    buf.writeBytes(new byte[0]);

    command.decode(buf);
  }

  @Test
  public void testReplaceCommandExecutesCorrectly() throws MemCachedException {
    final String key = "foo";
    final String value = "bar";

    final Cache fakeCache = new MockCache();

    final Command command = new ReplaceCommand(fakeCache);
    final byte[] bytes = String.format("%s %d %d %d\r\n%s\r\n", key, 1, 2, value.length(), value).getBytes();
    final ByteBuf buf = Unpooled.buffer(bytes.length);
    buf.writeBytes(bytes);

    command.decode(buf);

    final Command.CommandResult commandResult = command.execute();
    Assert.assertEquals(Command.CommandType.REPLACE, commandResult.getType());
    Assert.assertEquals(Command.STORED, new String(commandResult.getResultValue()));

    final CacheEntry result = fakeCache.get(key);
    Assert.assertEquals(value, new String(result.getValue()));
    Assert.assertEquals(key, result.getKey());
    Assert.assertEquals(1, result.getFlags());
    Assert.assertEquals(2, result.getExpTime());
  }

  @Test
  public void testReplaceCommandExecutesReturnNotStoredWhenFails() throws MemCachedException {
    final String key = "key";
    final String value = "value";

    // Use faultInjector constructor
    final Cache fakeCache = new MockCache(null, () -> false);
    final Command command = new ReplaceCommand(fakeCache);

    final byte[] bytes = String.format("%s %d %d %d\r\n%s\r\n", key, 1, 2, value.length(), value).getBytes();
    final ByteBuf buf = Unpooled.buffer(bytes.length);
    buf.writeBytes(bytes);

    command.decode(buf);

    final Command.CommandResult commandResult = command.execute();
    Assert.assertEquals(Command.CommandType.REPLACE, commandResult.getType());
    Assert.assertEquals(Command.NOT_STORED, new String(commandResult.getResultValue()));
  }

  @Test(expected = MemCachedException.class)
  public void testReplaceCommandExecutesThrowsMemCachedException() throws MemCachedException {
    final String key = "key";
    final String value = "value";

    // Use faultInjector constructor
    final Cache fakeCache = new MockCache(null, () -> {
      throw new RuntimeException("Replace failed");
    });

    final Command command = new ReplaceCommand(fakeCache);

    final byte[] bytes = String.format("%s %d %d %d\r\n%s\r\n", key, 1, 2, value.length(), value).getBytes();
    final ByteBuf buf = Unpooled.buffer(bytes.length);
    buf.writeBytes(bytes);

    command.decode(buf);

    command.execute();
  }
}
