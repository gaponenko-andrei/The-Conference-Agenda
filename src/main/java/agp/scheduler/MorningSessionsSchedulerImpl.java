package agp.scheduler;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.String.format;

import java.time.Duration;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import agp.vo.MorningSession;
import agp.vo.Talk;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.Accessors;
import lombok.val;

@Accessors(fluent = true)
public class MorningSessionsSchedulerImpl implements MorningSessionsScheduler {

  @NonNull @Getter
  private final MorningSessionScheduler sessionScheduler;

  @NonNull @Getter
  private final Integer requiredSessionsNumber;


  public MorningSessionsSchedulerImpl(Integer requiredSessionsNumber) {
    this(MorningSessionSchedulerImpl.using(Duration.ofHours(3)).build(), requiredSessionsNumber);
  }

  @Override
  public Result apply(@NonNull final Set<Talk> availableTalks) {
    validateNumberOf(availableTalks);

    val morningSessionsSetBuilder = ImmutableSet.<MorningSession>builder();
    Set<Talk> unusedTalks = availableTalks;

    for (int i = 0; i < requiredSessionsNumber; i++) {
      MorningSessionScheduler.Result result = scheduleSessionFrom(unusedTalks);
      morningSessionsSetBuilder.add(result.session());
      unusedTalks = result.unusedTalks();
    }

    val morningSessions = morningSessionsSetBuilder.build();
    return new Result(morningSessions, unusedTalks);
  }

  private void validateNumberOf(Set<Talk> talks) {
    checkArgument(
      talks.size() >= requiredSessionsNumber,
      "Number of talks should be at least %s.",
      requiredSessionsNumber
    );
  }

  private MorningSessionScheduler.Result scheduleSessionFrom(Set<Talk> talks) {
    try {
      return sessionScheduler.apply(talks);
    } catch (Exception ex) {
      throw newSchedulingException(ex);
    }
  }

  private SchedulingException newSchedulingException(Exception cause) {
    return new SchedulingException(format(
      "Failed to schedule required number (%s) of " +
        "sessions.", requiredSessionsNumber), cause
    );
  }

  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */
  /*                             Builder related stuff                               */
  /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

   /* Builder is mostly a replacement for partial application & carrying,
   to avoid 'apply' with several arguments when it's possible & convenient. */

  @lombok.Builder(builderClassName = "Builder")
  private MorningSessionsSchedulerImpl(
    @NonNull final MorningSessionScheduler sessionScheduler,
    @NonNull final Integer requiredSessionsNumber) {

    this.sessionScheduler = sessionScheduler;
    this.requiredSessionsNumber = requiredSessionsNumber;
  }

  /* Default field values for Lombok-generated Builder */

  public static final class Builder {
    private MorningSessionScheduler sessionScheduler =
      MorningSessionSchedulerImpl
        .using(Duration.ofHours(3))
        .build();
  }

  /* Shortcut methods for Builder creation */

  public static Builder using(@NonNull MorningSessionScheduler sessionScheduler) {
    return builder().sessionScheduler(sessionScheduler);
  }
}
