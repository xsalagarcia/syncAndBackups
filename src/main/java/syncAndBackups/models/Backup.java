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
public abstract class Backup {
	
	/**The name of the backup folder (will be followed by DATE_TIME_FOLDER pattern)*/
	protected static final String FULL_BACKUP_FOLDER = "full_backup";
	
	/**The date time pattern that follows FULL_BACKUP_FOLDER*/
	protected static final String DATE_TIME_FOLDER = "YYYYMMddHHmmss";
	
	/**Source folder of backup*/
	private File source;
	
	/**Destination folder of backup*/
	private File destination;
	
	/**LocalDateTime of the full backup*/
	protected LocalDateTime fullBackup;
	
	/**Some info about the last backup done*/
	private String lastBackupInfo;
	
	/**A List of LocalDateTime of the additional backups. They might be differential or incremental*/
	protected List<LocalDateTime> additionals = new LinkedList<LocalDateTime>();
	
	/**
	 * Constructor with source parameter and destination parameter.
	 * @param source folder
	 * @param destination folder
	 */
	public Backup (File source, File destination) {
		this.source = source;
		this.destination = destination;
	}
	
	/**
	 * Gets LocalDateTime of the full backup.
	 * @return Time representation of the full backup.
	 */
	public LocalDateTime getFullBackup() {
		return fullBackup;

	}
	
	/**
	 * Sets LocalDateTime of the full backup
	 * @param localDateTime representation of the full backup.
	 */
	public void setFullBackup(LocalDateTime localDateTime) {
		this.fullBackup = localDateTime;
	}
	
	/**
	 * Gets source folder.
	 * @return
	 */
	public File getSource() {
		return source;
	}

	/**
	 * Sets source folder.
	 * @param source
	 */
	public void setSource(File source) {
		this.source = source;
	}

	/**
	 * Gets destination folder.
	 * @return
	 */
	public File getDestination() {
		return destination;
	}

	/**
	 * Sets destination folder.
	 * @param destination
	 */
	public void setDestination(File destination) {
		this.destination = destination;
	}
	
	/**
	 * Gets {@code lastBackupInfo}.
	 * @return
	 */
	public String getLastBackupInfo() {
		return lastBackupInfo;
	}
	
	/**
	 * Sets {@code lastBackupInfo}
	 * @param lastBackupInfo
	 */
	public void setLastBackupInfo(String lastBackupInfo) {
		this.lastBackupInfo = lastBackupInfo;
	}
	
	/**
	 * Gets a list of {@code LocalDateTime} of additionals (incremental or differential).
	 * @return
	 */
	public List<LocalDateTime> getAdditionals(){
		return additionals;
	}
	
	/**
	 * Returns the last {@code LocalDateTime} of additional (incremental or differential).
	 * @return
	 */
	public LocalDateTime getLastAdditional() {
		if (additionals.size() == 0) return null;
		return additionals.stream().sorted((o1, o2)->o2.compareTo(o1)).findFirst().get();
	}

	/**
	 * Returns full backup folder given a {@code LocalDateTime}
	 * @param ldt
	 * @return
	 */
	public static String getFullBackupFolder (LocalDateTime ldt) {
		return FULL_BACKUP_FOLDER + ldt.format(DateTimeFormatter.ofPattern(DATE_TIME_FOLDER));
	}
	
	public enum Subclasses {
		DIFFERENTIAL,
		INCREMENTAL,
	}
	


	

}
