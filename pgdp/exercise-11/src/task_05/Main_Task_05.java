package task_05;

import org.xml.sax.SAXException;
import task_05.osm.*;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main_Task_05 {
    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {

        double ts1 = System.nanoTime();

        Map map = MapParser.parse(System.getProperty("user.dir") + "/campus_garching.osm");
        //Map map = MapParser.parse(System.getProperty("user.dir") + "/oberbayern.osm");


        Route r1 = map.route(new Location(48.2690197, 11.6751468), new Location(48.2638814, 11.6661943));
        Route r2 = map.route(new Location(48.2690197, 11.6751468), new Location(48.003833, 11.317972)); //<55km
        Route r3 = map.route(new Location(48.2690197, 11.6751468), new Location(48.098, 11.508833));
        Route r4 = map.route(new Location(47.862916, 11.0275), new Location(48.349388, 11.768416)); //looong


        int index = 1;
        for (Route route : Stream.of(r1, r2, r3, r4).filter(Objects::nonNull).collect(Collectors.toList())) {
            System.out.println("Distance: " + route.distance);
            System.out.println("Writing GPX track to route.gpx...");
            GPXWriter gw = new GPXWriter("route_" + index + ".gpx");
            gw.writeRoute(route);
            index++;
        }


        double ts2 = System.nanoTime();
        double duration = ((ts2 - ts1) / (double)1000000000);

        System.out.format("\n\nduration: %s\n", duration);
    }
}
