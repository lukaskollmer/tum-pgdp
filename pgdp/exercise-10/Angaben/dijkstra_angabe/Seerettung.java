package dijkstra_angabe;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

public class Seerettung {
  private static PriorityQueue<Eisscholle> nachbarschollen;

  public static List<Eisscholle> findeWeg
   (Eisscholle[] eisschollen,
    List<Seeweg> seewege,
    int startIndex,
    int endIndex)
  {
    nachbarschollen = new PriorityQueue<>(new EisschollenComparator());

    // TODO

    return null;
  }

  private static List<Seeweg> findSeewegeFromEisscholle(List<Seeweg> seewege, Eisscholle eisscholle) {
    return seewege.stream()
        .filter(o -> o.getFrom().equals(eisscholle))
        .collect(Collectors.toList());
  }
}
