package task_05.osm;

import java.util.*;

public class Way extends Element {

    // the ids of all nodes that are part of the way
    final List<Long> nodeIds = new ArrayList<>();


    Way(Long id) {
        super(id);
    }

    void addNodeId(Long nodeId) {
        this.nodeIds.add(nodeId);
    }


    boolean isOneWay() {
        return hasTagWithKeyAndValue("oneway", "yes");
    }

    boolean isHighway() {
        return hasTagWithKey("highway");
    }


    @Override
    public String toString() {
        return String.format("<osm.Way id=%s nodeIds=%s tags=%s>", id, nodeIds, toString_tags());
    }
}
