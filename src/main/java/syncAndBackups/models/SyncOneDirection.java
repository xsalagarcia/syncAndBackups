package syncAndBackups.models;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDateTime;

public class SyncOneDirection {
	public static final String LIST_OF_SYNC = "SyncList.dat";
	private File source;
	private File destination;
	private LocalDateTime lastSync;
	private String lastSyncInfo;
	
	
	
	public SyncOneDirection(File source, File destination, LocalDateTime lastSync) {
		this.source = source;
		this.destination = destination;
		this.lastSync = lastSync;
	}
	
	public SyncOneDirection(File source, File destination) {
		this.source = source;
		this.destination = destination;
	}



	public String getLastSyncInfo() {
		return lastSyncInfo;
	}
	
	public void setLastSyncInfo(String lastSyncInfo) {
		this.lastSyncInfo = lastSyncInfo;
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



	public LocalDateTime getLastSync() {
		return lastSync;
	}



	public void setLastSync(LocalDateTime lastSync) {
		this.lastSync = lastSync;
	}
	
}
