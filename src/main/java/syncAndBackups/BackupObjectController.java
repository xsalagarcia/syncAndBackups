package syncAndBackups;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import syncAndBackups.javafxUtils.Dialogs;
import syncAndBackups.models.Backup;



public class BackupObjectController {
	
	private Backup backup = null;

    @FXML
    private TextField backupDateTimeTF;

    @FXML
    private Button cancelBtn;

    @FXML
    private TextField destinationTF;

    @FXML
    private ListView<LocalDateTime> differentialsLV;

    @FXML
    private TextArea lastInfoTA;

    @FXML
    private Button saveBtn;

    @FXML
    private TextField sourceTF;




	
	@FXML
	private void initialize() {
		sourceTF.setOnMouseClicked(me->openDirectoryChooser(me));
		destinationTF.setOnMouseClicked(me->openDirectoryChooser(me));
		cancelBtn.setOnMouseClicked(me-> cancelBtnPressed());
		saveBtn.setOnMouseClicked(me->saveBtnPressed());
		setDifferentialsLV();

		if (backup != null) {
			destinationTF.setText(backup.getDestination().toString());
			sourceTF.setText(backup.getSource().toString());
			backupDateTimeTF.setText(backup.getFullBackup() == null ? "": backup.getFullBackup().format(DateTimeFormatter.ofPattern(MainClass.DATE_TIME_PATTERN)));
			lastInfoTA.setText(backup.getLastBackupInfo());
			differentialsLV.getItems().addAll(backup.getDifferentials());
			
			
		}
	}
	
	
	/**
	 * Opens a DirectoryChooser and puts the selection at the TextField that has been clicked when this
	 * function has been called.
	 * @param me MouseEvent that contains the information about the TextField clicked.
	 */
	private void openDirectoryChooser(MouseEvent me) {
		if (me.getButton().equals(MouseButton.PRIMARY)) {	
			DirectoryChooser dc = new DirectoryChooser();
			TextField tf = (TextField) me.getSource();
			if (tf.getText().length() > 0) {
				dc.setInitialDirectory(new File(tf.getText()));
			}
			
			File f = dc.showDialog(sourceTF.getScene().getWindow());
			if (f != null) {
				tf.setText(f.toString());
			}
		}
		
	}
	
	/**
	 * Returns backup object, which represent a backup folder.
	 * @return
	 */
	public Backup getBackup() {
		return backup;
	}
	
	/**
	 * Sets backup object to null and closes the stage.
	 * Setting backup to null, when the stage is closed, the item will
	 * be removed from the list of backup objects if it existed. If 
	 * it was a new creation, won't be created.
	 */
	private void cancelBtnPressed() {

		if (Dialogs.createDialogConfirmation(MainClass.getStrings().getString("want_to_delete"))) {
			backup = null;
			((Stage)cancelBtn.getScene().getWindow()).close();
		}

	}
	
	/**
	 * If source and destination textFields contains a folder, it will be saved
	 * to the backup object. After, the window will be closed.
	 * If source and or destination don't contain anything, just a popup notification.
	 */
	private void saveBtnPressed() {
		if (destinationTF.getText().length() == 0 || sourceTF.getText().length() == 0 || destinationTF.getText().equals(sourceTF.getText())) {
			Popup p = new Popup();
			Label l = new Label(MainClass.getStrings().getString("no_source_or_destination"));
			l.setTextFill(Color.WHITE);
			
			l.setBackground(new Background(new BackgroundFill(Color.BLACK, new CornerRadii(10), new Insets(-10))));

			p.getContent().add(new VBox(l));
			p.setAutoHide(true);
			
			p.show(saveBtn.getScene().getWindow());
			return;
		}
		if (backup == null) {
			backup = new Backup(new File(sourceTF.getText()),new File(destinationTF.getText()));
		} else {
			backup.setDestination(new File(destinationTF.getText()));
			backup.setSource(new File(sourceTF.getText()));
			backup.setFullBackup(LocalDateTime.now());
			
			backup.getDifferentials().add(LocalDateTime.now());
		}
		((Stage)saveBtn.getScene().getWindow()).close();
		
	}
	
	private void setDifferentialsLV() {
		
		
		
		differentialsLV.setCellFactory(param-> new TextFieldListCell<LocalDateTime>(new StringConverter<LocalDateTime>() {

			@Override
			public String toString(LocalDateTime object) {
				
				return object.format(DateTimeFormatter.ofPattern(MainClass.DATE_TIME_PATTERN));
			}

			@Override
			public LocalDateTime fromString(String string) {
					//not necessary
				return null;
			}
		}));
		

			

	}

	public void setBackup(Backup backup) {
		this.backup = backup;
		if (backup != null) {
			sourceTF.setText(backup.getSource().toString());
			destinationTF.setText(backup.getDestination().toString());
			backupDateTimeTF.setText(backup.getFullBackup()  ==null? "": backup.getFullBackup().format(DateTimeFormatter.ofPattern(MainClass.DATE_TIME_PATTERN)));
			differentialsLV.getItems().addAll(backup.getDifferentials());
		}
		
	}
	
}