package syncAndBackups;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import syncAndBackups.filesUtils.FileSyncAndBackupUtils;
import syncAndBackups.gsonUtils.FileDeserializer;
import syncAndBackups.gsonUtils.FileSerializer;
import syncAndBackups.gsonUtils.LocalDateTimeJsonDeserializer;
import syncAndBackups.gsonUtils.LocalDateTimeJsonSerializer;
import syncAndBackups.javafxUtils.Dialogs;
import syncAndBackups.models.BackupDif;

/**
 * Controller class for BackupPane.
 * It is shown in the MainScreen, when backup is selected.
 * With a table and a button for backup.
 * @author xsala
 *
 */
public class BackupPaneController {
	
	private Task<String> currentBackupTask = null;

	private BackupDif newBackup = new BackupDif(new File(MainClass.getStrings().getString("double_click_add_new")), null);
	
	private TextArea consoleTA;
	
    @FXML
    private TableView<BackupDif> backupTable;

    @FXML
    private TableColumn<BackupDif, String> differentialBackupCol;

    @FXML
    private Button backupBtn;

    @FXML
    private TableColumn<BackupDif, String> fullBackupCol;

    @FXML
    private TableColumn<BackupDif, Path> sourceCol;
    
	@FXML
	private void initialize() {
		setTable();
		loadList();
		backupTable.setOnMouseClicked(me->tableClicked(me));
		
		backupBtn.setOnAction(ae-> backupBtnPressed());

		//Add option will be always on last position.
		backupTable.getItems().addListener((ListChangeListener<BackupDif>)c->{
			if (backupTable.getItems().indexOf(newBackup) != backupTable.getItems().size()-1 && backupTable.getItems().indexOf(newBackup) >= 0 ) {
				backupTable.getItems().remove(newBackup);
				backupTable.getItems().add(newBackup);
				
			}
		});
	}
	
	/**
	 * Called when backupBtn is pressed.
	 * If there is a currentSyncTask in action, cancels it.
	 * If there is no currentSyncTask, creates one and sets a listener to message property. The messages will be put on {@value consoleTA}
	 */
	private void backupBtnPressed() {
		// TODO Auto-generated method stub
		
		if (currentBackupTask == null) {
			backupBtn.setText(MainClass.getStrings().getString("stop_backup"));
			ProgressIndicator pi = new ProgressIndicator();
			pi.setMaxHeight(15);
			pi.setMaxWidth(15);
			backupBtn.setGraphic(pi);
			
			Task<String> backupTask = createBackupTask();
			
			
			backupTask.messageProperty().addListener((obs, oldV, newV)->consoleTA.appendText(newV + System.lineSeparator()));
			
			backupTask.setOnSucceeded(wse-> {
				consoleTA.appendText( backupTask.getValue() + System.lineSeparator());
				backupBtn.setText(MainClass.getStrings().getString("backup"));
				backupBtn.setGraphic(null);
				currentBackupTask = null;
			});
	
			Thread t = new Thread(backupTask);
			t.start();
			currentBackupTask = backupTask;
		} else {
			

				if (Dialogs.createDialogConfirmation(MainClass.getStrings().getString("want_to_stop_question"))) {
					currentBackupTask.cancel();

				} 		
				

			
			currentBackupTask.cancel();
			backupBtn.setGraphic(null);
			backupBtn.setText(MainClass.getStrings().getString("backup"));
			
		}
		backupTable.refresh();
	}
	

	public void setConsole(TextArea consoleTA) {
		this.consoleTA = consoleTA;
	}
	
	/**
	 * Sets backupTable and its columns.
	 * The format for fullBackupCol and differentialBackupCol with a DateTimeFormatter, table with multiple selection.
	 */
	private void setTable() {
		
		sourceCol.setCellValueFactory(new PropertyValueFactory<BackupDif, Path>("source"));
		
		fullBackupCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<BackupDif, String>, ObservableValue<String>>(){
			@Override
			public ObservableValue<String> call(CellDataFeatures<BackupDif, String> arg) {
				
				LocalDateTime ldt = arg.getValue().getFullBackup();
				
				String s = (ldt == null)? "": ldt.format(DateTimeFormatter.ofPattern(MainClass.DATE_TIME_PATTERN));
				
				return new ReadOnlyObjectWrapper<String>(s);

			}
		} );
		
		differentialBackupCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<BackupDif, String>, ObservableValue<String>>(){
			@Override
			public ObservableValue<String> call(CellDataFeatures<BackupDif, String> arg) {
				
				LocalDateTime ldt = arg.getValue().getLastAdditional();
								
				String s = (ldt == null)? "": ldt.format(DateTimeFormatter.ofPattern(MainClass.DATE_TIME_PATTERN));
				
				return new ReadOnlyObjectWrapper<String>(s);

			}
		} );
		
		backupTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		
	}
	
	/**
	 * Loads the list of backups if the file exists. And after this, includes newBackup object.
	 */
	private void loadList() {
		//syncTable.getItems().add(new SyncOneDirection(new File("C:\\workspaces"), new File("J:\\workspaces"), LocalDateTime.now()));
		File file = new File(System.getProperty("user.home") + "\\" + MainClass.PROGRAM_NAME +"\\" + BackupDif.LIST_OF_BACKUPS);
		if (file.exists()) {
			try {
				InputStreamReader isw = new InputStreamReader (new FileInputStream(file));
				char[] cbuf = new char[(int)file.length()];
				isw.read(cbuf);
				
				GsonBuilder gsonBuilder = new GsonBuilder();
				
				gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeJsonDeserializer());
				gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeJsonSerializer());
				gsonBuilder.registerTypeAdapter(File.class, new FileSerializer());
				gsonBuilder.registerTypeAdapter(File.class, new FileDeserializer());
				Gson gson = gsonBuilder.create(); 
				Type listType = new TypeToken<ArrayList<BackupDif>>() {}.getType();
				List<BackupDif> list = gson.fromJson(new String(cbuf), listType );
				backupTable.getItems().addAll(list);
				isw.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		backupTable.getItems().add(newBackup);
		
		
	}
	
	/**
	 * Saves the backupTable list to a file.
	 */
	public void saveList () {
		try {
			File folder = new File(System.getProperty("user.home") + "\\" + MainClass.PROGRAM_NAME);
			if (!folder.exists()) {
				folder.mkdir();
			}
			OutputStreamWriter osw= new OutputStreamWriter( new FileOutputStream(folder.toString()+ "\\" + BackupDif.LIST_OF_BACKUPS));
			String s = null;
			List<BackupDif> array=  backupTable.getItems().subList(0, backupTable.getItems().size()-1);
			
			GsonBuilder gsonBuilder = new GsonBuilder();

			gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeJsonDeserializer());
			gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeJsonSerializer());
			gsonBuilder.registerTypeAdapter(File.class, new FileSerializer());
			gsonBuilder.registerTypeAdapter(File.class, new FileDeserializer());
			Gson gson = gsonBuilder.create(); 
			
			s = gson.toJson(array);

			
			osw.write(s, 0, s.length());
			osw.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Called when table is clicked.
	 * Double click will open the item or, if the item is newBackup value, it will open a new item.
	 * The item opening or the new item creation will be done calling openBackupWindow()
	 * @param me the mouse event.
	 */
	private void tableClicked(MouseEvent me) {
		
		if (me.getClickCount() == 2 && backupTable.getSelectionModel().getSelectedItem() != null) {
			if (backupTable.getSelectionModel().getSelectedItem().equals(newBackup)) {
				openBackupWindow(null);
			} else  {
				openBackupWindow(backupTable.getSelectionModel().getSelectedItem());
			}
			
		}
	}
	
	/**
	 * Opens a new window for item edition or creation.
	 * @param backup the item to be edited. If null, it will create a new.
	 */
	private void openBackupWindow(BackupDif backup) {


		Stage stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("BackupObject.fxml"), MainClass.getStrings());
		Parent backupPaneRoot= null;
		try {
			backupPaneRoot = fxmlLoader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		BackupObjectController backupObjectController = (BackupObjectController) fxmlLoader.getController();
		
		if (backup == null) {
			stage.setOnHiding(we-> {
				if (backupObjectController.getBackup() != null) {
					backupTable.getItems().add(backupTable.getItems().size()-1, backupObjectController.getBackup());
				}
			});
		} else {
			stage.setOnHiding(we-> {
				if (backupObjectController.getBackup() == null) {
					backupTable.getItems().remove(backup);
				}
				backupTable.refresh();
			});
		}
		backupObjectController.setBackup(backup);

		
		
		stage.setScene(new Scene(backupPaneRoot));

		

		stage.show();
	}
	
	/**
	 * Creates a Task<String> for backup works.
	 * The task takes the selected items from syncTable and for each one, does the synchronization work.
	 * Update messages will be created for each synchronization work (start, incidences).
	 * At the end (onSucceed), the value will be "synchronization finished".
	 * @return
	 */
	private Task<String> createBackupTask(){
		backupTable.getSelectionModel().clearSelection(backupTable.getItems().indexOf(newBackup)); //unselect newBackup
		List<BackupDif> listToBackup = backupTable.getSelectionModel().getSelectedItems().stream().toList();

		Task<String> backupTask = new Task<String>() {

			private BackupDif activeBackup = null;
			
			@Override
			protected String call() throws Exception {
				
				updateMessage(System.lineSeparator() + listToBackup.size() + MainClass.getStrings().getString("backup_actions"));
				
				
				
				listToBackup.forEach(ab -> {
					activeBackup= ab;
					updateMessage((listToBackup.indexOf(ab)+1) + "/" + listToBackup.size() +": " +ab.getSource().toString() +" -> " + ab.getDestination().toString() + ": " 
							+ ab.getFullBackup() == null? MainClass.getStrings().getString("full_backup") : MainClass.getStrings().getString("diff_backup"));
					
					LocalDateTime ldt = LocalDateTime.now();

					if (ab.getFullBackup() == null) {
						ab.setLastBackupInfo(FileSyncAndBackupUtils.totalCopy(ab.getSource().toPath(), ab.getDestination().toPath().resolve(BackupDif.getFullBackupFolder(ldt))  ) );
					} else {
						ab.setLastBackupInfo(FileSyncAndBackupUtils.startDifferentialCopy(
								ab.getSource().toPath(),
								ab.getDestination().toPath().resolve(BackupDif.getAdditionalFolder(ldt)),
								ab.getDestination().toPath().resolve(BackupDif.getFullBackupFolder(ab.getFullBackup()))));
					}
					
					if (ab.getLastBackupInfo().length() > 0) {
						updateMessage(String.format( MainClass.getStrings().getString("incidences_from_source_dest"),ab.getSource().toString(), ab.getDestination().toString() ));
						//updateMessage("Incidences! You have to watch " + sod.getSource().toString() + " -> " + sod.getDestination().toString());
						
					} else {
						updateMessage(String.format(MainClass.getStrings().getString("backup_form_source_dest"), ab.getSource().toString(), ab.getDestination().toString()));
						//updateMessage("Synchronization " + sod.getSource().toString() + " -> " + sod.getDestination().toString() + " OK!" );
						ab.setLastBackupInfo(MainClass.getStrings().getString("backup_successful"));
					}
					if (ab.getFullBackup()== null) {
						ab.setFullBackup(ldt);
					} else {
						ab.getAdditionals().add(ldt);
					}
					
				});
				
				return MainClass.getStrings().getString("sync_finished");
			}
			
	         @Override protected void cancelled() {
	             super.cancelled();
	             activeBackup.setLastBackupInfo(MainClass.getStrings().getString("backup_interrupted"));
	             updateMessage(String.format(MainClass.getStrings().getString("sync_cancelled_from_source_dest"), activeBackup.getSource(), activeBackup.getDestination() ));
	         }
			
		};
		
		return backupTask;
	}
	

}
