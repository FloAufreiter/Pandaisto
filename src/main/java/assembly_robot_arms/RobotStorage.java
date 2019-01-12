package assembly_robot_arms;

import shared.ItemType;

public class RobotStorage {
    public ItemType getType() {
        return type;
    }

    ItemType type;
    int maxNrOfElements;
    int minNrOfElements;

    public int getNrOfElements() {
        return nrOfElements;
    }

    int nrOfElements = 0;

    public RobotStorage(ItemType type, int maxNrOfElements, int minNrOfElements) {
        this.type = type;
        this.maxNrOfElements = maxNrOfElements;
        this.minNrOfElements = minNrOfElements;
    }

    public void removeElement() {
        if(type == ItemType.SCREW) {
            nrOfElements -= 4;
        } else {
            nrOfElements -= 1;
        }
    }

    public void addElement() {
        nrOfElements += type.getNrOfElements();
    }
}
