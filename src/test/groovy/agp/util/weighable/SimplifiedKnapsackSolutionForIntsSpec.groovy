package agp.util.weighable

import spock.lang.Specification

import static java.util.Collections.singletonList

class SimplifiedKnapsackSolutionForIntsSpec extends Specification {

  final def solution = new SimplifiedKnapsackSolutionForInts()


  def "Solution should throw when provided input ints are invalid"() {

    when: "Solution is applied"
      solution.apply(input, 12)

    then: "Exception should be thrown"
      thrown(IllegalArgumentException)

    where: "Input ints combination is invalid"
      input << [
        combination(/* without weighable elements */),
        combination(0, 1),  // contains int <= 0
        combination(-1, 1)  // contains int <= 0
      ]
  }

  def "Solution should throw when provided goal is <= 0"() {

    given: "Some valid input ints combination"
      def input = combination(1, 2)

    when: "Solution is applied"
      solution.apply(input, invalidGoal)

    then: "Exception should be thrown"
      thrown(IllegalArgumentException)

    where: "Goal is <= 0"
      invalidGoal << [0, -1]
  }

  def "Solution should return no answers when [every int of input] > goal"() {

    when: "Solution is applied"
      def answers = solution.apply(input, 12)

    then: "Result should be empty"
      answers.isEmpty()

    where: "Input ints > goal"
      input << [
        combination(13),
        combination(13, 13)
      ]
  }

  def "Solution should return no answers when [every int of input] barely < goal"() {

    when: "Solution is applied"
      def answers = solution.apply(input, 12)

    then: "Result should be empty"
      answers.isEmpty()

    where: "Input ints are barely less then goal"
      input << [
        combination(11),
        combination(11, 11)
      ]
  }

  def "Solution should result in one answer for [input int] == goal"() {

    given: "Input int == goal"
      def input = combination(12)

    when: "Solution is applied"
      def answers = solution.apply(input, 12)

    then: "Result should be input combination singleton"
      answers == singletonList(combination(12))
  }

  def "Solution should result in two answers when [two ints of input] == goal"() {

    when: "Solution is applied"
      def answers = solution.apply(input, 12)

    then: "Result should consist of two answers"
      answers == [combination(12), combination(12)]

    where: "Input has two ints == goal"
      input << [
        combination(12, 12),
        combination(12, 12, 13),
        combination(12, 12, 9, 9)
      ]
  }

  def "Solution should result in one answer when [sum of several input ints] == goal"() {

    when: "Solution is applied"
      def answers = solution.apply(input, 12)

    then: "Result should be input combination singleton"
      answers == singletonList(input)

    where: "Sum of several input ints == goal"
      input << [
        combination(11, 1),
        combination(9, 2, 1),
        combination(8, 2, 1, 1)
      ]
  }

  def "Solution should result in expected answers for more complex input combination"() {

    given: "Input ints combination must produce many answers"
      def input = combination(1, 1, 2, 3, 3, 4, 8, 9)

    when: "Solution is applied"
      def answers = solution.apply(input, 12)

    then: "Result should be expected"
      answers == [
        // answers starting with 9
        combination(9, 3),
        combination(9, 3),
        combination(9, 2, 1),
        combination(9, 2, 1),
        // answers starting with 8
        combination(8, 4),
        combination(8, 3, 1),
        combination(8, 3, 1),
        combination(8, 3, 1),
        combination(8, 3, 1),
        combination(8, 2, 1, 1),
        // answers starting with 4
        combination(4, 3, 3, 2),
        combination(4, 3, 3, 1, 1)
      ]
  }

  /* utils */

  private static List<Integer> combination(Integer... ints) {
    Arrays.asList(ints)
  }
}