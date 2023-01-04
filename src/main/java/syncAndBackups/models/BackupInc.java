package syncAndBackups.models;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a Backup with full and incrementals.
 * The fullBackup will be saved at destination\full_backupYYYYMMDDHHMMSS
 * The incremental will be saved at destination\incrementalYYYYMMDDHHMMSS. Will contain \ModifiedAndAded and deleted, a text file with lines of deleted elements (paths)
 * 
 */
public class BackupInc {
	public static final String LIST_OF_BACKUPS = "BackupIncList.dat";
	public static final String FULL_BACKUP_FOLDER = "full_backup"; //always with YYYYMMddHHmmss
	public static final String INCREMENTAL_FOLDER = "incremental"; //always with YYYYMMddHHmmss
	private File source;
	private File destination;
	private LocalDateTime fullBackup;
	private String lastBackupInfo;
	private List<LocalDateTime> incrementals = new LinkedList<LocalDateTime>();
	
	
	public BackupInc (File source, File destination) {
		this.source = source;
		this.destination = destination;
	}
	
	public LocalDateTime getFullBackup() {
		return fullBackup;

	}
	
	public void setFullBackup(LocalDateTime localDateTime) {
		this.fullBackup = localDateTime;
	}
	
	

	public File getSource() {
		return source;
	}

	public void setSource(File source) {
		this.source = source;
	}

	public File getDestination() {
		return destination;
	}

	public void setDestination(File destination) {
		this.destination = destination;
	}
	
	public String getLastBackupInfo() {
		return lastBackupInfo;
	}
	
	public void setLastBackupInfo(String lastBackupInfo) {
		this.lastBackupInfo = lastBackupInfo;
	}
	
	public List<LocalDateTime> getIncrementals(){
		return incrementals;
	}
	
	public LocalDateTime getLastDifferential() {
		if (incrementals.size() == 0) return null;
		return incrementals.stream().sorted((o1, o2)->o2.compareTo(o1)).findFirst().get();
	}

	public static String getFullBackupFolder (LocalDateTime ldt) {
		return FULL_BACKUP_FOLDER + ldt.format(DateTimeFormatter.ofPattern("YYYYMMddHHmmss"));
	}
	
	public static String getIncrementalFolder (LocalDateTime ldt) {
		return INCREMENTAL_FOLDER + ldt.format(DateTimeFormatter.ofPattern("YYYYMMddHHmmss"));
	}
	
	public String getLastBackupFolder () {
		
		if (incrementals.isEmpty()) {
			return getFullBackupFolder(fullBackup);
		} else {
			return getIncrementalFolder(getLastDifferential());
		}
	}
	
	

}
