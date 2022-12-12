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
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
import syncAndBackups.gsonUtils.FileDeserializer;
import syncAndBackups.gsonUtils.FileSerializer;
import syncAndBackups.gsonUtils.LocalDateTimeJsonDeserializer;
import syncAndBackups.gsonUtils.LocalDateTimeJsonSerializer;
import syncAndBackups.models.Backup;


public class BackupPaneController {

	private Backup newBackup = new Backup(new File(MainClass.getStrings().getString("double_click_add_new")), null);
	
	private TextArea consoleTA;
	
    @FXML
    private TableView<Backup> backupTable;

    @FXML
    private TableColumn<Backup, String> differentialBackupCol;

    @FXML
    private Button backupBtn;

    @FXML
    private TableColumn<Backup, String> fullBackupCol;

    @FXML
    private TableColumn<Backup, Path> sourceCol;
    
	@FXML
	private void initialize() {
		setTable();
		loadList();
		backupTable.setOnMouseClicked(me->tableClicked(me));
		
		backupBtn.setOnAction(ae-> backupBtnPressed());

		//Add option will be always on last position.
		backupTable.getItems().addListener((ListChangeListener<Backup>)c->{
			if (backupTable.getItems().indexOf(newBackup) != backupTable.getItems().size()-1 && backupTable.getItems().indexOf(newBackup) >= 0 ) {
				backupTable.getItems().remove(newBackup);
				backupTable.getItems().add(newBackup);
				
			}
		});
	}
	
	private Object backupBtnPressed() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setConsole(TextArea consoleTA) {
		this.consoleTA = consoleTA;
	}
	
	/**
	 * Sets backupTable and its columns.
	 * The format for fullBackupCol and differentialBackupCol with a DateTimeFormatter, table with multiple selection.
	 */
	private void setTable() {
		
		sourceCol.setCellValueFactory(new PropertyValueFactory<Backup, Path>("source"));
		
		fullBackupCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Backup, String>, ObservableValue<String>>(){
			@Override
			public ObservableValue<String> call(CellDataFeatures<Backup, String> arg) {
				
				LocalDateTime ldt = arg.getValue().getFullBackup();
				
				String s = (ldt == null)? "": ldt.format(DateTimeFormatter.ofPattern(MainClass.DATE_TIME_PATTERN));
				
				return new ReadOnlyObjectWrapper<String>(s);

			}
		} );
		
		differentialBackupCol.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Backup, String>, ObservableValue<String>>(){
			@Override
			public ObservableValue<String> call(CellDataFeatures<Backup, String> arg) {
				
				LocalDateTime ldt = arg.getValue().getLastDifferential();
								
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
		File file = new File(System.getProperty("user.home") + "\\" + MainClass.PROGRAM_NAME +"\\" + Backup.LIST_OF_BACKUPS);
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
				Type listType = new TypeToken<ArrayList<Backup>>() {}.getType();
				List<Backup> list = gson.fromJson(new String(cbuf), listType );
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
			OutputStreamWriter osw= new OutputStreamWriter( new FileOutputStream(folder.toString()+ "\\" + Backup.LIST_OF_BACKUPS));
			String s = null;
			List<Backup> array=  backupTable.getItems().subList(0, backupTable.getItems().size()-1);
			
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
	private void openBackupWindow(Backup backup) {


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
					consoleTA.setText( backupObjectController.getBackup().toString());

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
	

}
