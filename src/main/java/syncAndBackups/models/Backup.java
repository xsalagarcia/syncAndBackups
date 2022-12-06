package syncAndBackups.models;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

public class Backup {
	

	private File source;
	private File destination;
	private List<FullBackup> fullBackups;
	
	public Backup (File source, File destination) {
		this.source = source;
		this.destination = destination;
	}
	
	public FullBackup getLastFullBackup() {
		return fullBackups.stream().sorted((o1, o2)->  o1.getDateTime().compareTo(o2.getDateTime())).findFirst().get();

	}
	
	

	/**
	 * The fullBackup will be saved at destination\FullBackupYYYYMMDDHHMMSS
	 * @author xsala
	 *
	 */
	class FullBackup {
		private File destination;
		private LocalDateTime dateTime;
		private List<Differential> differentials;
		
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
		public List<Differential> getDifferentials() {
			return differentials;
		}
		public void setDifferentials(List<Differential> differentials) {
			this.differentials = differentials;
		}
		
		
	}
	
	/**
	 * The incremental will be saved at fullBackup.getDestination()\DifferentialYYYYMMDD. Will contain \ModifiedAndAded and deleted, a text file with lines of deleted elements (paths)
	 * @author xsala
	 *
	 */
	class Differential {
		private File destination;
		private LocalDateTime dateTime;
		private FullBackup fullbackup;
	}
	
	
	

}
