package task_06;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

public class Seerettung {
    private static PriorityQueue<Eisscholle> neighbors = new PriorityQueue<>(new EisschollenComparator());

    public static List<Eisscholle> findeWeg(Eisscholle[] eisschollen, List<Seeweg> seewege, int startIndex, int endIndex) {

        // 1. we set the distance to the first floe to 0 (since that's our starting point)
        //    and the distance to all other floes to MAX_INT (indicating that we have yet to process them)
        for (int i = 0; i < eisschollen.length; i++) {
            eisschollen[i].setDistance(i == startIndex ? 0 : Integer.MAX_VALUE);
        }

        neighbors.add(eisschollen[startIndex]);



        while (!neighbors.isEmpty()) {
            Eisscholle floe = neighbors.poll();

            if (floe == eisschollen[endIndex]) {
                //reached the end
                break;
            }

            // get all neighbors and calculate the distance to each of them
            for (Seeweg way : findSeewegeFromEisscholle(seewege, floe)) {


                int newDistance = floe.getDistance() + way.getDistance();

                if (newDistance < way.getTo().getDistance()) {
                    way.getTo().setPredecessor(floe);
                    way.getTo().setDistance(newDistance);
                }
            }

            // add all new neighbors
            neighbors.addAll(findSeewegeFromEisscholle(seewege, floe)
                    .stream()
                    .map(Seeweg::getTo)
                    .filter(f -> !neighbors.contains(f))
                    .collect(Collectors.toList())
            );
        }


        // reconstruct the path

        List<Eisscholle> path = new ArrayList<>();

        Eisscholle tmp = eisschollen[endIndex];

        while (tmp != null) {
            path.add(tmp);
            tmp = tmp.getPredecessor();
        }

        Collections.reverse(path);

        return path;
    }

    private static List<Seeweg> findSeewegeFromEisscholle(List<Seeweg> seewege, Eisscholle eisscholle) {
        return seewege.stream()
                .filter(o -> o.getFrom().equals(eisscholle))
                .collect(Collectors.toList());
    }
}
