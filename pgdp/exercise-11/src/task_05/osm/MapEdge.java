package task_05.osm;

public class MapEdge {

    final Long to;
    final Way way;

    MapEdge(Long to, Way way) {
        this.to = to;
        this.way = way;
    }


    @Override
    public String toString() {
        return String.format("<osm.MapEdge to=%s way=%s>", to, way);
    }
}
