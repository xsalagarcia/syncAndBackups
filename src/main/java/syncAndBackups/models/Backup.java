package syncAndBackups.models;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a Backup with full and differentials.
 * The fullBackup will be saved at destination\FullBackup
 * The incremental will be saved at destination\DifferentialYYYYMMDDHHMMSS. Will contain \ModifiedAndAded and deleted, a text file with lines of deleted elements (paths)
 * 
 * @param lastBackupInfo
 */
public class Backup {
	
	public static final String LIST_OF_BACKUPS = "BackupList.dat";
	private File source;
	private File destination;
	private LocalDateTime fullBackup;
	private String lastBackupInfo;
	private List<LocalDateTime> differentials = new LinkedList<LocalDateTime>();
	
	public Backup (File source, File destination) {
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
	
	public List<LocalDateTime> getDifferentials(){
		return differentials;
	}
	
	public LocalDateTime getLastDifferential() {
		if (differentials.size() == 0) return null;
		return differentials.stream().sorted((o1, o2)->o2.compareTo(o1)).findFirst().get();
	}


	

}
