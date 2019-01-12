package AGV;

import es.usc.citius.hipster.algorithm.AStar;
import es.usc.citius.hipster.algorithm.Hipster;
import es.usc.citius.hipster.graph.GraphBuilder;
import es.usc.citius.hipster.graph.GraphSearchProblem;
import es.usc.citius.hipster.graph.HipsterGraph;
import es.usc.citius.hipster.model.impl.WeightedNode;
import es.usc.citius.hipster.model.problem.SearchProblem;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;

public class Area {
    private static HipsterGraph<Location, Double> GRAPH;

    private static Map<Integer, Location> ROBOT_ARMS = new HashMap<>();
    private static Map<Integer, Location> SHELVES = new HashMap<>();
    private static Map<Integer, Location> DOCKS = new HashMap<>();

    Area() {
        buildGraph();
    }

    public static Location getLocation(Location.LocationType type, int id) {
        switch (type) {
            case FLOORSHELF:
                return SHELVES.get(id);
            case PRODUCTION_LINE:
                return ROBOT_ARMS.get(id);
            case LOADING_DOCK:
                return DOCKS.get(id);
            default:
                return null;
        }

    }

    public static Location getRandomLocation() {
        Object[] locations = Stream.concat(DOCKS.values().stream(), Stream.concat(ROBOT_ARMS.values().stream(), SHELVES.values().stream())).toArray();
        Random random = new Random();
        return (Location) locations[random.nextInt(locations.length)];
    }

    private static void buildGraph() { //should later be done by Spec-File
        GraphBuilder<Location, Double> gb = GraphBuilder.create();

        //shelves
        Location prev = null;
        for (int i = 0; i < 60; i++) {
            //floor shelves
            Location fs = new Location(Location.LocationType.FLOORSHELF, i);
            if (prev != null) {
                gb.connect(prev).to(fs).withEdge(1d);
            }
            SHELVES.put(i, fs);
            prev = fs;
            //first floor shelves
            int ts1_id = ++i;
            Location ts1 = new Location(Location.LocationType.TOPSHELF1, ts1_id);
            gb.connect(fs).to(ts1).withEdge(0d);
            SHELVES.put(ts1_id, ts1);
            //second floor shelves
            int ts2_id = ++i;
            Location ts2 = new Location(Location.LocationType.TOPSHELF2, ts2_id);
            gb.connect(ts1).to(ts2).withEdge(0d);
            SHELVES.put(ts2_id, ts2);
        }

        //robot arms
        prev = null;
        double dist = 5d;
        for (int i = 0; i <= 7; i++) {
            Location ra = new Location(Location.LocationType.PRODUCTION_LINE, i);
            if (prev != null) {
                gb.connect(prev).to(ra).withEdge(1d);
            }
            ROBOT_ARMS.put(i, ra);
            prev = ra;
            gb.connect(ra).to(SHELVES.get(0)).withEdge(dist++);
        }

        //loading docks
        dist = 3;
        for (int i = 0; i <= 7; i++) {
            Location ld = new Location(Location.LocationType.LOADING_DOCK, i);
            DOCKS.put(i, ld);
            gb.connect(ld).to(SHELVES.get(57)).withEdge(dist++);
        }
        GRAPH = gb.createUndirectedGraph();

        for (Location l : GRAPH.vertices()) {
            SearchProblem p = GraphSearchProblem
                    .startingFrom(l)
                    .in(GRAPH)
                    .takeCostsFromEdges()
                    .build();
        }
    }

    static Double getMinimalCostFrom(Location start, Location dest) {
        SearchProblem p = GraphSearchProblem
                .startingFrom(start)
                .goalAt(dest)
                .in(GRAPH)
                .takeCostsFromEdges()
                .build();
        Hipster.createDijkstra(p);
        WeightedNode node = (WeightedNode) Hipster.createDijkstra(p).search(dest).getGoalNode();
        return (Double) node.getCost();
    }
}