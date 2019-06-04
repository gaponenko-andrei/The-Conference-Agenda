package agp.vo;

import java.time.Duration;

public class NetworkingEvent implements Event {

  @Override
  public String title() {
    return "Networking event";
  }

  @Override
  public Duration duration() {
    return Duration.ofHours(2);
  }
}
