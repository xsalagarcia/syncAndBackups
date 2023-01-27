package syncAndBackups.models;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BackupInc extends Backup {

	public static final String LIST_OF_BACKUPS = "BackupIncList.dat";
	public static final String INCREMENTALS_FOLDER = "incremental";
	public BackupInc(File source, File destination) {
		super(source, destination);
	}


	public static String getAdditionalFolder(LocalDateTime ldt) {
		return INCREMENTALS_FOLDER + ldt.format(DateTimeFormatter.ofPattern(DATE_TIME_FOLDER));
	}
	
	public String getLastBackupFolder () {

		if (additionals.isEmpty()) {
			return getFullBackupFolder(fullBackup);
		} else {
			return getAdditionalFolder(getLastAdditional());
		}
	}

}
