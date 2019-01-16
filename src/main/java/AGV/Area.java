package AGV;

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


/**
 * Defines the Area of the factory as a graph
 * Edges define the paths on which forklifts can run
 * Used by other subsystems to get Location objects needed to create tasks for the fork lifts
 * Uses Dijkstra Algorithm to compute the minimal cost distance between two locations
 */
public class Area {
    private static HipsterGraph<Location, Double> GRAPH;

    private static Map<Integer, Location> ROBOT_ARMS = new HashMap<>();
    private static Map<Integer, Location> SHELVES = new HashMap<>();
    private static Map<Integer, Location> DOCKS = new HashMap<>();

    private static Area instance = new Area();

    private Area() {
        buildGraph();
    }

    public static Area getInstance() {
        return instance;
    }

    public Location getLocation(Location.LocationType type, int id) {
        switch (type) {
            case FLOOR_SHELF:
            case TOP_SHELF1:
            case TOP_SHELF2:
                return SHELVES.get(id);
            case PRODUCTION_LINE:
                return ROBOT_ARMS.get(id);
            case LOADING_DOCK:
                return DOCKS.get(id);
            default:
                return null;
        }

    }

    public Location getRandomLocation() {
        Object[] locations = Stream.concat(DOCKS.values().stream(), Stream.concat(ROBOT_ARMS.values().stream(), SHELVES.values().stream())).toArray();
        Random random = new Random();
        return (Location) locations[random.nextInt(locations.length)];
    }

    /**
     * builds the graph, representing the area of the factory
     * should later be build by using a Spec-File to be able to Start the system with different graphs (factories)
     */
    private static void buildGraph() {
        GraphBuilder<Location, Double> gb = GraphBuilder.create();

        //shelves
        Location prev = null;
        for (int i = 0; i < 60; i++) {
            //floor shelves
            Location fs = new Location(Location.LocationType.FLOOR_SHELF, i);
            if (prev != null) {
                gb.connect(prev).to(fs).withEdge(1d);
            }
            SHELVES.put(i, fs);
            prev = fs;
            //first floor shelves
            int ts1_id = ++i;
            Location ts1 = new Location(Location.LocationType.TOP_SHELF1, ts1_id);
            gb.connect(fs).to(ts1).withEdge(0d);
            SHELVES.put(ts1_id, ts1);
            //second floor shelves
            int ts2_id = ++i;
            Location ts2 = new Location(Location.LocationType.TOP_SHELF2, ts2_id);
            gb.connect(ts1).to(ts2).withEdge(0d);
            SHELVES.put(ts2_id, ts2);
        }

        //robot arms
        prev = null;
        for (int i = 0; i < 10; i++) {
            Location ra = new Location(Location.LocationType.PRODUCTION_LINE, i);
            if (prev != null) {
                gb.connect(prev).to(ra).withEdge(1d);
            }
            ROBOT_ARMS.put(i, ra);
            prev = ra;
        }

        gb.connect(ROBOT_ARMS.get(0)).to(SHELVES.get(0)).withEdge(7d);
        gb.connect(ROBOT_ARMS.get(5)).to(SHELVES.get(0)).withEdge(6d);
        gb.connect(ROBOT_ARMS.get(4)).to(SHELVES.get(0)).withEdge(7d);
        gb.connect(ROBOT_ARMS.get(9)).to(SHELVES.get(0)).withEdge(6d);

        //loading docks
        double dist = 3;
        for (int i = 0; i <= 3; i++) {
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

    /**
     *
     * @param start
     * @param dest
     * @return the minimal cost between two locations
     */
    Double getMinimalCostFrom(Location start, Location dest) {
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
