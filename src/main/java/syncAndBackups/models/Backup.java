package syncAndBackups.models;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Backup {
	
	public static final String LIST_OF_BACKUPS = "BackupList.dat";
	private File source;
	private File destination;
	private FullBackup fullBackup;
	private String lastBackupInfo;
	
	public Backup (File source, File destination) {
		this.source = source;
		this.destination = destination;
	}
	
	public FullBackup getFullBackup() {
		return fullBackup;

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



	/**
	 * The fullBackup will be saved at destination\FullBackup
	 * @author xsala
	 *
	 */
	public class FullBackup {

		private LocalDateTime dateTime;
		private List<Differential> differentials;
		
		
		public FullBackup(LocalDateTime dateTime) {
			this.dateTime = dateTime;
			
		}

		public LocalDateTime getDateTime() {
			return dateTime;
		}
		public void setDateTime(LocalDateTime dateTime) {
			this.dateTime = dateTime;
		}
		public List<Differential> getDifferentials() {
			return differentials;
		}
		public void setDifferentials(List<Differential> differentials) {
			this.differentials = differentials;
		}
		
		public Differential getLastDifferential() {
			if (differentials.size() == 0) return null;
			return differentials.stream().sorted((o1, o2)->o1.getDateTime().compareTo(o2.getDateTime())).findFirst().get();
		}
		
		
	}
	
	/**
	 * The incremental will be saved at destination\DifferentialYYYYMMDDHHMMSS. Will contain \ModifiedAndAded and deleted, a text file with lines of deleted elements (paths)
	 * @author xsala
	 *
	 */
	public class Differential {
		private File destination;
		private LocalDateTime dateTime;
		private FullBackup fullbackup;
		
		public File getDestination() {
			return destination;
		}
		
		
		public void setDestination(File destination) {
			this.destination = destination;
		}
		public LocalDateTime getDateTime() {
			return dateTime;
		}
		public void setDateTime(LocalDateTime dateTime) {
			this.dateTime = dateTime;
		}
		public FullBackup getFullbackup() {
			return fullbackup;
		}
		public void setFullbackup(FullBackup fullbackup) {
			this.fullbackup = fullbackup;
		}
		

		
		
		
	}
	
	
	

}
