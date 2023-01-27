package syncAndBackups;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import syncAndBackups.filesUtils.FileSyncAndBackupUtils;
import syncAndBackups.javafxUtils.Dialogs;
import syncAndBackups.javafxUtils.LocalDateTimeTextFieldListCell;
import syncAndBackups.models.BackupDif;


/**
 * Controller class for RestoreBackupObject.fxml.
 * RestoreBackupObject is a window which offers restoration to a given point with a backup object (total or differential backup).
 * @author xsala
 *
 */
public class RestoreBackupObjectController {
	
	private BackupDif backup;
	
	private Task<String> currentTask = null;

    @FXML
    private TextField backupSourceTF;

    @FXML
    private Button cancelBtn;

    @FXML
    private ListView<LocalDateTime> dateTimesLV;

    @FXML
    private TextField destinationTF;

    @FXML
    private TextArea restoreInfoTA;

    @FXML
    private Button okBtn;
    
    
    @FXML
    private void initialize() {

    	destinationTF.setOnMouseClicked(me->Dialogs.openDirectoryChooser(me));
    	cancelBtn.setOnMouseClicked(me-> cancelBtnPressed());
    	okBtn.setOnMouseClicked(me->okBtnPressed());
    	setDateTimesLV();
    }
    
    /**
     * Sets the {@code dateTimesLV} cell.
     */
    private void setDateTimesLV() {
		dateTimesLV.setCellFactory(param-> new LocalDateTimeTextFieldListCell());
    }
    
    /**
     * Called when cancelBtn is pressed. Just closes the window.
     */
    private void cancelBtnPressed() {
    	((Stage)cancelBtn.getScene().getWindow()).close();
    }
    
    /**
     * Called when okBtn is pressed.
     * Creates a thread for restore activity if there is some DateTime selected at {@code dateTimesLV}.
     * Turns the OK button to Stop Synchronization button.
     */
    private void okBtnPressed() {
    	    	
    	if (currentTask == null) {
	    	okBtn.setText(MainClass.getStrings().getString("stop_backup"));
			ProgressIndicator pi = new ProgressIndicator();
			pi.setMaxHeight(15);
			pi.setMaxWidth(15);
			okBtn.setGraphic(pi);
			
			Task<String> task = createRestoreTask();
			
			task.messageProperty().addListener((obs, oldV, newV)->restoreInfoTA.appendText(newV + System.lineSeparator()));
			
			task.setOnSucceeded(wse-> {
				restoreInfoTA.appendText( task.getValue() + System.lineSeparator());
				okBtn.setText(MainClass.getStrings().getString("ok"));
				okBtn.setGraphic(null);
				currentTask = null;
			});
	
			Thread t = new Thread(task);
			t.start();
			currentTask = task;
    	} else {
    		try {
				currentTask.wait();
				if (Dialogs.createDialogConfirmation(MainClass.getStrings().getString("want_to_stop_question"))) {
					currentTask.cancel();

				} else {
					currentTask.notify();
				}			
				
			} catch (InterruptedException e) {

				e.printStackTrace();
			}
			
			currentTask.cancel();
			okBtn.setGraphic(null);
			okBtn.setText(MainClass.getStrings().getString("ok"));
    	}
    }
    
    
    /**
     * Sets a Backup object.
     * @param backup The backup object to set.
     */
    public void setBackup (BackupDif backup) {
    	this.backup = backup;
    	if (backup.getFullBackup() != null) dateTimesLV.getItems().add(backup.getFullBackup());
    	if (backup.getAdditionals() != null) dateTimesLV.getItems().addAll(backup.getAdditionals());
    	dateTimesLV.getItems().sort((o1, o2)->o1.compareTo(o2));
    	backupSourceTF.setText(backup.getDestination().toString());
    }
    
	/**
	 * Creates a Task<String> for synchronization works.
	 * The task takes the selected items from syncTable and for each one, does the synchronization work.
	 * Update messages will be created for each synchronization work (start, incidences).
	 * At the end (onSucceed), the value will be "synchronization finished".
	 * @return The Task<String>
	 */
    private Task<String> createRestoreTask(){
    	LocalDateTime ldt = dateTimesLV.getSelectionModel().getSelectedItem();
    	
    	Task<String> task = new Task<String> () {

			@Override
			protected String call() throws Exception {
				updateMessage(String.format(MainClass.getStrings().getString("restoring_date_time"), destinationTF.getText(), ldt.format(DateTimeFormatter.ofPattern(MainClass.DATE_TIME_PATTERN))));
				String info;
				if (ldt.equals(backup.getFullBackup())) {
					info = FileSyncAndBackupUtils.totalCopy(backup.getDestination().toPath().resolve( new File(BackupDif.getFullBackupFolder(ldt)).toPath()), new File(destinationTF.getText()).toPath());
				} else {
					info = FileSyncAndBackupUtils.restoreWithDifferential(  new File(destinationTF.getText()).toPath(), backup.getDestination().toPath().resolve( new File(BackupDif.getAdditionalFolder(ldt)).toPath()), backup.getDestination().toPath().resolve( new File(BackupDif.getFullBackupFolder(backup.getFullBackup())).toPath()));
				}
				
				return info + System.lineSeparator()+ MainClass.getStrings().getString("restore_finished");
			}
			
	         @Override protected void cancelled() {
	             super.cancelled();
	             
	             updateMessage(MainClass.getStrings().getString("restore_canceled"));
	         }
    		
    	};
    	
    	return task;
    }
    
    

}

