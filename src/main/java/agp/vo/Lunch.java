package agp.vo;

import java.time.Duration;

public class Lunch implements Event {

  @Override
  public String title() {
    return "Lunch";
  }

  @Override
  public Duration duration() {
    return Duration.ofHours(1);
  }
}
