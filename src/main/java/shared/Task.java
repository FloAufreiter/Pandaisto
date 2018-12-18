package shared;

import AGV.Route;

import java.util.ArrayList;

public class Task {

    boolean done = false;
    long deadline; //deadline time in millis

    private Route route;
    private ArrayList<Location> locations;
}
