package task_05.osm;

import java.util.List;

public class Route {

    public final MapNode start;
    public final MapNode end;

    public final List<MapNode> path;
    public final Float distance;

    Route(MapNode start, MapNode end, List<MapNode> path) {
        this.start = start;
        this.end = end;
        this.path = path;

        Float _distance = 0f;

        for (int i = 1; i < path.size() - 1; i++) {
            _distance += path.get(i - 1).location.distanceTo(path.get(i).location);
        }

        this.distance = _distance;
    }

}
