package agp;

import static agp.vo.ConferenceTrack.ScheduledEvent;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.Set;

import com.google.common.collect.Iterables;
import com.google.common.io.Resources;

import agp.scheduler.ConferenceAgendaSchedulerImpl;
import agp.vo.ConferenceTrack;
import agp.vo.Lunch;
import agp.vo.NetworkingEvent;
import agp.vo.Talk;
import lombok.val;

public class AppEntryPoint {

  private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");


  public static void main(String[] args) throws URISyntaxException {
    val inputFilePath = getInputFile(args);
    logUsed(inputFilePath);
    Set<Talk> talks = parseTalks(inputFilePath);
    Set<ConferenceTrack> tracks = scheduleTracksFrom(talks);
    print(tracks);
  }

  private static Path getInputFile(String[] args) throws URISyntaxException {
    if (args.length == 0) {
      return Paths.get(Resources.getResource("SampleInput.txt").toURI());
    } else {
      return Paths.get(args[0]);
    }
  }

  private static void logUsed(Path path) {
    System.out.println(String.format("File '%s' is used as input.", path));
  }

  private static Set<Talk> parseTalks(Path inputFilePath) {
    return new InputFileParser().apply(inputFilePath);
  }

  private static Set<ConferenceTrack> scheduleTracksFrom(Set<Talk> talks) {
    return new ConferenceAgendaSchedulerImpl().apply(talks);
  }

  private static void print(Set<ConferenceTrack> tracks) {
    System.out.println();
    for (int i = 0; i < tracks.size(); i++) {
      System.out.println("Track " + (i + 1) + ":");
      Iterables.get(tracks, i).events().forEach(AppEntryPoint::print);
      System.out.println();
    }
  }

  private static void print(ScheduledEvent event) {
    System.out.println(
      TIME_FORMATTER.format(event.startTime()) + " "
        + event.title() + " "
        + buildDurationStringFor(event)
    );
  }

  private static String buildDurationStringFor(ScheduledEvent event) {
    if (isLunchOrNetworking(event)) {
      return "";
    } else if (event.duration().toMinutes() == 5) {
      return "lightning";
    } else {
      return event.duration().toMinutes() + "min";
    }
  }

  private static boolean isLunchOrNetworking(ScheduledEvent event) {
    return event.delegate() instanceof Lunch
      || event.delegate() instanceof NetworkingEvent;
  }
}
