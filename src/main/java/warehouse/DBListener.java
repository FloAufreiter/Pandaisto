package warehouse;

/**
 * Listener interface classes that want to be notified of db changes need to implement
 * @author Tom Peham
 *
 */
interface DBListener {
	void notifyEvent(DBEvent e);
}
