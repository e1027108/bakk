package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import logic.Argument;
import logic.Extension;
import logic.Framework;


public class DemonstrationWindowController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button backButton, completeBtn, preferredBtn, stableBtn, groundedBtn, conflictFreeBtn, admissibleBtn;
    // TODO implement back button
    
    @FXML
    private CheckBox previousCheckBox;

    @FXML
    private TextArea explanationArea;

    @FXML
    private AnchorPane graphPane;

    @FXML
    private AnchorPane root;
    
    private Framework argumentFramework;
    private ArrayList<Argument> arguments;
    private Interactor interactor;

    @FXML
    void initialize() {
      	interactor = new Interactor(explanationArea); //TODO add parameters later
    	
    	arguments = new ArrayList<Argument>();
        /* --------------------------- testing purposes ------------------------------- */
    	
    	// test one
    	arguments.add(new Argument('A',"A","B"));
    	arguments.add(new Argument('B',"B",""));
    	arguments.add(new Argument('C',"C","BD"));
    	arguments.add(new Argument('D',"D","CE"));
    	arguments.add(new Argument('E',"E","E"));
    	
    	/* test two
    	arguments.add(new Argument('A',"A","B"));
    	arguments.add(new Argument('B',"B","AC"));
    	arguments.add(new Argument('C',"C","DE"));
    	arguments.add(new Argument('D',"D","B"));
    	arguments.add(new Argument('E',"E","AF"));
    	arguments.add(new Argument('F',"F","G"));
    	arguments.add(new Argument('G',"G",""));
    	*/
    	
    	// test three
    	/*
    	arguments.add(new Argument('A',"A","B"));
    	arguments.add(new Argument('B',"B","ACE"));
    	arguments.add(new Argument('C',"C","D"));
    	arguments.add(new Argument('D',"D","B"));
    	arguments.add(new Argument('E',"E","BF"));
    	arguments.add(new Argument('F',"F",""));
    	arguments.add(new Argument('G',"G","FD"));
    	*/
    	
    	argumentFramework = new Framework(arguments, interactor);
    	
    	/* --------------------------- /testing purposes ------------------------------ */
    }
    
    @FXML
    public void onConflictFreeClick(){
    	//TODO implement user-friendly output in TextArea
    	ArrayList<Extension> conflictFree = argumentFramework.getConflictFreeSets();
    	
    	for(Extension e: conflictFree){
    		System.out.println("{" + e.getArgumentNames() + "}");
    	}
    }
    
    @FXML
    public void onCompleteClick(){
    	//TODO implement user-friendly output in TextArea
    	ArrayList<Extension> complete = argumentFramework.getCompleteExtensions(previousCheckBox.isSelected());
    	
    	for(Extension e: complete){
    		System.out.println("{" + e.getArgumentNames() + "}");
    	}
    }
    
    @FXML
    public void onPreferredClick(){
    	// TODO compute all preferred extensions
    	// TODO visualize for user (nodes and text)
    }
    
    @FXML
    public void onStableClick(){
    	//TODO implement user-friendly output in TextArea
    	ArrayList<Extension> stable = argumentFramework.getStableExtensions(previousCheckBox.isSelected());
    	
    	for(Extension e: stable){
    		System.out.println("{" + e.getArgumentNames() + "}");
    	}
    }
    
    @FXML
    public void onGroundedClick(){
    	// TODO compute the grounded extension
    	// TODO visualize for user (nodes and text)
    }
    
    @FXML
    public void onAdmissibleClick(){
    	//TODO implement user-friendly output in TextArea
    	ArrayList<Extension> admissible = argumentFramework.getAdmissibleSets(previousCheckBox.isSelected());
    	
    	for(Extension e: admissible){
    		System.out.println("{" + e.getArgumentNames() + "}");
    	}
    }
    
    // for testing
    private void printAttacks(){
    	for(Argument a: arguments){
    		String attackers = "";
    		for(Argument a2: argumentFramework.getAttackers(a.getName())){
    			attackers += a2.getName();
    		}
    		
    		System.out.println("" + a.getName() + " gets attacked by: " + attackers);
    	}
    }
    
}
