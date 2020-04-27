package protocol.command;

import cache.Cache;
import cache.CacheEntry;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Assert;
import org.junit.Test;
import protocol.exception.CommandDecodingException;
import protocol.exception.MemCachedException;

public class SetCommandTest {
  @Test(expected = IllegalArgumentException.class)
  public void testCanNotCreateSetCommandWithNullCache() {
    new SetCommand(null);
  }

  @Test
  public void testSetCommandDecodeCorrectly() throws MemCachedException {
    final SetCommand command = new SetCommand(new MockCache());
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

  @Test
  public void testSetCommandDecodeTrimTrailingSpaces() throws MemCachedException {
    final SetCommand command = new SetCommand(new MockCache());
    final byte[] bytes = "key 2 1 5   \r\nvalue    \r\n".getBytes();
    final ByteBuf buf = Unpooled.buffer(bytes.length);
    buf.writeBytes(bytes);

    command.decode(buf);

    Assert.assertEquals("key", command.getKey());
    Assert.assertEquals("value", new String(command.getData()));
    Assert.assertEquals(5, command.getDataSize());
    Assert.assertEquals(1, command.getExpTime());
    Assert.assertEquals(2, command.getFlags());
  }

  @Test(expected = CommandDecodingException.class)
  public void testSetCommandDecodeThrowsCommandDecodingExceptionWhenBufferIsEmpty() throws MemCachedException {
    final SetCommand command = new SetCommand(new MockCache());
    final ByteBuf buf = Unpooled.buffer(0);
    buf.writeBytes(new byte[0]);

    command.decode(buf);
  }

  @Test
  public void testSetCommandExecutesCorrectly() throws MemCachedException {
    final String key = "key";
    final String value = "value";

    final Cache fakeCache = new MockCache();
    final Command command = new SetCommand(fakeCache);

    final byte[] bytes = String.format("%s %d %d %d\r\n%s\r\n", key, 1, 2, value.length(), value).getBytes();
    final ByteBuf buf = Unpooled.buffer(bytes.length);
    buf.writeBytes(bytes);

    command.decode(buf);

    final Command.CommandResult commandResult = command.execute();
    Assert.assertEquals(Command.CommandType.SET, commandResult.getType());
    Assert.assertEquals(Command.STORED, new String(commandResult.getResultValue()));

    final CacheEntry result = fakeCache.get(key);
    Assert.assertEquals(value, new String(result.getValue()));
    Assert.assertEquals(key, result.getKey());
    Assert.assertEquals(1, result.getFlags());
    Assert.assertEquals(2, result.getExpTime());
  }

  @Test
  public void testSetCommandExecutesReturnServerErrorWhenFailsToSet() throws MemCachedException {
    final String key = "key";
    final String value = "value";

    // Use faultInjector constructor
    final Cache fakeCache = new MockCache(null, () -> false);
    final Command command = new SetCommand(fakeCache);

    final byte[] bytes = String.format("%s %d %d %d\r\n%s\r\n", key, 1, 2, value.length(), value).getBytes();
    final ByteBuf buf = Unpooled.buffer(bytes.length);
    buf.writeBytes(bytes);

    command.decode(buf);

    final Command.CommandResult commandResult = command.execute();
    Assert.assertEquals(Command.CommandType.SET, commandResult.getType());
    Assert.assertEquals(Command.SERVER_ERROR, new String(commandResult.getResultValue()));
  }

  @Test(expected = MemCachedException.class)
  public void testSetCommandExecutesThrowsMemCachedException() throws MemCachedException {
    final String key = "key";
    final String value = "value";

    // Use faultInjector constructor
    final Cache fakeCache = new MockCache(null, () -> {
      throw new RuntimeException("Oops");
    });

    final Command command = new SetCommand(fakeCache);

    final byte[] bytes = String.format("%s %d %d %d\r\n%s\r\n", key, 1, 2, value.length(), value).getBytes();
    final ByteBuf buf = Unpooled.buffer(bytes.length);
    buf.writeBytes(bytes);

    command.decode(buf);

    command.execute();
  }
}
