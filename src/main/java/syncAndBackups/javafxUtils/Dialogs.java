package syncAndBackups.javafxUtils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.paint.Color;
import javafx.scene.control.Alert.AlertType;
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
}
