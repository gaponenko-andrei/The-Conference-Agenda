package agp.scheduler

import agp.vo.ConferenceTrack
import agp.vo.ConferenceTrack.ScheduledEvent
import agp.vo.Lunch
import agp.vo.NetworkingEvent
import agp.vo.Talk
import spock.lang.Specification

import java.time.LocalTime

import static agp.Utils.*

class ConferenceAgendaSchedulerImplSpec extends Specification {

  def "Exception should be thrown when overall duration of talks is <= 3 hours"() {

    given: "Some valid scheduler instance"
      def scheduler = new ConferenceAgendaSchedulerImpl()

    when: "Scheduler is applied"
      scheduler.apply(set(
        talk("Title 1", 60), talk("Title 2", 60),
        talk("Title 3", 30), talk("Title 4", 30),
      ))

    then: "Exception should be thrown"
      thrown(IllegalArgumentException)
  }

  def "Exception should not be thrown when overall duration of talks is more than 3 hours"() {

    given: "Some valid scheduler instance"
      def scheduler = new ConferenceAgendaSchedulerImpl()

    when: "Scheduler is applied"
      scheduler.apply(set(
        talk("Title 1", 60), talk("Title 2", 60),
        talk("Title 3", 60), talk("Title 4", 60),
      ))

    then: "Exception should not be thrown"
      notThrown(IllegalArgumentException)
  }

  def "Exception should be thrown when it's impossible to create agenda for given talks"() {

    given: "Some valid scheduler instance"
      def scheduler = new ConferenceAgendaSchedulerImpl()

    when: "Scheduler is applied"
      scheduler.apply(set(
        talk("Title 1", 51), talk("Title 2", 52), talk("Title 3", 53),
        talk("Title 4", 54), talk("Title 5", 55), talk("Title 6", 56)
      ))

    then: "Exception should be thrown"
      thrown(SchedulingException)
  }

  def "Number of tracks should be based on overall duration of talks"() {

    given: "Some valid scheduler instance"
      def scheduler = new ConferenceAgendaSchedulerImpl()

    when: "Scheduler is applied"
      Set<ConferenceTrack> tracks = scheduler.apply(allTalks)

    then: "Number of tracks should be expected"
      tracks.size() == expectedTracksNumber

    where: "Test cases are shown as table"
      /* - - - - - - - - - - - - - - - - - */
      allTalks       | expectedTracksNumber
      /* - - - - - - - - - - - - - - - - - */
      nHourTalks(4)  | 1
      nHourTalks(7)  | 1
      nHourTalks(8)  | 2
      nHourTalks(15) | 3
  }

  def "No talks should be left unused"() {

    given: "Some valid scheduler instance"
      def scheduler = new ConferenceAgendaSchedulerImpl()

    and: "Talks for one track"
      def talks = set(
        talk("Title 1", 29), talk("Title 2", 31),
        talk("Title 3", 29), talk("Title 4", 31),
        talk("Title 5", 9), talk("Title 6", 51),
        talk("Title 7", 9), talk("Title 8", 51)
      )

    when: "Scheduler is applied"
      Set<ConferenceTrack> tracks = scheduler.apply(talks)

    then: "One track should be created"
      tracks.size() == 1

    and: "All talks should be used"
      tracks.first().talks() == talks
  }

  def "Each track should have exactly one lunch scheduled for 12:00PM - 1:00PM"() {

    given: "Some valid scheduler instance"
      def scheduler = new ConferenceAgendaSchedulerImpl()

    when: "Scheduler is applied"
      Set<ConferenceTrack> tracks = scheduler.apply(nHourTalks(15))

    then: "Each track should have one lunch "
      tracks.each { hasExactlyOneLunch(it) }
  }

  def "Each track should have networking event scheduled for 5:00 PM or earlier"() {

    given: "Some valid scheduler instance"
      def scheduler = new ConferenceAgendaSchedulerImpl()

    when: "Scheduler is applied"
      Set<ConferenceTrack> tracks = scheduler.apply(nHourTalks(15))

    then: "Each track should have networking event"
      tracks.each { hasExactlyOneNetworkingEvent(it) }
  }

  def "Each track should have at least three talks for morning session & one for afternoon session"() {

    given: "Some valid scheduler instance"
      def scheduler = new ConferenceAgendaSchedulerImpl()

    when: "Scheduler is applied"
      Set<ConferenceTrack> tracks = scheduler.apply(nHourTalks(15))

    then: "Each track should have networking event"
      tracks.each {
        assert hasMinimumThreeMorningTalks(it)
        assert hasMimimumOneAfternoonTalk(it)
      }
  }

  /* utils */

  def hasExactlyOneLunch(ConferenceTrack track) {
    track.hasExactlyOne { isLunch(it) }
  }

  def isLunch(ScheduledEvent event) {
    event.delegate() instanceof Lunch &&
      event.startTime() == LocalTime.of(12, 0) &&
      event.endTime() == LocalTime.of(13, 0)
  }

  def hasExactlyOneNetworkingEvent(ConferenceTrack track) {
    track.hasExactlyOne { isNetworking(it) }
  }

  def isNetworking(ScheduledEvent event) {
    event.delegate() instanceof NetworkingEvent &&
      event.startTime().isBefore(LocalTime.of(17, 1))
  }

  def hasMinimumThreeMorningTalks(ConferenceTrack track) {
    track.count { isMorningSessionTalk(it) } >= 3
  }

  def isMorningSessionTalk(ScheduledEvent event) {
    event.delegate() instanceof Talk &&
      event.startTime().isAfter(LocalTime.of(8, 59)) &&
      event.endTime().isBefore(LocalTime.of(12, 1))
  }

  def hasMimimumOneAfternoonTalk(ConferenceTrack track) {
    track.count { isAfternoonSessionTalk(it) } >= 1
  }

  def isAfternoonSessionTalk(ScheduledEvent event) {
    event.delegate() instanceof Talk &&
      event.startTime().isAfter(LocalTime.of(13, 0)) &&
      event.endTime().isBefore(LocalTime.of(17, 1))
  }
}