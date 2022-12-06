package syncAndBackups;

import java.util.Locale;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainClass extends Application {

	private static ResourceBundle stringsBundle;
	
	public static final String PROGRAM_NAME = "SyncAndBackups";

	public static void main(String[] args) {
		setStrings();
		launch(args);

	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader fxmll = new FXMLLoader(getClass().getResource("MainScreen.fxml"), getStrings());
		Parent root = fxmll.load();//FXMLLoader.load(getClass().getResource("MainScreen.fxml"));
		
		primaryStage.setOnHidden(we->{
			((MainScreenController)fxmll.getController()).getSyncPaneController().saveList();
		});
		//https://www.youtube.com/watch?v=LMl_OZHJYC8&list=PLMBvOu-jQroC0YhlKS0VqsMuhh5jHtWtU&index=16
		//https://stackoverflow.com/questions/40753613/javafx-button-with-svg
		// min 8:30
		
		
		primaryStage.setScene(new Scene(root));
		primaryStage.show();
		
	}

	private static void setStrings() {
		//Locale currentLocale = Locale.getDefault();
		Locale locale = new Locale("en");
		stringsBundle = ResourceBundle.getBundle("syncAndBackups.strings", locale); //it's necessary to indicate the package
	}
	
	public static ResourceBundle getStrings() {
		return stringsBundle;
	}
}
