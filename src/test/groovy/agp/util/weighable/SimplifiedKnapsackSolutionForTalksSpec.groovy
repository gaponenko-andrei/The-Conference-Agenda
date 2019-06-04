package agp.util.weighable

import agp.vo.Talk
import com.google.common.collect.ImmutableSet
import spock.lang.Specification

import java.time.Duration
import java.util.function.Function

import static agp.util.weighable.KnapsackSolutionForTalks.Answer
import static java.util.Collections.singleton
import static java.util.Comparator.comparing

class SimplifiedKnapsackSolutionForTalksSpec extends Specification {

  final def solution = new SimplifiedKnapsackSolutionForTalks()


  def "Solution should throw when goal is <= 0"() {

    given: "Some valid combination of talks"
      def allTalks = combination(talk("Title 1", 30), talk("Title 2", 20))

    when: "Solution is applied"
      solution.apply(allTalks, nonPositiveGoal)

    then: "Exception should be thrown"
      thrown(IllegalArgumentException)

    where: "Goal is <= 0"
      nonPositiveGoal << [
        Duration.ofHours(0),
        Duration.ofHours(-1),
      ]
  }

  def "Solution should return no answers when every talk is longer then goal"() {

    given: "Goal duration"
      def goal = Duration.ofMinutes(30)

    when: "Solution is applied"
      def answers = solution.apply(allTalks, goal)

    then: "Result should be empty"
      answers.isEmpty()

    where: "Every talk in combination is longer then goal"
      allTalks << [
        combination(talk("Title 1", 31)),
        combination(talk("Title 1", 31), talk("Title 2", 31)),
      ]
  }

  def "Solution should return no answers when every talk is barely shorter then goal"() {

    given: "Goal duration"
      def goal = Duration.ofMinutes(30)

    when: "Solution is applied"
      def answers = solution.apply(allTalks, goal)

    then: "Result should be empty"
      answers.isEmpty()

    where: "Every talk of combination is barely shorter then goal"
      allTalks << [
        combination(talk("Title 1", 29)),
        combination(talk("Title 1", 29), talk("Title 2", 29)),
      ]
  }

  def "Solution should result in one answer for talk with goal duration"() {

    given: "Goal duration"
      def goal = Duration.ofMinutes(30)

    and: "Combination consists of one talk with goal duration"
      def allTalks = combination(talk("Title 1", 30))

    when: "Solution is applied"
      def answers = solution.apply(allTalks, goal)

    then: "Result should be one answer equal to only given talk"
      answers == singleton(combination(talk("Title 1", 30)))
  }

  def "Solution should result in two answers when two among all talks have goal duration"() {

    given: "Goal duration"
      def goal = Duration.ofMinutes(30)

    when: "Solution is applied"
      def answers = solution.apply(allTalks, goal)

    then: "Result should consist of two answers"
      answers == ImmutableSet.of(
        combination(talk("Title 1", 30)),
        combination(talk("Title 2", 30))
      )

    where: "Two among all talks have goal duration"
      allTalks << [
        combination(talk("Title 1", 30), talk("Title 2", 30)),
        combination(talk("Title 1", 30), talk("Title 2", 30), talk("Title 3", 25))
      ]
  }

  def "Solution should result in one answer when sum duration of several talks is the goal duration"() {

    given: "Goal duration"
      def goal = Duration.ofHours(1)

    and: "Sum duration of several input talks is goal duration"
      def allTalks = combination(talk("Title 1", 15), talk("Title 2", 15), talk("Title 3", 30))

    when: "Solution is applied"
      def answers = solution.apply(allTalks, goal)
      answers = sortAnswersCombinations(answers)

    then: "Result should be one given combination of talks"
      answers == singleton(
        combination(talk("Title 3", 30), talk("Title 1", 15), talk("Title 2", 15))
      )
  }

  def "Solution should result in expected answers when for more complex combination of tasks"() {

    given: "Goal duration"
      def goal = Duration.ofMinutes(50)

    and: "Combination of talks that must result in many answers"
      def allTalks = combination( // 10, 10, 20, 30, 50, 60
        talk("Title 1", 10), talk("Title 2", 10), talk("Title 3", 20),
        talk("Title 4", 30), talk("Title 5", 50), talk("Title 6", 60),
      )

    when: "Solution is applied"
      def answers = solution.apply(allTalks, goal)
      answers = sortAnswersCombinations(answers)

    then: "Answer should consist of several combinations"
      answers == ImmutableSet.of(
        combination(talk("Title 5", 50)),
        combination(talk("Title 4", 30), talk("Title 3", 20)),
        combination(talk("Title 4", 30), talk("Title 1", 10), talk("Title 2", 10)),
      )
  }

  /* utils */

  private static Talk talk(String title, long minutes) {
    new Talk(title, Duration.ofMinutes(minutes))
  }

  private static Set<Talk> combination(Talk... talks) {
    Arrays.asList(talks).toSet()
  }

  private static Set<Answer> sortAnswersCombinations(Set<Answer> answers) {
    answers.collect { new Answer(sortCombination(it)) }
  }

  private static Set<Talk> sortCombination(Set<Talk> talks) {
    talks.toSorted(
      comparing({ it.duration() } as Function<Talk, Duration>).reversed()
        .thenComparing({ it.title() } as Function<Talk, String>)
    )
  }
}