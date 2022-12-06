package syncAndBackups;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class BackupPaneController {

    @FXML
    private TableView<?> backupTable;

    @FXML
    private TableColumn<?, ?> differentialBackupCol;

    @FXML
    private TableColumn<?, ?> fullBackupCol;

    @FXML
    private TableColumn<?, ?> sourceCol;

    @FXML
    private Button syncBtn;

}
