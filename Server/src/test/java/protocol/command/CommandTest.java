package protocol.command;

import org.junit.Assert;
import org.junit.Test;
import protocol.exception.UnsupportedCommandException;

public class CommandTest {
  @Test
  public void testCommandFactoryReturnsTheCorrectCommand() throws UnsupportedCommandException {
    final MockCache mockCache = new MockCache();

    Command command = Command.CommandFactory.createCommand("get", mockCache);
    Assert.assertTrue(command instanceof GetCommand);
    Assert.assertEquals(Command.CommandType.GET, command.getType());

    command = Command.CommandFactory.createCommand("set", mockCache);
    Assert.assertTrue(command instanceof SetCommand);
    Assert.assertEquals(Command.CommandType.SET, command.getType());


    command = Command.CommandFactory.createCommand("replace", mockCache);
    Assert.assertTrue(command instanceof ReplaceCommand);
    Assert.assertEquals(Command.CommandType.REPLACE, command.getType());

    command = Command.CommandFactory.createCommand("add", mockCache);
    Assert.assertTrue(command instanceof AddCommand);
    Assert.assertEquals(Command.CommandType.ADD, command.getType());
  }

  @Test(expected = UnsupportedCommandException.class)
  public void testCommandFactoyThrowsForUnknownCommands() throws UnsupportedCommandException {
    Command.CommandFactory.createCommand("foo", new MockCache());
  }

  @Test(expected = UnsupportedCommandException.class)
  public void testCommandFactoryIsCaseSensitive() throws UnsupportedCommandException {
    Command.CommandFactory.createCommand("Get", new MockCache());
  }
}
