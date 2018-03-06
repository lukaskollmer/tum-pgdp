package task_05.osm;


import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Map {

    final java.util.Map<Long, MapNode> nodes = new HashMap<>();
    final java.util.Map<Long, Set<MapEdge>> edges = new HashMap<>();


    Map(List<Element> elements) {

        for (Element element : elements) {
            if (element instanceof MapNode) {
                this.nodes.put(element.id, (MapNode) element);

            } else if (element instanceof Way) {
                Way element_way = (Way)element;

                int size = element_way.nodeIds.size();

                for (int i = 0; i < size; i++) {
                    Long nodeId = element_way.nodeIds.get(i);

                    // add a new `MapEdge` to the edges map, creating a new set if necessary
                    Consumer<Integer> addEdge = offset -> {
                        if (!edges.containsKey(nodeId)) {
                            edges.put(nodeId, new HashSet<>());
                        }
                        edges.get(nodeId).add(new MapEdge(element_way.nodeIds.get(offset), element_way));
                    };

                    // Look ahead and behind, check if there are edges and add them if necessary
                    boolean hasPrev = i > 0;
                    boolean hasNext = i < size - 1;

                    if (hasPrev) addEdge.accept(i - 1);
                    if (hasNext) addEdge.accept(i + 1);
                }
            }
        }



        // Optimization

        Set<Long> nodesToBeDeleted = new HashSet<>();

        for (Element element : elements) {
            if (element instanceof Way) {
                Way element_way = (Way)element;

                if (!element_way.isHighway()) {
                    nodesToBeDeleted.addAll(element_way.nodeIds);
                } else {
                    nodesToBeDeleted.removeAll(element_way.nodeIds);
                }
            }
        }

        System.out.format("stats:\n");
        System.out.format("#nodes: %s\n", nodes.size());
        System.out.format("#edges: %s\n", edges.size());

        // todo disable this if it somehow causes problems
        nodesToBeDeleted.forEach(nodes::remove);
        nodesToBeDeleted.forEach(edges::remove);

        System.out.format("stats:\n");
        System.out.format("#nodes: %s\n", nodes.size());
        System.out.format("#edges: %s\n", edges.size());
    }


    // get the node on the map closest to the location you passed
    public MapNode closestTo(Location location) {

        List<MapNode> nodes_sorted = this.nodes.values()
                .stream()
                .sorted(Comparator.comparingInt(node -> (int) node.location.distanceTo(location)))
                .collect(Collectors.toList());

        //for (MapNode node : nodes_sorted) {
        //    System.out.format("%s\n", node.location.distanceTo(location));
        //}

        // todo check if the first couple of elements have the same distance, return by lowest id if necessary
        return nodes_sorted.get(0);
    }


    private boolean hasEdgeBetween(MapNode nodeA, MapNode nodeB) {
        // todo test this eventually (maybe)

        for (Long id : edges.keySet()) {
            System.out.format("%s\n", edges.get(id));
        }

        return false;
    }


    public Route route(Location from, Location to) {
        MapNode start = closestTo(from);
        MapNode end   = closestTo(to);

        System.out.format("\n\n\n=======\nwill calculate route between:\n%s\nand\n%s\n\n", start, end);









        //if (true) return null;



        AtomicReference<Location> lastLocation = new AtomicReference<>(start.location);


        Comparator<MapNode> comparator = (node0, node1) -> {
            float d1 = node0.location.distanceTo(end.location); // lastLocation?
            float d2 = node1.location.distanceTo(end.location); // ^
            return Float.compare(d1, d2);
        };

        Set<Long> visited = new HashSet<>();

        PriorityQueue<MapNode> queue = new PriorityQueue<>(comparator);
        queue.add(start);



        while (!queue.isEmpty()) {
            MapNode node = queue.poll();
            visited.add(node.id);

            if (node.equals(end)) {
                return new Route(start, end, node.reconstructPath());
            }



            List<MapNode> neighbors = this.edges.get(node.id)
                    .stream()
                    .map(edge -> edge.to)
                    .map(nodes::get)
                    .filter(Objects::nonNull)
                    .filter(_node -> !queue.contains(_node))
                    .filter(_node -> !visited.contains(_node.id))
                    .sorted((node0, node1) -> {
                        float d0 = node0.location.distanceTo(end.location);
                        float d1 = node1.location.distanceTo(end.location);
                        return Float.compare(d0, d1);
                    })
                    .collect(Collectors.toList());


            for (MapNode neighborNode : neighbors) {
                Float newDistance = node.location.distanceTo(end.location) + node.location.distanceTo(neighborNode.location);

                if (newDistance > neighborNode._distance) {
                    neighborNode._distance = newDistance;
                }

                neighborNode.parent = node;
            }

            queue.addAll(neighbors);
        }
        return null;
    }
}
