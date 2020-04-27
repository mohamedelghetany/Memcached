package protocol;

import org.junit.Assert;
import org.junit.Test;

public class ServerPropertiesTest {
  @Test
  public void testIntegerPropertyKeyDefaultsToDefaultValue() {
    final ServerProperties.IntegerPropertyKey propertyKey = new ServerProperties.IntegerPropertyKey("foo", 1);

    Assert.assertEquals(1, propertyKey.getDefaultValue().intValue());
    Assert.assertEquals(1, propertyKey.get().intValue());
  }
}
