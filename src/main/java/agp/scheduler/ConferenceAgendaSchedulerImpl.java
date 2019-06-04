package agp.scheduler;

import static java.util.stream.Collectors.toSet;

import java.time.Duration;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

import agp.util.Events;
import agp.vo.AfternoonSession;
import agp.vo.ConferenceTrack;
import agp.vo.Lunch;
import agp.vo.MorningSession;
import agp.vo.NetworkingEvent;
import agp.vo.Talk;
import lombok.NonNull;
import lombok.val;

public class ConferenceAgendaSchedulerImpl implements ConferenceAgendaScheduler {

  /* duration of morning session */
  private final Duration MS_DURATION = Duration.ofHours(3);

  /* maximum duration of afternoon session */
  private final Duration MAX_AS_DURATION = Duration.ofHours(4);

  /* minimum duration of track session */
  private final Duration MIN_TRACK_DURATION = MS_DURATION.plus(Duration.ofMinutes(1));

  /* maximum duration of track session */
  private final Duration MAX_TRACK_DURATION = MS_DURATION.plus(MAX_AS_DURATION);


  @Override
  public Set<ConferenceTrack> apply(@NonNull final Set<Talk> availableTalks) {
    validate(availableTalks);
    val morningSessionsSchedulingResult = scheduleMorningSessionsFor(availableTalks);
    return scheduleTracksBasedOn(morningSessionsSchedulingResult);
  }

  private void validate(Set<Talk> talks) {
    val minutes = Events.durationOf(talks).toMinutes();

    if (minutes <= MIN_TRACK_DURATION.toMinutes()) {
      throw new IllegalArgumentException(
        "Overall duration of talks must be > " + MIN_TRACK_DURATION.toHours() +
          " hours to schedule at least one track of morning & afternoon session."
      );
    }
  }

  /* Methods to schedule required number of morning sessions */

  private MorningSessionsScheduler.Result scheduleMorningSessionsFor(Set<Talk> talks) {
    val requiredTracksNumber = calcRequiredTracksNumberFor(talks);
    try {
      return new MorningSessionsSchedulerImpl(requiredTracksNumber).apply(talks);
    } catch (Exception ex) {
      throw new SchedulingException("Failed to schedule conference agenda.", ex);
    }
  }

  private int calcRequiredTracksNumberFor(Set<Talk> talks) {
    val talksDurationInMinutes = Events.durationOf(talks).toMinutes();
    val maxTrackDurationInMunutes = MAX_TRACK_DURATION.toMinutes();
    val div = talksDurationInMinutes / maxTrackDurationInMunutes;
    val mod = talksDurationInMinutes % maxTrackDurationInMunutes;
    return (int) (mod == 0 ? div : div + 1);
  }

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
  /*                                Tracks Scheduler                                 */
  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

  private Set<ConferenceTrack> scheduleTracksBasedOn(
    MorningSessionsScheduler.Result morningSessionsSchedulingResult) {

    return new TracksScheduler(morningSessionsSchedulingResult).schedule();
  }

  private static final class TracksScheduler {

    private final ImmutableList<MorningSession> morningSessions;
    private final ImmutableSet<Talk> unusedTalks;
    private final Integer tracksNumber;
    private final Multimap<Integer, Talk> unusedTalksByTracksNumber;


    private TracksScheduler(@NonNull MorningSessionsScheduler.Result morningSessionsSchedulingResult) {
      this.morningSessions = ImmutableList.copyOf(morningSessionsSchedulingResult.sessions());
      this.unusedTalks = morningSessionsSchedulingResult.unusedTalks();
      this.tracksNumber = morningSessions.size();
      this.unusedTalksByTracksNumber = distributeAmongTracks(unusedTalks, tracksNumber);
    }

    Set<ConferenceTrack> schedule() {
      return unusedTalksByTracksNumber
        .asMap().entrySet().stream()
        .map(this::scheduleConferenceTrack)
        .collect(toSet());
    }

    private ConferenceTrack scheduleConferenceTrack(Map.Entry<Integer, Collection<Talk>> entry) {
      val morningSession = morningSessions.get(entry.getKey());
      val unusedTalksForTrack = ImmutableSet.copyOf(entry.getValue());
      val afternoonSession = AfternoonSession.of(unusedTalksForTrack);

      return ConferenceTrack.builder()
        .scheduleSequence(morningSession)
        .scheduleEvent(new Lunch())
        .scheduleSequence(afternoonSession)
        .scheduleEvent(new NetworkingEvent())
        .build();
    }

    private static Multimap<Integer, Talk> distributeAmongTracks(
      final Set<Talk> unusedTalks, final int tracksNumber) {

      val indexBuilder = ImmutableMultimap.<Integer, Talk>builder();
      val unusedTalksDeque = new ArrayDeque<Talk>(unusedTalks);
      val lastTrackIndex = tracksNumber - 1;

      for (int trackIndex = 0; !unusedTalksDeque.isEmpty(); ) {
        val unusedTalk = unusedTalksDeque.pollFirst();
        indexBuilder.put(trackIndex, unusedTalk);
        trackIndex = (trackIndex == lastTrackIndex) ? 0 : trackIndex + 1;
      }

      return indexBuilder.build();
    }
  }
}
