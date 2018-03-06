package task_05.osm;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GPXWriter {

    private final StringBuilder stringBuilder = new StringBuilder();
    private final Path filepath;


    public GPXWriter(String filename) {
        this.filepath = Paths.get(System.getProperty("user.dir"), filename);

        stringBuilder
                .append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?>\n")
                .append("<gpx version=\"1.1\" creator=\"me\">\n")
                .append("  <metadata></metadata>\n");
    }

    public void writeRoute(Route route) {
        addWaypoint(route.start);
        addWaypoint(route.end);

        beginRoute();
        route.path.forEach(this::addRouteWaypoint);
        endRoute();
        endFile();

        List<String> lines = Arrays.asList(stringBuilder.toString().split("\n"));
        try {
            Files.write(filepath, lines, Charset.forName("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addNode(String classname, MapNode node, int indentation) {
        stringBuilder
                .append(String.format(
                        "%s<%s lat=\"%s\" lon=\"%s\"/>\n",
                        String.join("", Collections.nCopies(indentation, " ")),
                        classname,
                        node.location.latitude,
                        node.location.longitude//,
                        //classname
                ));
    }


    private void addWaypoint(MapNode node) {
        addNode("wpt", node, 2);
    }


    private void addRouteWaypoint(MapNode node) {
        addNode("rtept", node, 4);
    }


    void beginRoute() {
        stringBuilder
                .append("  <!-- BEGIN ROUTE -->\n")
                .append("  <rte>\n");

    }

    void endRoute() {
        stringBuilder
                .append("  </rte>\n")
                .append("  <!-- END ROUTE -->\n");

    }

    void endFile() {
        stringBuilder.append("</gpx>");
    }
}
