package syncAndBackups.javafxUtils;

import java.io.File;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import syncAndBackups.MainClass;

public class Dialogs {
	public static boolean createDialogConfirmation(String message) {
		ButtonType btnOK = new ButtonType(MainClass.getStrings().getString("ok"));
		ButtonType btCancel = new ButtonType(MainClass.getStrings().getString("cancel"));
		Alert a = new Alert(AlertType.NONE, message, btnOK, btCancel);
		a.setTitle("Confirmar");
		a.setGraphic(SVGIcons.WARNING.getRegion(40, 40, Color.DARKRED));
		a.showAndWait();
		if (a.getResult() == btnOK) {
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * Opens a DirectoryChooser and puts the selection at the TextField that has been clicked when this
	 * function has been called.
	 * @param me MouseEvent that contains the information about the TextField clicked.
	 */
	public static void openDirectoryChooser(MouseEvent me) {
		if (me.getButton().equals(MouseButton.PRIMARY)) {	
			DirectoryChooser dc = new DirectoryChooser();
			TextField tf = (TextField) me.getSource();
			if (tf.getText().length() > 0) {
				dc.setInitialDirectory(new File(tf.getText()));
			}
			
			File f = dc.showDialog(tf.getScene().getWindow());
			if (f != null) {
				tf.setText(f.toString());
			}
		}
		
	}
	

	
}
