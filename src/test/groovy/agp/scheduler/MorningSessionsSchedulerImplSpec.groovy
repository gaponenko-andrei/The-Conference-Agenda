package agp.scheduler

import agp.vo.MorningSession
import agp.vo.Talk
import spock.lang.Specification

import java.time.Duration

import static agp.Utils.*
import static java.util.Collections.emptySet
import static java.util.Collections.singleton

class MorningSessionsSchedulerImplSpec extends Specification {

  def "Default morning session scheduler should be MorningSessionSchedulerImpl"() {

    expect: "MorningSessionSchedulerImpl as default morning session scheduler"
      scheduler.sessionScheduler() instanceof MorningSessionSchedulerImpl

    where: "Scheduler has default morning session scheduler"
      scheduler <<[
        new MorningSessionsSchedulerImpl(2),
        MorningSessionsSchedulerImpl.builder().requiredSessionsNumber(2).build()
      ]
  }

  def """Exception should be thrown when given number of talks
      is less then required number of morning sessions"""() {

    given: "Some valid scheduler instance"
      def scheduler = MorningSessionsSchedulerImpl
        .builder().requiredSessionsNumber(2).build()

    when: "Scheduler is applied"
      scheduler.apply(availableTalks)

    then: "Exception should be thrown"
      thrown(IllegalArgumentException)

    where: "Number of available talks is < required number of sessions"
      availableTalks << [emptySet(), nTalks(1)]
  }

  def """Exception should not be thrown when given number of talks
      is equal or greater then required number of morning sessions"""() {

    given: "Morning session scheduler of 1 hours sessions"
      def morningSessionScheduler = MorningSessionSchedulerImpl
        .using(Duration.ofHours(1))
        .build()

    and: "Some valid scheduler instance using it"
      def scheduler = MorningSessionsSchedulerImpl
        .using(morningSessionScheduler)
        .requiredSessionsNumber(2)
        .build()

    when: "Scheduler is applied"
      scheduler.apply(nHourTalks(2))

    then: "Exception should be thrown"
      notThrown(IllegalArgumentException)
  }

  def """Exception should be thrown when it's not possible to create
      required number of sessions with given morning session scheduler"""() {

    given: """Morning sessions scheduler using morning
      session scheduler always returning empty result"""
      def scheduler = MorningSessionsSchedulerImpl
        .using(morningSessionScheduler)
        .requiredSessionsNumber(3)
        .build()

    when: "Scheduler is applied"
      scheduler.apply(nTalks(10))

    then: "Exception should be thrown"
      thrown(SchedulingException)

    where: "Morning session scheduler is can't return required number of sessions"
      morningSessionScheduler << [
        // first scenario: no results at all
        newFailingMorningSessionScheduler(),
        // second scenario:
        Mock(MorningSessionScheduler) {
          // one session for first 10 talks, 5 talks unused
          apply({ it.size() == 10 }) >> morningSessionSchedulerResult(session(1), nTalks(5))
          // no session for remaining batch of 5 talks
          apply({ it.size() == 5 }) >> Optional.empty()
        }
      ]
  }

  def """Result should be returned when it's possible to create required
      number of sessions with given morning session scheduler"""() {

    given: "Morning session scheduler returning two results"
      def morningSessionScheduler = Mock(MorningSessionScheduler) {
        // one session for first 10 talks, 5 talks unused
        apply({ it.size() == 10 }) >> morningSessionSchedulerResult(session(1), nTalks(5))
        // one session for remaining batch of 5 talks, 2 talks unused
        apply({ it.size() == 5 }) >> morningSessionSchedulerResult(session(2), nTalks(2))
      }

    and: "Morning sessions scheduler using it"
      def scheduler = MorningSessionsSchedulerImpl
        .using(morningSessionScheduler)
        .requiredSessionsNumber(2)
        .build()

    when: "Scheduler is applied"
      def result = scheduler.apply(nTalks(10))

    then: "Expected sessions should be returned"
      result.sessions() == set(session(1), session(2))

    and: "Number of unused talks should be expected"
      result.unusedTalks().size() == 2
  }

  /* utils */

  def newFailingMorningSessionScheduler() {
    Mock(MorningSessionScheduler) {
      apply(*_) >> Optional.empty()
    }
  }

  def nTalks(int n) {
    def talks = []
    for (int i = 0; i < n; i++) {
      talks.add(talk("Title ${i + 1}", 5 + i))
    }
    talks.toSet()
  }

  def session(int i) {
    def fiveMinutesOrMore = 5 + i
    def talk = talk("Title ${i}", fiveMinutesOrMore)
    MorningSession.of(singleton(talk))
  }

  def morningSessionSchedulerResult(MorningSession session, Set<Talk> unusedTalks) {
    new MorningSessionScheduler.Result(session, unusedTalks)
  }
}