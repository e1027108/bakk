package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import logic.Argument;
import logic.Framework;


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
    
    private Framework argumentFramework;


    @FXML
    void initialize() {
        /* --------------------------- testing purposes ------------------------------- */
    	ArrayList<Argument> arguments = new ArrayList<Argument>();
    	
    	arguments.add(new Argument('A',"A","ABG"));
    	arguments.add(new Argument('B',"B","DI"));
    	arguments.add(new Argument('C',"C",""));
    	arguments.add(new Argument('D',"D","AE"));
    	arguments.add(new Argument('E',"E","ADF"));
    	arguments.add(new Argument('F',"F","DG"));
    	arguments.add(new Argument('G',"G","E"));
    	arguments.add(new Argument('H',"H","F"));
    	arguments.add(new Argument('I',"I",""));
    	
    	argumentFramework = new Framework(arguments);
    	
    	/* --------------------------- /testing purposes ------------------------------ */
    	
    	// TODO test framework and implement extensions
    }

}
