package warehouse;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.stream.Stream;

//import org.apache.derby.iapi.services.io.FileUtil;

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
	public BackupManager(int backupInterval, String srcPath, String destPath) {
		lastBackup = new Date();
		this.backupInterval = backupInterval;
		interrupted = false;
		this.backup = new File(destPath);
		this.DB = new File(srcPath);
	}
	
	@Override
	public void run() {
		try {
			try {
				System.out.println("blah");
				copyDB();
				System.out.println("bluh");
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
			
			lastBackup = new Date(); //creating new date gets current day and time
			}
		} catch(InterruptedException e) {
			interrupted = true;
		}
		
	}
	
	private void copyDB() throws IOException {
		//FileUtils.copyDirectory(DB, backup);
//		try(Stream<Path> stream = Files.walk(DB.toPath())) {
//			stream.forEach(sourcePath -> {
//				try {
//					Files.copy(sourcePath, DB.toPath().resolve(backup.toPath().relativize(sourcePath)));
//					System.out.println(sourcePath);
//				} catch(Exception e) {
//					e.printStackTrace();
//				}
//			});
//		}
		
		
	}
}
