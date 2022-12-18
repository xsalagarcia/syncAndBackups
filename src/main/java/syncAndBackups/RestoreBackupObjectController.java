package syncAndBackups;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import syncAndBackups.javafxUtils.Dialogs;
import syncAndBackups.javafxUtils.LocalDateTimeTextFieldListCell;
import syncAndBackups.models.Backup;


/**
 * Controller class for RestoreBackupObject.fxml.
 * @author xsala
 *
 */
public class RestoreBackupObjectController {
	
	private Backup backup;

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
    	//TODO
    }
    
    
    /**
     * Sets a Backup object.
     * @param backup The backup object to set.
     */
    public void setBackup (Backup backup) {
    	this.backup = backup;
    	if (backup.getFullBackup() != null) dateTimesLV.getItems().add(backup.getFullBackup());
    	if (backup.getDifferentials() != null) dateTimesLV.getItems().addAll(backup.getDifferentials());
    	dateTimesLV.getItems().sort((o1, o2)->o1.compareTo(o2));
    	backupSourceTF.setText(backup.getDestination().toString());
    }
    
    

}

