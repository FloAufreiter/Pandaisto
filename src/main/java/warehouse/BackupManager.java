package warehouse;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import org.apache.commons.io.FileUtils;

/**
 * Class for handling automatic backups
 * @author Tom Peham
 *
 */
public class BackupManager extends Thread {

	private int backupInterval; //interval of backups in days
	private boolean interrupted;
	File backup;
	File DB;
	
	/**
	 * Public constructor
	 */
	BackupManager(int backupInterval, String srcPath, String destPath) {
		this.backupInterval = backupInterval;
		interrupted = false;
		this.backup = new File(destPath);
		this.backup.mkdir();
		this.DB = new File(srcPath);
	}
	
	public BackupManager(int backupInterval) {
		this(backupInterval, System.getProperty("user.dir") + "/warehouseDB", System.getProperty("user.dir")+"/DBBACKUP");
	}
	
	@Override
	public void run() {
		try {
			try { //first time this is opened make backup of db
				copyDB();
			} catch(IOException e) {
				e.printStackTrace();
			}
			
			while(! interrupted) {
			Thread.sleep(backupInterval * 24 * 60 * 60 * 100); //suspend thread until next backup
			
			try {
				copyDB();
			} catch(IOException e) {
				e.printStackTrace();
			}
			
			}
		} catch(InterruptedException e) {
			interrupted = true;
		}
		
	}
	
	/**
	 * Simple copy of all contents of DB
	 * @throws IOException
	 */
	private void copyDB() throws IOException {
		Database.warehousePrint("CREATING BACKUP");
		FileUtils.copyDirectory(DB, backup);

		Database.warehousePrint("BACKUP DONE");
	}
}
