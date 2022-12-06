package syncAndBackups.filesUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.LinkedList;
import java.util.List;

/**
 * A class with static methods for sync and backup.
 * @author xsala
 *
 */
public class FileSyncAndBackupUtils {
	
	/**
	 * Synchronizes the destination with source content. That is, new files from source to destination (they doesn't exist in destination or they are old).
	 * And deletes files from destination that doesn't exist at source.
	 * @param source
	 * @param destination
	 */
	public static String syncFromTo(Path source, Path destination)  {
		StringBuilder report = new StringBuilder();
		report.append( copyModifiedFiles(source, destination));
		report.append( deleteDeletedFromDestination(source, destination));
		return report.toString();
	}

	
	/**
	 * Copies files from source to destination if the files doesn't exist in destination or they are modified recently in source. Recursively to all subfolders.
	 * @param source
	 * @param destination
	 * @return String with errors report. If there weren't any exceptions/errors
	 */
	private static String copyModifiedFiles (Path source, Path destination) {
		
		StringBuilder report = new StringBuilder();
		
		//first, checks destination folder, if it doesn't exist, it will be created.
		if (!destination.toFile().exists()) {
			if (!destination.toFile().mkdirs()) {
				return "Couldn't create " + destination.toString() + " and copy " + source.toString() + System.lineSeparator();
			}
		}
		
		//Gets a List with the filenames at destination folder
		List<String> destinationFileNamesAtWorkingFolder = new LinkedList<String>();
		try {
			destinationFileNamesAtWorkingFolder.addAll( Files.list(destination).filter(path -> path.toFile().isFile()).map(path -> path.getFileName().toString()).toList());
		} catch (IOException e1) {
			return "Error getting a list with filenames at " + destination.toString() + ": " +e1.toString() + System.lineSeparator() + 
					"Couldn't copy " + destination.toString() + System.lineSeparator();
		}
		
		
		try {
			Files.list(source).filter(path -> path.toFile().isFile()).forEach(srcPath-> { //for each file at source
				
				//gets the index of destinationFile that matches with the source filename. Will be -1 if destinationFile doesn't exist
				int destinationFileIndex = destinationFileNamesAtWorkingFolder.indexOf(srcPath.getFileName().toString()); 
				if (destinationFileIndex < 0 || new File (destination.toFile(), destinationFileNamesAtWorkingFolder.get(destinationFileIndex)).lastModified() < srcPath.toFile().lastModified() ) { //if not contains a destination file or
					try {
						//System.out.println("Coypying " + srcPath.toString());
						Files.copy(srcPath, new File(destination.toFile(), srcPath.getFileName().toString()).toPath(), StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING );
					} catch (IOException e) {
						report.append("Couldn't copy " + srcPath.toString() + ": " + e.toString() + System.lineSeparator());
					}
				}
			});
		} catch (IOException e1) {
			return "Error getting a list with filenames at " + source.toString() + ": " +e1.toString() + System.lineSeparator() + 
					"Couldn't copy " + destination.toString();
		}
		
		//calls itself recursively for each subFolder
		try {
			Files.list(source).filter(path -> path.toFile().isDirectory()).forEach(subFolder-> {
					report.append( copyModifiedFiles(subFolder, new File(destination.toFile(), subFolder.getFileName().toString()).toPath()) ) ;
			});
		} catch (IOException e) {
			report.append("Couldn't get " + source + " subfolders: " + e.toString());
		}
		
		return report.toString();
	}
	
	
	
	/**
	 * Checks if destination contains some files that source doesn't contain. If it's, the destination file will be removed.
	 * @param source
	 * @param destination
	 * @return String with errors, if there were.
	 */
	static String deleteDeletedFromDestination (Path source, Path destination) {
		StringBuilder report = new StringBuilder();
		
		
		//The list contains the names of directories
		final List<String> sourceDirNamesAtWorkingFolder = new LinkedList<String>();
		try {
			 sourceDirNamesAtWorkingFolder.addAll( Files.list(source).filter(path-> path.toFile().isDirectory()).map(path->path.getFileName().toString()).toList());
		} catch (IOException e1) {
			return "Couldn't access to source directories when tried to delete at destination: " + e1.toString() + System.lineSeparator();
		}
		
		try {
			Files.list(destination).filter(path-> path.toFile().isDirectory()).forEach(destDirectory->{ //For each directory in destination
				if (sourceDirNamesAtWorkingFolder.indexOf(destDirectory.getFileName().toString()) == -1) { //the directory doesn't exist at source will be deleted.
						report.append( deleteNonEmptyDirectory(destDirectory));
				}
			});
		} catch (IOException e1) {
			return "Couldn't access to destination directories when tried to delete at destination: " + e1.toString() + System.lineSeparator();
		}
		
		
		//The list contains the names of file
		final List<String >sourceFileNamesAtWorkingFolder = new LinkedList<String>();
		
		try {
			sourceFileNamesAtWorkingFolder.addAll( Files.list(source).filter(path-> path.toFile().isFile()).map(path->path.getFileName().toString()).toList());
		} catch (IOException e1) {
			return "Couldn't access to source file names when tried to delete at destination: " + e1.toString() + System.lineSeparator();
		}
		
		try {
			Files.list(destination).filter(path-> path.toFile().isFile()).forEach(destFile->{ //For each file in destination
				if (sourceFileNamesAtWorkingFolder.indexOf(destFile.getFileName().toString()) == -1) { //the file doesn't exist at source will be deleted.
					destFile.toFile().delete();
				}
			});
		} catch (IOException e1) {
			return "Couldn't access to destination file names when tried to delete at destination: " + e1.toString() + System.lineSeparator();
		}
		
		//calls itself recursively for each subFolder
		try {
			Files.list(source).filter(path -> path.toFile().isDirectory()).forEach(subFolder-> {
					deleteDeletedFromDestination(subFolder, new File(destination.toFile(), subFolder.getFileName().toString()).toPath()) ;
			});
		} catch (IOException e) {
			return "Couldn't access to source directiries names when tried to delete at destination: " + e.toString() + System.lineSeparator();
		}
		
		return report.toString();
		
		
	}



	/**
	 * Deletes the directory. If it isn't empty, the contain will be erased recursively.
	 * @param path The directory to be erased.
	 * @throws IOException
	 */
	static String deleteNonEmptyDirectory(Path path) {
		StringBuilder report = new StringBuilder();
		if (!path.toFile().delete() && path.toFile().isDirectory()) {
			try {
				Files.list(path).filter(p->p.toFile().isFile()).forEach(f->f.toFile().delete());
			} catch (IOException e) {
				report.append("Couldn't delete " + path.toString() + ": " + e.toString() + System.lineSeparator());
			}
			try {
				Files.list(path).filter(p->p.toFile().isDirectory()).forEach(d->deleteNonEmptyDirectory(d));
			} catch (IOException e) {
				report.append("Couldn't delete " + path.toString() + ": " + e.toString() + System.lineSeparator());
			}
			path.toFile().delete();
		}
		return report.toString();
	}
}
