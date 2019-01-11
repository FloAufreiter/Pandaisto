package AGV;

import es.usc.citius.hipster.graph.GraphBuilder;
import es.usc.citius.hipster.graph.GraphSearchProblem;
import es.usc.citius.hipster.graph.HipsterGraph;

import java.util.HashMap;
import java.util.Map;

public class Area {

    private static final Area instance = new Area();
    private static HipsterGraph<Location, Double> GRAPH;

    private static Map<Integer, Location> ROBOT_ARMS = new HashMap<>();
    private static Map<Integer, Location> SHELVES = new HashMap<>();
    private static Map<Integer, Location> DOCKS = new HashMap<>();

    private Area() {
        createArea();
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

    static void createArea() { //should later be done by Spec-File
        GraphBuilder<Location, Double> gb = GraphBuilder.create();

        //shelves
        Location prev = null;
        for (int i = 0; i < 20; i++) {
            //floor shelves
            Location fs = new Location(Location.LocationType.FLOORSHELF, i);
            if (prev != null) {
                gb.connect(prev).to(fs).withEdge(1d);
            }
            SHELVES.put(i, fs);
            prev = fs;
            //first floor shelves
            int ts1_id = i + 20;
            Location ts1 = new Location(Location.LocationType.TOPSHELF1, ts1_id);
            gb.connect(fs).to(ts1).withEdge(0d);
            SHELVES.put(ts1_id, ts1);
            //second floor shelves
            int ts2_id = i + 40;
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
            gb.connect(ld).to(SHELVES.get(20)).withEdge(dist++);
        }
        gb.connect(ROBOT_ARMS.get(0)).to(SHELVES.get(20)).withEdge(8d);

        GRAPH = gb.createUndirectedGraph();
    }

    static Double getMinimalCostFrom(Location start, Location dest) {
        return GraphSearchProblem
                .startingFrom(start)
                .in(GRAPH)
                .takeCostsFromEdges()
                .build().getFinalNode().getCost();
    }
}
