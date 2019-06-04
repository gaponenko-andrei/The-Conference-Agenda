package agp.vo;

import static com.google.common.base.Preconditions.checkArgument;

import java.time.Duration;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Value;

@Value
@EqualsAndHashCode
public final class Talk implements Event {

  @NonNull
  private final String title;

  @NonNull
  private final Duration duration;


  public Talk(@NonNull String title, @NonNull Duration duration) {
    validate(duration);
    this.title = title.trim();
    this.duration = duration;
  }

  private static void validate(Duration duration) {
    long minutes = duration.toMinutes();
    checkArgument(
      minutes >= 5 && minutes <= 60,
      "Duration of a talk must be 5 <= minutes <= 60."
    );
  }
}