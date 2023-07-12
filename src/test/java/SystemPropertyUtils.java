/**
 * (c) 2003-2021 MuleSoft, Inc. The software in this package is
 * published under the terms of the Commercial Free Software license V.1, a copy of which
 * has been included with this distribution in the LICENSE.md file.
 */
import java.util.Properties;

public class SystemPropertyUtils {

  public static void setSystemProperty(String key, String value) {
    try {
      Properties props = System.getProperties();
      props.setProperty(key, value);
    } catch (NullPointerException e) {
      return;
    } catch (Exception e) {
      throw new IllegalStateException("Failed to set system property", e);
    }
  }

  public static String getSystemProperty(String key) {
    try {
      Properties props = System.getProperties();
      return props.getProperty(key);
    } catch (Exception e) {
      throw new IllegalStateException("Failed to get system property", e);
    }
  }

  public static void clearSystemProperty(String key) {
    System.clearProperty(key);
  }
}
