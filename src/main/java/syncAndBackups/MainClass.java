package syncAndBackups;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main class for starting the application. Contains some String constants and methods for string resources (internationalization).
 * @author xsala
 *
 */
public class MainClass extends Application {

	private static ResourceBundle stringsBundle;
	
	public static final String PROGRAM_NAME = "SyncAndBackups";
	public static final String DATE_TIME_PATTERN = "dd/MM/YYYY - HH:mm:ss";

	public static void main(String[] args) {
		setStrings();
		launch(args);

	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader fxmll = new FXMLLoader(getClass().getResource("MainScreen.fxml"), getStrings());
		Parent root = fxmll.load();
		
		primaryStage.setOnHidden(we->{
			if (((MainScreenController)fxmll.getController()).getSyncPaneController()!= null)
				((MainScreenController)fxmll.getController()).getSyncPaneController().saveList();
			if (((MainScreenController)fxmll.getController()).getBackupPaneController()!= null)
				((MainScreenController)fxmll.getController()).getBackupPaneController().saveList();
		});
		//https://www.youtube.com/watch?v=LMl_OZHJYC8&list=PLMBvOu-jQroC0YhlKS0VqsMuhh5jHtWtU&index=16
		//https://stackoverflow.com/questions/40753613/javafx-button-with-svg
		// min 8:30
		
		
		primaryStage.setScene(new Scene(root));
		primaryStage.show();
		
	}

	private static void setStrings() {
		Locale locale = Locale.getDefault();
		//Locale locale = new Locale("en");
		try {
			stringsBundle = ResourceBundle.getBundle("syncAndBackups.strings", locale); //it's necessary to indicate the package
		}catch (MissingResourceException e) {
			stringsBundle = ResourceBundle.getBundle("syncAndBackups.strings", new Locale("en"));
		}
	}
	
	public static ResourceBundle getStrings() {
		return stringsBundle;
	}
}
