package warehouse;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import org.apache.commons.io.FileUtils;

/**
 * Class for handling automatic backups
 * @author Tom Peham
 *
 */
public class BackupManager extends Thread {

	private Date lastBackup;
	private int backupInterval; //interval of backups in days
	private boolean interrupted;
	File backup;
	File DB;
	
	/**
	 * Public constructor
	 */
	BackupManager(int backupInterval, String srcPath, String destPath) {
		lastBackup = new Date();
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
			} finally {
				
			}
			
			while(! interrupted) {
			Thread.sleep(backupInterval * 24 * 60 * 60 * 100); //suspend thread until next backup
			
			try {
				copyDB();
			} catch(IOException e) {
				e.printStackTrace();
			}
			
			lastBackup = new Date(); //creating new date gets current day and time
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
		try {
			Database.getInstance().lockDB();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				Database.getInstance().unLockDB();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		FileUtils.copyDirectory(DB, backup);
		try {
			Database.getInstance().unLockDB();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				Database.getInstance().unLockDB();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Database.warehousePrint("BACKUP DONE");
	}
}
