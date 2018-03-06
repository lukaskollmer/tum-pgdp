package task_05.osm;


import java.util.ArrayList;
import java.util.List;

public class MapNode extends Element {

    public final Location location;

    public MapNode parent = null;


    public Float _distance = Float.MAX_VALUE;

    MapNode(Long id, Double latitude, Double longitude) {
        super(id);

        this.location = new Location(latitude, longitude);
    }


    public boolean equals(MapNode otherNode) {
        return this == otherNode || this.id.equals(otherNode.id) && this.location.equals(otherNode.location);
    }


    List<MapNode> reconstructPath() {
        List<MapNode> path = new ArrayList<>();

        MapNode currentStep = this;

        do {
            path.add(currentStep);
            currentStep = currentStep.parent;
        } while (currentStep != null);


        return path;
    }


    @Override
    public String toString() {
        return String.format("<osm.MapNode id=%s lat=%s lon=%s tags=%s", id, location.latitude, location.longitude, toString_tags());
    }
}
