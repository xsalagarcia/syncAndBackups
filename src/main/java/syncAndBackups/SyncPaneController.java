package syncAndBackups;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

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
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
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
import syncAndBackups.models.SyncOneDirection;
import java.lang.reflect.Type;



/**
 * Controller class for SyncPane.fxml
 * It is shown in the MainScreen, when Synchronize is selected.
 * With a table and a button for backup.
 * @author xsala
 *
 */
public class SyncPaneController {

	private Thread thread;
	private SyncOneDirection newSync = new SyncOneDirection(new File(MainClass.getStrings().getString("double_click_add_new")), null, null);
	
	private TextArea consoleTA;
	
	private Task<String> currentSyncTask = null;
	
    @FXML
    private TableColumn<SyncOneDirection, String> lastSyncCol;

    @FXML
    private TableColumn<SyncOneDirection, Path> sourceCol;

    @FXML
    private TableView<SyncOneDirection> syncTable;
    
    @FXML
    private Button syncBtn;

	
	public void setConsole(TextArea consoleTA) {
		this.consoleTA = consoleTA;
	}
	
	@FXML
	private void initialize() {

		
		setTable();
		loadList();
		syncTable.setOnMouseClicked(me->tableClicked(me));
		
		syncBtn.setOnAction(ae-> syncBtnPressed());

		//Add option will be always on last position.
		syncTable.getItems().addListener((ListChangeListener<SyncOneDirection>)c->{
			if (syncTable.getItems().indexOf(newSync) != syncTable.getItems().size()-1 && syncTable.getItems().indexOf(newSync) >= 0 ) {
				syncTable.getItems().remove(newSync);
				syncTable.getItems().add(newSync);
				
			}
		});
		
		
	}
	
	/**
	 * Loads the list of syncOneDirection objects from the file.
	 */
	private void loadList() {
		//syncTable.getItems().add(new SyncOneDirection(new File("C:\\workspaces"), new File("J:\\workspaces"), LocalDateTime.now()));
		File file = new File(System.getProperty("user.home") + "\\" + MainClass.PROGRAM_NAME +"\\" + SyncOneDirection.LIST_OF_SYNC);
		if (file.exists()) {
			try {
				InputStreamReader isw = new InputStreamReader (new FileInputStream(file),  StandardCharsets.US_ASCII);
				char[] cbuf = new char[(int)file.length()];
				isw.read(cbuf);
				
				GsonBuilder gsonBuilder = new GsonBuilder();
				
				gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeJsonDeserializer());
				gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeJsonSerializer());
				gsonBuilder.registerTypeAdapter(File.class, new FileSerializer());
				gsonBuilder.registerTypeAdapter(File.class, new FileDeserializer());
				Gson gson = gsonBuilder.create(); 
				Type listType = new TypeToken<ArrayList<SyncOneDirection>>() {}.getType();
				
				JsonReader jr = new JsonReader(isw);
				
				jr.setLenient(true);
				String str = new String(cbuf);
				

				
				List<SyncOneDirection> list = gson.fromJson(new String(cbuf), listType );
				syncTable.getItems().addAll(list);
				isw.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		syncTable.getItems().add(newSync);
		
		
	}
	
	/**
	 * Save the list of syncObjects to the file.
	 */
	public void saveList () {
		try {
			File folder = new File(System.getProperty("user.home") + "\\" + MainClass.PROGRAM_NAME);
			if (!folder.exists()) {
				folder.mkdir();
			}
			OutputStreamWriter osw= new OutputStreamWriter( new FileOutputStream(folder.toString()+ "\\" + SyncOneDirection.LIST_OF_SYNC), StandardCharsets.US_ASCII);
			String s = null;
			List<SyncOneDirection> array=  syncTable.getItems().subList(0, syncTable.getItems().size()-1);
			
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
	 * Sets syncTable and its columns.
	 * The format for lastSyncCol with a DateTimeFormatter. Table with multiple selection.
	 */
	private void setTable() {

		
		sourceCol.setCellValueFactory(new PropertyValueFactory<SyncOneDirection, Path>("source"));

		lastSyncCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<SyncOneDirection, String>, ObservableValue<String>>(){
			@Override
			public ObservableValue<String> call(CellDataFeatures<SyncOneDirection, String> arg) {
				LocalDateTime ldt = arg.getValue().getLastSync();
				String s = (ldt == null)? "": ldt.format(DateTimeFormatter.ofPattern(MainClass.DATE_TIME_PATTERN));
				
				return new ReadOnlyObjectWrapper<String>(s);

			}
		} );
		syncTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	}

	/**
	 * Called when table is clicked.
	 * Double click will open the item or, if the item is newSync value, it will open a new item.
	 * The item opening or the new item creation will be done calling opensyncObjectWindow()
	 * @param me the mouse event.
	 */
	private void tableClicked(MouseEvent me) {
		
		if (me.getClickCount() == 2 && syncTable.getSelectionModel().getSelectedItem() != null) {
			if (syncTable.getSelectionModel().getSelectedItem().equals(newSync)) {
				openSyncObjectWindow(null);
			} else  {
				openSyncObjectWindow(syncTable.getSelectionModel().getSelectedItem());
			}
			
		}
	}
	
	
	/**
	 * Opens a new window for item edition or creation.
	 * @param syncOneDirection the item to be edited. If null, it will create a new.
	 */
	private void openSyncObjectWindow(SyncOneDirection syncOneDirection) {


		Stage stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("SyncObject.fxml"), MainClass.getStrings());
		Parent syncPaneRoot= null;
		try {
			syncPaneRoot = fxmlLoader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		SyncObjectController syncObjectController = (SyncObjectController) fxmlLoader.getController();
		
		if (syncOneDirection == null) {
			stage.setOnHiding(we-> {
				if (syncObjectController.getSyncOneDirection() != null) {
					syncTable.getItems().add(syncTable.getItems().size()-1, syncObjectController.getSyncOneDirection());
				}
			});
		} else {
			stage.setOnHiding(we-> {
				if (syncObjectController.getSyncOneDirection() == null) {
					syncTable.getItems().remove(syncOneDirection);
				}
				syncTable.refresh();
			});
		}
		syncObjectController.setSyncObjectOneDirection(syncOneDirection);

		
		
		stage.setScene(new Scene(syncPaneRoot));

		

		stage.show();
	}
	
	
	/**
	 * Called when sync button is pressed.
	 * If there is a currentSyncTask in action, cancels it.
	 * If there is no currentSyncTask, creates one and sets a listener to message property. The messages will be put on {@value consoleTA}
	 */
	private void syncBtnPressed() {
		
		if (currentSyncTask == null) {
			syncBtn.setText(MainClass.getStrings().getString("stop_sync"));
			ProgressIndicator pi = new ProgressIndicator();
			pi.setMaxHeight(15);
			pi.setMaxWidth(15);
			syncBtn.setGraphic(pi);
			
			Task<String> syncTask = createSyncTask();
			
			
			syncTask.messageProperty().addListener((obs, oldV, newV)->consoleTA.appendText(newV + System.lineSeparator()));
			
			syncTask.setOnSucceeded(wse-> {
				consoleTA.appendText( syncTask.getValue() + System.lineSeparator());
				syncBtn.setText(MainClass.getStrings().getString("synchronize"));
				syncBtn.setGraphic(null);
				currentSyncTask = null;
			});
	
			Thread t = new Thread(syncTask);
			thread = t;
			t.start();
			currentSyncTask = syncTask;
		} else {
			
			if (Dialogs.createDialogConfirmation(MainClass.getStrings().getString("want_to_stop_question"))) {
				currentSyncTask.cancel();
				syncBtn.setGraphic(null);
				syncBtn.setText(MainClass.getStrings().getString("synchronize"));

			} 

		}
		syncTable.refresh();
	}
	
	/**
	 * Creates a Task<String> for synchronization works.
	 * The task takes the selected items from syncTable and for each one, does the synchronization work.
	 * Update messages will be created for each synchronization work (start, incidences).
	 * At the end (onSucceed), the value will be "synchronization finished".
	 * @return The Task<String>
	 */
	private Task<String> createSyncTask(){
		syncTable.getSelectionModel().clearSelection(syncTable.getItems().indexOf(newSync));
		List<SyncOneDirection> listToSync = syncTable.getSelectionModel().getSelectedItems().stream().toList();

		Task<String> syncTask = new Task<String>() {

			private SyncOneDirection activeSod = null;
			
			@Override
			protected String call() throws Exception {
				
				updateMessage(System.lineSeparator() + listToSync.size() + MainClass.getStrings().getString("sync_actions"));
				
				
				
				listToSync.forEach(sod -> {
					activeSod = sod;
					updateMessage((listToSync.indexOf(sod)+1) + "/" + listToSync.size() +": " +sod.getSource().toString() +" -> " + sod.getDestination().toString());
					
					sod.setLastSyncInfo( FileSyncAndBackupUtils.syncFromTo(sod.getSource().toPath(), sod.getDestination().toPath() ));
					if (sod.getLastSyncInfo().length() > 0) {
						updateMessage(String.format( MainClass.getStrings().getString("incidences_from_source_dest"),sod.getSource().toString(), sod.getDestination().toString() ));
						//updateMessage("Incidences! You have to watch " + sod.getSource().toString() + " -> " + sod.getDestination().toString());
						
					} else {
						updateMessage(String.format(MainClass.getStrings().getString("synchronization_form_source_dest"), sod.getSource().toString(), sod.getDestination().toString()));
						//updateMessage("Synchronization " + sod.getSource().toString() + " -> " + sod.getDestination().toString() + " OK!" );
						sod.setLastSyncInfo(MainClass.getStrings().getString("sync_successful"));
					}
					sod.setLastSync(LocalDateTime.now());
				});
				
				return MainClass.getStrings().getString("sync_finished");
			}
			
	         @Override protected void cancelled() {
	             super.cancelled();
	             activeSod.setLastSyncInfo(MainClass.getStrings().getString("sync_interrupted"));
	             updateMessage(String.format(MainClass.getStrings().getString("sync_cancelled_from_source_dest"), activeSod.getSource(),activeSod.getDestination() ));
	         }
			
		};
		
		return syncTask;
	}
	

	
	
}
