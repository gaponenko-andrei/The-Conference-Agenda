package agp.util;

import java.nio.file.Path;
import java.nio.file.Paths;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TestResources {

  private static final Path RESOURCES_DIRECTORY;

  static {
    try {
      RESOURCES_DIRECTORY = Paths.get(
        TestResources.class
          .getClassLoader()
          .getResource("./")
          .toURI()
      );
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static Path get(@NonNull final String fileName) {
    return RESOURCES_DIRECTORY.resolve(fileName);
  }
}
