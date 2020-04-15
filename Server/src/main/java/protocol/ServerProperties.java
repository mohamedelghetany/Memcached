package protocol;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public final class ServerProperties {
  private static Properties propsFromFile = null;

  private ServerProperties() {
  }

  public static void initialize() throws IOException {
    propsFromFile = new Properties();
    FileInputStream in = new FileInputStream(ServerProperties.class.getClassLoader().getResource("").getPath() + "config.properties");
    propsFromFile.load(in);
    in.close();
  }

  public static PropertyKey<Integer> PORT = new IntegerPropertyKey("port", 11211);
  public static PropertyKey<Integer> maxCacheSize = new IntegerPropertyKey("maxCacheSize", 1000000);


  public static abstract class PropertyKey<T> {
    private final String key;
    private final T defaultValue;

    public PropertyKey(String key, T defaultValue) {
      this.key = key;
      this.defaultValue = defaultValue;
    }

    public String getKey() {
      return key;
    }

    protected T getDefaultValue() {
      return defaultValue;
    }

    public abstract T get();
  }

  public static final class IntegerPropertyKey extends PropertyKey<Integer> {

    public IntegerPropertyKey(String key, Integer defaultValue) {
      super(key, defaultValue);
    }

    @Override
    public Integer get() {
      final String value = propsFromFile.getProperty(getKey());

      if (value == null) {
        return getDefaultValue();
      }

      return Integer.valueOf(value);
    }
  }
}
