package agp;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Verify.verify;
import static java.lang.String.format;
import static java.nio.file.Files.exists;
import static java.util.stream.Collectors.toSet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Arrays;
import java.util.Set;
import java.util.function.Function;

import com.google.common.collect.Iterables;

import agp.vo.Talk;
import lombok.NonNull;
import lombok.val;

public class InputFileParser implements Function<Path, Set<Talk>> {

  @Override
  public Set<Talk> apply(@NonNull final Path path) {
    requireExistingFile(path);
    try {
      return parseTalks(path);
    } catch (Exception ex) {
      throw new IllegalArgumentException("Invalid file format.", ex);
    }
  }

  private void requireExistingFile(Path path) {
    checkArgument(exists(path), format("File '%s' is not found.", path));
  }

  private Set<Talk> parseTalks(Path path) throws IOException {
    return Files.lines(path)
      .skip(1)                // ignore talks count
      .map(this::parseTalk)
      .collect(toSet());
  }

  private Talk parseTalk(String talkString) {
    val chunks = Arrays.asList(talkString.split(" "));
    val durationString = Iterables.getLast(chunks);
    val title = talkString.replace(durationString, "");
    verify(isDuration(durationString));

    if (durationString.contains("min")) {
      val minutesString = durationString.replace("min", "");
      val minutes = Long.valueOf(minutesString);
      return new Talk(title, Duration.ofMinutes(minutes));
    } else {
      return new Talk(title, Duration.ofMinutes(5));
    }
  }

  private boolean isDuration(String s) {
    return s.contains("min") || s.contains("lightning");
  }
}
