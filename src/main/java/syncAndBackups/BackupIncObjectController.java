package syncAndBackups;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import syncAndBackups.javafxUtils.Dialogs;
import syncAndBackups.javafxUtils.LocalDateTimeTextFieldListCell;
import syncAndBackups.models.BackupInc;


/**
 * Controller for BackupIncObject.fxml.
 * This view shows a SynOneDirection object, that represents source and destination folders for
 * a synchronization and additional information.
 * @author xsala
 *
 */
public class BackupIncObjectController {
	
	/**The instance of a BackupInc*/
	private BackupInc backupInc = null;

    @FXML
    private TextField backupDateTimeTF;

    @FXML
    private Button cancelBtn;

    @FXML
    private TextField destinationTF;

    @FXML
    private ListView<LocalDateTime> incrementalsLV;

    @FXML
    private TextArea lastInfoTA;

    @FXML
    private Button saveBtn;

    @FXML
    private TextField sourceTF;
    
    @FXML
    private Button restoreBtn;




	
	@FXML
	private void initialize() {
		sourceTF.setOnMouseClicked(me->Dialogs.openDirectoryChooser(me));
		destinationTF.setOnMouseClicked(me->Dialogs.openDirectoryChooser(me));
		cancelBtn.setOnMouseClicked(me-> cancelBtnPressed());
		saveBtn.setOnMouseClicked(me->saveBtnPressed());
		restoreBtn.setOnAction(ae->restoreBtnPressed());
		setIncrementalsLV();

	}
	
	
	
	private void restoreBtnPressed() {
		/*
		Stage stage = new Stage();
		stage.initModality(Modality.APPLICATION_MODAL);
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("RestoreBackupObject.fxml"), MainClass.getStrings());
		Parent restorePaneRoot= null;
		try {
			restorePaneRoot = fxmlLoader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		RestoreBackupObjectController restoreBackupObjectController = (RestoreBackupObjectController) fxmlLoader.getController();
		


		restoreBackupObjectController.setBackup(backup);

		
		
		stage.setScene(new Scene(restorePaneRoot));

		

		stage.show();*/
	}


	/**
	 * Returns BackupInc object, which represent a backup folder.
	 * @return the backupInc object associated at this controller.
	 */
	public BackupInc getBackupInc() {
		return backupInc;
	}
	
	/**
	 * Sets backupInc object to null and closes the stage.
	 * Setting backupInc to null, when the stage is closed, the item will
	 * be removed from the list of backupInc objects if it existed (A dialog confirmation will
	 * be opened to confirm the remove action).
	 * If it was a new creation, won't be created.
	 */
	private void cancelBtnPressed() {

		if (Dialogs.createDialogConfirmation(MainClass.getStrings().getString("want_to_delete"))) {
			backupInc = null;
			((Stage)cancelBtn.getScene().getWindow()).close();
		}

	}
	
	/**
	 * If source and destination textFields contains a folder, it will be saved
	 * to the backupInc object. After, the window will be closed.
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
		if (backupInc == null) {
			backupInc = new BackupInc(new File(sourceTF.getText()),new File(destinationTF.getText()));
		} else {
			backupInc.setDestination(new File(destinationTF.getText()));
			backupInc.setSource(new File(sourceTF.getText()));
		}
		((Stage)saveBtn.getScene().getWindow()).close();
		
	}
	
	/**
	 * Sets the differentialsLV (ListView). Just formats LocalDateTime and puts new incremental option at the end.
	 */
	private void setIncrementalsLV() {

		incrementalsLV.setCellFactory(param-> new TextFieldListCell<LocalDateTime>(new StringConverter<LocalDateTime>() {

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
		incrementalsLV.setCellFactory(param-> new LocalDateTimeTextFieldListCell());
	

	}

	/**
	 * Sets the backup object. If backupInc parameter isn't null, the fields of the UI will be set.
	 * @param backupInc
	 */
	public void setBackupInc(BackupInc backupInc) {
		this.backupInc = backupInc;
		if (backupInc != null) {
			sourceTF.setText(backupInc.getSource().toString());
			destinationTF.setText(backupInc.getDestination().toString());
			backupDateTimeTF.setText(backupInc.getFullBackup()  ==null? "": backupInc.getFullBackup().format(DateTimeFormatter.ofPattern(MainClass.DATE_TIME_PATTERN)));
			incrementalsLV.getItems().addAll(backupInc.getAdditionals());
			if (backupInc.getLastBackupInfo() != null) lastInfoTA.setText(backupInc.getLastBackupInfo());
		}
		
	}
	
}