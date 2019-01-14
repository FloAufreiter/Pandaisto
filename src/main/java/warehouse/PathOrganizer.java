package warehouse;

import shared.ItemType;

import java.sql.SQLException;
import java.util.HashMap;

/**
 * Class handling efficient storage of goods used in production line
 *
 * @author Tom Peham
 */
public class PathOrganizer extends Organizer implements DBListener {

    private final HashMap<ItemType, Integer> numItemRemovalMap = new HashMap<>(); //map storing how often a particular item has been accessed
    private final HashMap<ItemType, Integer> itemAvgPathLength = new HashMap<>(); //map storing average path length of items
    int numItemAccessess;

    PathOrganizer() {
        for (ItemType i : ItemType.values()) {
            numItemRemovalMap.put(i, 0);
            itemAvgPathLength.put(i, 0);
        }
    }

    @Override
    public void requestReording() {
        // TODO
    }

    @Override
    public void calcEfficientItemDistribution() {

    }

    @Override
    public void notifyEvent(DBEvent e) {
        if (e.eType.equals(EventType.ItemRemoved)) {
            if (numItemRemovalMap.containsKey(e.itemType)) {
                int n = numItemRemovalMap.get(e.itemType);
                numItemRemovalMap.put(e.itemType, n + 1);
            } else {
                numItemRemovalMap.put(e.itemType, 1);
            }
        }

        if (e.eType.equals(EventType.ItemAdded)) {

        }

    }
}
