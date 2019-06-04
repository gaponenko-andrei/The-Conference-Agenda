package agp.scheduler

import agp.util.weighable.KnapsackSolutionForTalks
import agp.util.weighable.SimplifiedKnapsackSolutionForTalks
import agp.vo.Talk
import spock.lang.Shared
import spock.lang.Specification

import java.time.Duration

import static agp.Utils.set
import static agp.Utils.talk
import static agp.util.weighable.KnapsackSolutionForTalks.Answer
import static java.util.Collections.emptySet
import static java.util.Collections.singleton

class MorningSessionSchedulerImplSpec extends Specification {

  @Shared
  def someDuration = Duration.ofMinutes(10)


  def "Default knapsack solution for scheduler should be SimplifiedKnapsackSolutionForTalks"() {

    expect: "SimplifiedKnapsackSolutionForTalks as default knapsack solution"
      scheduler.knapsackSolution() instanceof SimplifiedKnapsackSolutionForTalks

    where: "Scheduler has default knapsack solution"
      scheduler << [
        MorningSessionSchedulerImpl.using(someDuration).build(),
        MorningSessionSchedulerImpl.builder().sessionGoalDuration(someDuration).build()
      ]
  }

  def "Exception should be thrown on empty set of talks"() {

    given: "Some valid scheduler instance"
      def scheduler = MorningSessionSchedulerImpl.using(someDuration).build()

    when: "Scheduler is applied to empty set of talks"
      scheduler.apply(emptySet())

    then: "Exception should be thrown"
      thrown(IllegalArgumentException)
  }

  def "Exception should be thrown when it's not possible to create MorningSession with goal duration"() {

    given: "Scheduler using knapsack solution always returning zero answers"
      def scheduler = MorningSessionSchedulerImpl
        .using(newFailingKnapsackSolution())
        .sessionGoalDuration(someDuration)
        .build()

    and: "Some non-empty set of talks"
      def talks = Mock(Set) { isEmpty() >> false }

    when: "Scheduler is applied"
      scheduler.apply(talks)

    then: "Exception should be thrown"
      thrown(SchedulingException)
  }

  def "MorningSession must be created based on first answer given by knapsack solution"() {

    given: "Some random talks"
      def talks = set(talk("Title 1", 30), talk("Title 2", 30))

    and: "Knapsack solution that returns each given talk as possible answer"
      def knapsackSolutionGivingTwoAnswers = newKnapsackSolutionReturning(
        answer(talk("Title 2", 30)), // first answer
        answer(talk("Title 1", 30))  // second answer
      )

    and: "Scheduler using this knapsack solution"
      def scheduler = MorningSessionSchedulerImpl
        .using(knapsackSolutionGivingTwoAnswers)
        .sessionGoalDuration(someDuration)
        .build()

    when: "Scheduler is applied to singleton combination of this talk"
      def result = scheduler.apply(talks)

    then: "MorningSession should consist of first answer"
      result.session().isConsistsOf(talk("Title 2", 30))
  }

  def "Talks unused for creation of MorningSession must be returned"() {

    given: "Some random talks"
      def talks = set(talk("Title 1", 30), talk("Title 2", 30), talk("Title 3", 30))

    and: "Knapsack solution returning first two talks as answer"
      def knapsackSolutionGivingOneAnswer = newKnapsackSolutionReturning(
        answer(talk("Title 1", 30), talk("Title 2", 30))
      )

    and: "Scheduler using this knapsack solution"
      def scheduler = MorningSessionSchedulerImpl
        .using(knapsackSolutionGivingOneAnswer)
        .sessionGoalDuration(someDuration)
        .build()

    when: "Scheduler is applied to singleton set of this talk"
      def result = scheduler.apply(talks)

    then: "Third talk should be returned as the only unused one"
      result.unusedTalks() == singleton(talk("Title 3", 30))
  }

  /* utils */

  def newFailingKnapsackSolution() {
    Mock(KnapsackSolutionForTalks) {
      apply(*_) >> emptySet()
    }
  }

  def newKnapsackSolutionReturning(Answer... answers) {
    Mock(KnapsackSolutionForTalks) {
      apply(*_) >> answers.toList().toSet()
    }
  }

  def answer(Talk... talks) {
    new Answer(talks.toList().toSet())
  }
}