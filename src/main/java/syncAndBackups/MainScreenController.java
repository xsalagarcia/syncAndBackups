package syncAndBackups;



import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

/**
 * Controller class for MainScreen.fxml.
 * The Main window of the application.
 * @author xsala
 *
 */
public class MainScreenController { 

	private Parent syncPane;

	private SyncPaneController synchPaneController = null;
	
	private Parent backupPane;
	private BackupPaneController backupPaneController = null;
	

    @FXML
    private VBox optionsLayout;

    @FXML
    private AnchorPane centerAP;
	
    @FXML
    private Button backupBtn;
    
    @FXML
    private Button synchronizeBtn;

    @FXML
    private Label menuL;

    @FXML
    private AnchorPane sliderAP;
    

    @FXML
    private BorderPane rootBP;
    

    @FXML
    private TextArea consoleTA;
	
	@FXML
	private void initialize() { //Will be called when the fxml will be loaded.
		

		synchronizeBtn.setOnAction(ae-> loadSync());
		backupBtn.setOnAction(ae->loadBackup());
		
		
	}
	

	private void loadSync() {
		changeSelectedOption(synchronizeBtn);

		if (syncPane == null) {
			try {
				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("SyncPane.fxml"), MainClass.getStrings());
				syncPane = fxmlLoader.load();
				synchPaneController = (SyncPaneController) fxmlLoader.getController();
				synchPaneController.setConsole(consoleTA);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		centerAP.getChildren().clear();
		AnchorPane.setTopAnchor(syncPane, 0.0);
		AnchorPane.setRightAnchor(syncPane, 0.0);
		AnchorPane.setLeftAnchor(syncPane, 0.0);
		AnchorPane.setBottomAnchor(syncPane, 0.0);
		
		centerAP.getChildren().add(syncPane);
		//rootBP.setCenter(syncPane);
	}
	
	private void loadBackup() {
		changeSelectedOption(backupBtn);
		if (backupPane == null) {
			try {
				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("BackupPane.fxml"), MainClass.getStrings());
				backupPane = fxmlLoader.load();
				backupPaneController = (BackupPaneController) fxmlLoader.getController();
				backupPaneController.setConsole(consoleTA);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		centerAP.getChildren().clear();
		AnchorPane.setTopAnchor(backupPane, 0.0);
		AnchorPane.setRightAnchor(backupPane, 0.0);
		AnchorPane.setLeftAnchor(backupPane, 0.0);
		AnchorPane.setBottomAnchor(backupPane, 0.0);
		centerAP.getChildren().clear();
		centerAP.getChildren().add(backupPane);
		//rootBP.setCenter(backupPane);
	}
	
	public SyncPaneController getSyncPaneController() {
		return synchPaneController;
	}
	public BackupPaneController getBackupPaneController() {
		return backupPaneController;
	}
	
	/**
	 * Changes background, text and icon color of the buttons (selected and non selected).
	 * @param selectedBtn The button selected.
	 */
	private void changeSelectedOption (Button selectedBtn) {
		optionsLayout.getChildren().filtered(button->!button.equals(selectedBtn)).forEach(button -> {
			Button b = (Button)button;
			b.setStyle("-fx-background-color :  transparent;");
			b.setTextFill(Color.WHITESMOKE);
			//synchronizeBtn.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
			b.setBackground(new Background(new BackgroundFill(Color.DARKSLATEBLUE, null, null)));
			((SVGPath) synchronizeBtn.getGraphic()).setFill(Color.WHITESMOKE);
			((SVGPath) synchronizeBtn.getGraphic()).setStroke(Color.WHITESMOKE);
		});
		selectedBtn.setStyle(null);
		selectedBtn.setTextFill(Color.DARKSLATEBLUE);
		//synchronizeBtn.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
		selectedBtn.setBackground(new Background(new BackgroundFill(Color.WHITESMOKE, null, null)));
		((SVGPath) selectedBtn.getGraphic()).setFill(Color.DARKSLATEBLUE);
		((SVGPath) selectedBtn.getGraphic()).setStroke(Color.DARKSLATEBLUE);
	}
	
	

}
