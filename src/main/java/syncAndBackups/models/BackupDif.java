package syncAndBackups.models;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents a Backup with full and incrementals or differentials.
 * The fullBackup will be saved at destination\full_backup
 * The incremental or differential will be saved at destination\dependsOnSubclassYYYYMMDDHHMMSS.
 * 
 */
public class BackupDif {
	
	public static final String LIST_OF_BACKUPS = "BackupList.dat";
	public static final String FULL_BACKUP_FOLDER = "full_backup"; //always with YYYYMMddHHmmss
	public static final String ADDITIONALS_FOLDER = "differential"; //always with YYYYMMddHHmmss <--TO BE OVERRIDED!
	private File source;
	private File destination;
	private LocalDateTime fullBackup;
	private String lastBackupInfo;
	private List<LocalDateTime> additionals = new LinkedList<LocalDateTime>();
	
	public BackupDif (File source, File destination) {
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
	
	public List<LocalDateTime> getAdditionals(){
		return additionals;
	}
	
	public LocalDateTime getLastDifferential() {
		if (additionals.size() == 0) return null;
		return additionals.stream().sorted((o1, o2)->o2.compareTo(o1)).findFirst().get();
	}

	public static String getFullBackupFolder (LocalDateTime ldt) {
		return FULL_BACKUP_FOLDER + ldt.format(DateTimeFormatter.ofPattern("YYYYMMddHHmmss"));
	}
	
	public static String getDifferentialFolder (LocalDateTime ldt) {
		return ADDITIONALS_FOLDER + ldt.format(DateTimeFormatter.ofPattern("YYYYMMddHHmmss"));
	}

	

}
