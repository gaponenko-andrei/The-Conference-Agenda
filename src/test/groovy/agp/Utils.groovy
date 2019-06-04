package agp

import agp.vo.Talk
import com.google.common.collect.ImmutableSet

import java.time.Duration

class Utils {

  static def talk(String title, long minutes) {
    new Talk(title, Duration.ofMinutes(minutes))
  }

  static <T> ImmutableSet<T> set(T... objects) {
    ImmutableSet.copyOf(objects.toList())
  }

  static def nHourTalks(int n) {
    def talks = []
    for (int i = 0; i < n; i++) {
      talks.add(talk("Title ${i + 1}", 60))
    }
    talks.toSet()
  }
}
