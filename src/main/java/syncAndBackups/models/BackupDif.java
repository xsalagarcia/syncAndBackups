package syncAndBackups.models;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BackupDif extends Backup {

	public static final String LIST_OF_BACKUPS = "BackupList.dat";
	public static final String DIFFERENTIALS_FOLDER = "differential"; //always with YYYYMMddHHmmss <--TO BE OVERRIDED!
	public BackupDif(File source, File destination) {
		super(source, destination);
	}


	public static String getAdditionalFolder(LocalDateTime ldt) {
		return DIFFERENTIALS_FOLDER + ldt.format(DateTimeFormatter.ofPattern(DATE_TIME_FOLDER));
	}


}
