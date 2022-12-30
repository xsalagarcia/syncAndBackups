package syncAndBackups.filesUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.checkerframework.common.util.report.qual.ReportCall;

import syncAndBackups.MainClass;

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
	 * @return a {@code String} with information about the task.
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
	
	
	/**
	 * Copy all files from source to destination. 
	 * @param source
	 * @param destination
	 * @return a {@code String} with information about the task.
	 */
	public static String totalCopy (Path source, Path destination) {
		
		StringBuilder report = new StringBuilder();
		
		try {
			if (!destination.toFile().isDirectory())
				Files.copy(source, destination, StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING );
		} catch (IOException e) {
			report.append("Couldn't copy " + source.toString() + ": " + e.toString() + System.lineSeparator());
		}
		
		try {
			Files.list(source).forEach(sourceFile ->{
				try {
					if (!destination.resolve(source.relativize(sourceFile)).toFile().isDirectory())
						Files.copy(sourceFile, destination.resolve(source.relativize(sourceFile)), StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING );
				} catch (IOException e) {
					report.append("Couldn't copy " + sourceFile.toString() + ": " + e.toString() + System.lineSeparator());
				}
			});
		} catch (IOException e) {
			 report.append("Error getting a list with filenames at " + source.toString() + ": " +e.toString() + System.lineSeparator() + 
				"Couldn't copy " + source.toString() + System.lineSeparator());
		}
		
		//recursive call if there are subfolders.
		try {
			Files.list(source).filter(path->path.toFile().isDirectory()).forEach(path -> report.append( totalCopy(path, destination.resolve(source.relativize(path)))));
		} catch (IOException e) {
			report.append("Couldn't get " + source + " subfolders: " + e.toString());
		}
		
	return report.toString();
	}
	
	/**
	 * Calls {@link #differentialCopy() differentialCopy()} . Creates
	 * a file differentialYYYYMMddHHmmss.txt at destination folder, which the first line contains {@code totalCopy} and
	 * the next lines contains the removed files.
	 * @param source Source folder.
	 * @param differential Differential folder.
	 * @param totalCopy Total copy folder.
	 * @return A String with report.
	 */
	public static String startDifferentialCopy (Path source, Path differential, Path totalCopy) {
		//String dateTimeSuffix = LocalDateTime.now().format(DateTimeFormatter.ofPattern("YYYYMMddHHmmss"));
		String[] res= differentialCopy(source, differential, totalCopy);
		differential.toFile().mkdirs();

		try {
			BufferedWriter bw = Files.newBufferedWriter(differential.getParent().resolve(differential.getFileName().toString() + ".txt") , StandardOpenOption.CREATE,StandardOpenOption.TRUNCATE_EXISTING);
			bw.write("Source: " + source.toString() + System.lineSeparator());
			bw.write(res[1]);
			bw.close();
		} catch (IOException e) {
			// 
			return "Couldn't create removed_files.txt!" + System.lineSeparator() + res[0];
		}
		return res[0];
	}
	
	
	/**
	 * It should be called through startDifferentialCopy.
	 * Copy the modified source files in relation to totalCopy and put these on destination\added.
	 * Returns a report and string of deleted files.
	 * Restoration will be possible copying files from differential and from totalCopy except those listed in destination\deteted.txt
	 * @param source
	 * @param destination
	 * @param totalCopy
	 * @return String[] with 2 elements. Element 0 is report, element 1 is list of removed files.
	 */
	private static String[] differentialCopy (Path source, Path differential, Path totalCopy) {
		
		StringBuilder report = new StringBuilder();
		StringBuilder removed = new StringBuilder();

		//copy modified
		try {
			Files.list(source).forEach(path-> {
				if (path.toFile().lastModified() !=  totalCopy.resolve(path.getFileName()).toFile().lastModified() && path.toFile().isFile()) {
					try {
						differential.toFile().mkdirs();
						Files.copy(path, differential.resolve(path.getFileName()), StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING );
					} catch (IOException e) {
						report.append("Couldn't copy " + path.toString() +": " +e.toString() + System.lineSeparator());
					}

				}
			});
		} catch (IOException e) {
			report.append ("Error getting a list with filenames at " + source.toString() + ": " +e.toString() + System.lineSeparator() + 
					"Couldn't check modified files" + System.lineSeparator());
		}
		
		//list removed
		if (totalCopy.toFile().exists()) {
			try {
				Files.list(totalCopy).forEach(path->{
					if (!source.resolve(path.getFileName()).toFile().exists()) {
						removed.append(source.resolve(path.getFileName()).toString() + System.lineSeparator()); 
					}
				});
			} catch (IOException e) {
				report.append ("Error getting a list with filenames at " + totalCopy.toString() + ": " +e.toString() + System.lineSeparator() + 
						"Couldn't check deleted files" + System.lineSeparator());
			}
		}
		//recursive call
		try {
			Files.list(source).filter(path->path.toFile().isDirectory()).forEach(path->{
				String[] result = differentialCopy( path, differential.resolve(path.getFileName()),totalCopy.resolve(path.getFileName()) );
				report.append(result[0]);
				removed.append(result[1]);
			});
		} catch (IOException e) {
			report.append("Couldn't get " + source + " subfolders: " + e.toString());
		}
		
			return new String[] {report.toString(), removed.toString()};
		
		
	}
	
	
	/**
	 * Restores a differential copy. To delete old deletes files (They existed at totalCopy, but not at the moment of differential copy),
	 * it's necessary to read a file with this information at the parent directory of the differential copy.
	 * @param restoreFolder The folder where the data will be restored.
	 * @param differential Folder that contains differential information.
	 * @param totalCopy Folder that contains total copy.
	 */
	public static String restoreWithDifferential (Path restoreFolder, Path differential, Path totalCopy) {
		StringBuilder report = new StringBuilder();
		//restoreFolder.toFile().mkdirs();
		
		//This could be improved: First total copy of differential, after this, total copy of non existing at differential from total.
		report.append(totalCopy(totalCopy, restoreFolder) + System.lineSeparator());
		report.append(totalCopy(differential, restoreFolder) + System.lineSeparator());
		//delete deleted files, listed at parent_of_differential\differential_name.txt
		try {
			
			BufferedReader br = Files.newBufferedReader(new File(differential.toString()+".txt").toPath());
			String pathAsString = br.readLine();
			String oldOriginFolder =null;
			if (pathAsString != null && pathAsString.length()>8 && pathAsString.subSequence(0, 8).equals("Source: ")) {
				oldOriginFolder = pathAsString.subSequence(8, pathAsString.length()).toString();	
			}
			
			if (oldOriginFolder == null) {
				report.append(MainClass.getStrings().getString("couldnt_complete_rfr") + System.lineSeparator());
				return report.toString();
			}
			
			Path oldOriginPath = new File(oldOriginFolder).toPath();
			pathAsString= br.readLine();
			while(pathAsString != null) {
				Path p = (restoreFolder.resolve(oldOriginPath.relativize(new File( pathAsString).toPath())));
				if (p.toFile().isDirectory()){
					report.append(deleteNonEmptyDirectory(p));
				} else {
					p.toFile().delete();
				}
				pathAsString = br.readLine();
			}
			br.close();
		} catch (IOException e) {

			report.append(MainClass.getStrings().getString("couldnt_complete_rfr") + ":" + e.toString() + System.lineSeparator());
		}
		/*
		 * 1, copiar tot lo de total
		 * 2, copiar lo de differential sobreescrivint
		 * 3, esborrar lo de deleted files.
		 */
		return report.toString();
	}
	
	
	/**
	 * Returns a hashMap <Path,Long> with the full file name and last modified attribute.
	 * @param directory
	 * @return HashMap <Path,Long> with the full file name and last modified attribute.
	 */
	public static LinkedHashMap<Path, Long> getHashMapFilesAndLastMod(Path directory) throws IOException {
		final var hm = new LinkedHashMap<Path, Long>(); 

			Files.walk(directory).filter(path -> path.toFile().isFile()).forEach(path->hm.put(path, path.toFile().lastModified()));

		return hm;
	}
	
	/**
	 * Copies the files at {@value hashMapWithSource} to {@value destination}. Returns a string with non copied files and the exception.
	 * If some file can't be copied, this will be removed from {@value hashMapWithSource}. 
	 * @param hashMapWithSource
	 * @param source
	 * @param destination
	 * @return a String with the exceptions and files non copied.
	 */
	public static String copyFromHashMapTo(HashMap<Path, Long> hashMapWithSource, Path source, Path destination) {
		StringBuilder report = new StringBuilder();
		hashMapWithSource.keySet().forEach(path -> { 
			
			try {
				Files.copy(path, destination.resolve(source.relativize(path)) );
			} catch (IOException e) {
				report.append("Couldn't copy " + source.toString() + ": " + e.toString() + System.lineSeparator());
				hashMapWithSource.remove(path);
			}});
		return report.toString();
	}
	

	
	public static void saveFileList(LinkedHashMap<Path, Long> hashMapToSave, File file ) throws IOException{

			ObjectOutputStream outputStream = new ObjectOutputStream (new FileOutputStream(file));
			outputStream.writeInt(hashMapToSave.size());
			

			hashMapToSave.forEach((k,v)->{

				try {
					outputStream.writeObject(k);
					//outputStream.writeUTF(k.toString());
					outputStream.writeLong(v);
				} catch (IOException e) {

					e.printStackTrace();

				}

			});

			outputStream.close();
	
		
	}
	
	
	
	
}
