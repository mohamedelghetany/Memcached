package protocol.command;

import cache.Cache;
import cache.CacheEntry;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Assert;
import org.junit.Test;
import protocol.exception.CommandDecodingException;
import protocol.exception.MemCachedException;

public class GetCommandTest {

  @Test(expected = IllegalArgumentException.class)
  public void testCanNotCreateGetCommandWithNullCache() {
    new GetCommand(null);
  }

  @Test
  public void testGetCommandDecodeCorrectly() throws MemCachedException {
    final Cache cache = new MockCache();
    final GetCommand command = new GetCommand(cache);
    final byte[] bytes = "foo\r\n".getBytes();
    final ByteBuf buf = Unpooled.buffer(bytes.length);

    buf.writeBytes(bytes);

    command.decode(buf);

    Assert.assertEquals("foo", command.getKey());
  }

  @Test
  public void testGetCommandDecodeTrimSpaces() throws MemCachedException {
    final GetCommand command = new GetCommand(new MockCache());
    final byte[] bytes = "   bar    \r\n".getBytes();
    final ByteBuf buf = Unpooled.buffer(bytes.length);
    buf.writeBytes(bytes);

    command.decode(buf);

    Assert.assertEquals("bar", command.getKey());
  }

  @Test(expected = CommandDecodingException.class)
  public void testGetCommandDecodeThrowsCommandDecodingExceptionWhenBufferIsEmpty() throws MemCachedException {
    final GetCommand command = new GetCommand(new MockCache());
    final byte[] bytes = new byte[0];
    final ByteBuf buf = Unpooled.buffer(bytes.length);
    buf.writeBytes(bytes);

    command.decode(buf);
  }

  @Test
  public void testGetCommandExecutesCorrectlyIfKeyExists() throws MemCachedException {
    final String key = "foo";
    final String value = "bar";
    final String expectedResult = "VALUE " + key + " 0 " + value.length() + "\r\n" + value + "\r\nEND\n";

    final Cache fakeCache = new MockCache();
    fakeCache.set(new CacheEntry(key, value.getBytes(), 0, 0));
    final GetCommand command = new GetCommand(fakeCache);
    command.setKey(key);

    final Command.CommandResult commandResult = command.execute();
    Assert.assertEquals(Command.CommandType.GET, commandResult.getType());

    Assert.assertEquals(expectedResult, new String(commandResult.getResultValue()));
  }

  @Test
  public void testGetCommandExecutesCorrectlyIfKeyDoesNotExist() throws MemCachedException {
    final String expectedResult = "END\r\n";

    final GetCommand command = new GetCommand(new MockCache());
    command.setKey("missing");

    final Command.CommandResult commandResult = command.execute();
    Assert.assertEquals(Command.CommandType.GET, commandResult.getType());

    Assert.assertEquals(expectedResult, new String(commandResult.getResultValue()));
  }

  @Test(expected = MemCachedException.class)
  public void testGetCommandExecutesThrowsMemCachedException() throws MemCachedException {
    // Use faultInjector constructor
    final MockCache mockCache = new MockCache(() -> {
      throw new RuntimeException("Get failed");
    }, null);
    final GetCommand command = new GetCommand(mockCache);

    command.execute();
  }
}
