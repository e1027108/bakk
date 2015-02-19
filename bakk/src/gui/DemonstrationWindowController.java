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
    private ArrayList<Argument> arguments; //TODO read stuff from input into here later
    private Interactor interactor;

    @FXML
    void initialize() {
      	interactor = new Interactor(explanationArea); //TODO add parameters later
    	
        /* --------------------------- testing purposes ------------------------------- */
      	
      	ArrayList<Argument> arguments1 = new ArrayList<Argument>();
      	ArrayList<Argument> arguments2 = new ArrayList<Argument>();
      	ArrayList<Argument> arguments3 = new ArrayList<Argument>();
      	ArrayList<Argument> arguments4 = new ArrayList<Argument>();
      	ArrayList<Argument> arguments5 = new ArrayList<Argument>();

    	// test one
    	arguments1.add(new Argument('A',"A","B"));
    	arguments1.add(new Argument('B',"B",""));
    	arguments1.add(new Argument('C',"C","BD"));
    	arguments1.add(new Argument('D',"D","CE"));
    	arguments1.add(new Argument('E',"E","E"));
    	
    	// test two
    	arguments2.add(new Argument('A',"A","B"));
    	arguments2.add(new Argument('B',"B","AC"));
    	arguments2.add(new Argument('C',"C","DE"));
    	arguments2.add(new Argument('D',"D","B"));
    	arguments2.add(new Argument('E',"E","AF"));
    	arguments2.add(new Argument('F',"F","G"));
    	arguments2.add(new Argument('G',"G",""));
    	
    	// test three
    	arguments3.add(new Argument('A',"A","B"));
    	arguments3.add(new Argument('B',"B","ACE"));
    	arguments3.add(new Argument('C',"C","D"));
    	arguments3.add(new Argument('D',"D","B"));
    	arguments3.add(new Argument('E',"E","BF"));
    	arguments3.add(new Argument('F',"F",""));
    	arguments3.add(new Argument('G',"G","FD"));
    	
    	// test four
    	arguments4.add(new Argument('A',"A","B"));
    	arguments4.add(new Argument('B',"B","A"));
    	arguments4.add(new Argument('C',"C","B"));
    	
    	// test five
    	arguments5.add(new Argument('A',"A","B"));
    	arguments5.add(new Argument('B',"B","AC"));
    	arguments5.add(new Argument('C',"C","B"));
    	
    	argumentFramework = new Framework(arguments1, interactor);
    	
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
    	//TODO implement user-friendly output in TextArea
    	ArrayList<Extension> preferred = argumentFramework.getPreferredExtensions(previousCheckBox.isSelected());
    	
    	for(Extension e: preferred){
    		System.out.println("{" + e.getArgumentNames() + "}");
    	}
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
    	//TODO implement user-friendly output in TextArea
    	Extension grounded = argumentFramework.getGroundedExtension(previousCheckBox.isSelected());
    	
    	System.out.println("{" + grounded.getArgumentNames() + "}");
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
