package agp.scheduler;

import java.util.Set;
import java.util.function.Function;

import agp.vo.ConferenceTrack;
import agp.vo.Talk;

public interface ConferenceAgendaScheduler extends Function<Set<Talk>, Set<ConferenceTrack>> {

}
