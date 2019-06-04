package agp

import agp.util.TestResources
import agp.vo.Talk
import spock.lang.Specification

import java.nio.file.Paths

import static agp.Utils.set
import static agp.Utils.talk

class InputFileParserSpec extends Specification {

  final def parser = new InputFileParser()


  def "Exception should be thrown for non-existing file"() {

    given: "Some invalid path"
      def invalidPath = Paths.get("./some-file.txt")

    when: "Parser is applied"
      parser.apply(invalidPath)

    then: "Exception should be thrown"
      thrown(IllegalArgumentException)
  }

  def "Talks should be parsed correctly"() {

    given: "Some invalid path"
      def invalidPath = TestResources.get("SampleInput.txt")

    when: "Parser is applied"
      Set<Talk> talks = parser.apply(invalidPath)

    then: "Result talks should be expected"
      talks == set(
        talk("Writing Fast Tests Using Selenium", 60),
        talk("Overdoing it in Java", 45),
        talk("AngularJS for the Masses", 30),
        talk("Ruby Errors from Mismatched Gem Versions", 45),
        talk("Common Hibernate Errors", 45),
        talk("Rails for Java Developers", 5),
        talk("Face-to-Face Communication", 60),
        talk("Domain-Driven Development", 45),
        talk("What's New With Java 11", 30),
        talk("A Perfect Sprint Planning", 30),
        talk("Pair Programming vs Noise", 45),
        talk("Java Is Not Magic", 60),
        talk("Ruby on Rails: Why We Should Move On", 60),
        talk("Clojure Ate Scala (on my project)", 45),
        talk("Programming in the Boondocks of Seattle", 30),
        talk("Ant vs. Maven vs. Gradle Build Tool for Back-End Development", 30),
        talk("Java Legacy App Maintenance", 60),
        talk("A World Without Clinical Trials", 30),
        talk("User Interface CSS in AngularJS Apps", 30)
      )
  }
}