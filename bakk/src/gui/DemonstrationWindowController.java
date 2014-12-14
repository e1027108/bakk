package gui;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;


public class DemonstrationWindowController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button backButton; //TODO implement while connecting views

    @FXML
    private TextArea explanationArea;

    @FXML
    private AnchorPane graphPane;

    @FXML
    private AnchorPane root;


    @FXML
    void initialize() {
        /* --------------------------- testing purposes ------------------------------- */
    	// contains test framework
    	
    	/* --------------------------- /testing purposes ------------------------------ */
    }

}
