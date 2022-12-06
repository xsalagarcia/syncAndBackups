package syncAndBackups;


import java.io.File;
import java.time.format.DateTimeFormatter;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
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
import syncAndBackups.javafxUtils.Dialogs;
import syncAndBackups.models.SyncOneDirection;

/**
 * Controller for SyncObject.fxml.
 * This view shows a SynOneDirection object, that represents source and destination folders for
 * a synchronization and additional information.
 * @author xsala
 *
 */
public class SyncObjectController {

	/**
	 * The object that represent a item from synchronization list.
	 */
	private SyncOneDirection syncOneDirection = null;

	
    @FXML
    private Button cancelBtn;

    @FXML
    private TextField destinationTF;

    @FXML
    private TextArea lastInfoTA;

    @FXML
    private TextField lastUpdatedTF;

    @FXML
    private Button saveBtn;

    @FXML
    private TextField sourceTF;
    
    /**
     * Constructor.
     * @param syncOneDirection if it isn't null, the values will be setted at textFields.
     */
	public void setSyncObjectOneDirection(SyncOneDirection syncOneDirection) {
		this.syncOneDirection = syncOneDirection;
		if (syncOneDirection != null) {
			destinationTF.setText(syncOneDirection.getDestination().toString());
			sourceTF.setText(syncOneDirection.getSource().toString());
			lastUpdatedTF.setText(syncOneDirection.getLastSync() == null ? "": syncOneDirection.getLastSync().format(DateTimeFormatter.ofPattern("dd/MM/YYYY - HH:mm:ss")));
			lastInfoTA.setText(syncOneDirection.getLastSyncInfo());
			
		}
	}
	
	@FXML
	private void initialize() {
		sourceTF.setOnMouseClicked(me->openDirectoryChooser(me));
		destinationTF.setOnMouseClicked(me->openDirectoryChooser(me));
		cancelBtn.setOnMouseClicked(me-> cancelBtnPressed());
		saveBtn.setOnMouseClicked(me->saveBtnPressed());
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
	 * Returns syncOneDirection object, which represent a synchronization folder.
	 * @return
	 */
	public SyncOneDirection getSyncOneDirection() {
		return syncOneDirection;
	}
	
	/**
	 * Sets syncOneDirection object to null and closes the stage.
	 * Setting syncOneDirection to null, when the stage is closed, the item will
	 * be removed from the list of syncOneDirection objects if it existed. If 
	 * it was a new creation, won't be created.
	 */
	private void cancelBtnPressed() {

		if (Dialogs.createDialogConfirmation("Do you want to delete this syncrhonization object?")) {
			syncOneDirection = null;
			((Stage)cancelBtn.getScene().getWindow()).close();
		}
		
		
	}
	
	/**
	 * If source and destination textFields contains a folder, it will be saved
	 * to the syncOneDirection object. After, the window will be closed.
	 * If source and or destination don't contain anything, just a popup notification.
	 */
	private void saveBtnPressed() {
		if (destinationTF.getText().length() == 0 || sourceTF.getText().length() == 0 || destinationTF.getText().equals(sourceTF.getText())) {
			Popup p = new Popup();
			Label l = new Label("There is no source and or destination selected. You have to select both.\nOr source and destination has to be different.");
			l.setTextFill(Color.WHITE);
			
			l.setBackground(new Background(new BackgroundFill(Color.BLACK, new CornerRadii(10), new Insets(-10))));

			p.getContent().add(new VBox(l));
			p.setAutoHide(true);
			
			p.show(saveBtn.getScene().getWindow());
			return;
		}
		if (syncOneDirection == null) {
			syncOneDirection = new SyncOneDirection(new File(sourceTF.getText()),new File(destinationTF.getText()));
		} else {
			syncOneDirection.setDestination(new File(destinationTF.getText()));
			syncOneDirection.setSource(new File(sourceTF.getText()));
		}
		((Stage)cancelBtn.getScene().getWindow()).close();
		
	}
	
}
