package warehouse;
import shared.ItemType;

/**
 * Class encapsulating information about a database event
 * TODO: So far this represents changes to items only, maybe this needs to be generalized
 * @author Tom Peham
 *
 */
class DBEvent {
	final ItemType itemType;
	final EventType eType;
	final int id;
	
	public DBEvent(EventType eType, ItemType itemType, int id) {
		this.itemType = itemType;
		this.eType = eType;
		this.id = id;
	}
}
