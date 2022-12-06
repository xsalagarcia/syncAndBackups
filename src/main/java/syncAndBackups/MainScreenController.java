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
import javafx.scene.layout.BorderPane;

/**
 * Controller class for MainScreen.fxml
 * @author xsala
 *
 */
public class MainScreenController { 

	private Parent syncPane;
	private SyncPaneController synchPaneController = null;
	
	private Parent backupPane;
	
    @FXML
    private ImageView exitImg;

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
		
		exitImg.setOnMouseClicked(me-> exitClicked());
		synchronizeBtn.setOnAction(ae-> loadSync());
		
		
	}
	
	private void exitClicked() {

		System.exit(0);
	}
	
	private void loadSync() {
		if (syncPane == null) {
			try {
				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("SyncPane.fxml"), MainClass.getStrings());
				syncPane = fxmlLoader.load();
				synchPaneController = (SyncPaneController) fxmlLoader.getController();
				synchPaneController.setConsole(consoleTA);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		rootBP.setCenter(syncPane);
	}
	
	public SyncPaneController getSyncPaneController() {
		return synchPaneController;
	}
	
	

}
