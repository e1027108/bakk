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
    private Button backButton, nextButton, arrowButton, completeBtn, preferredBtn, stableBtn, groundedBtn, conflictFreeBtn, admissibleBtn;
    
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
      	ArrayList<Argument> arguments6 = new ArrayList<Argument>();

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
    	
    	// test six (nixon)
    	arguments6.add(new Argument('A',"A","B"));
    	arguments6.add(new Argument('B',"B","A"));
    	
    	argumentFramework = new Framework(arguments1, interactor);
    	
    	/* --------------------------- /testing purposes ------------------------------ */
    }
    
    @FXML
    public void onConflictFreeClick(){
    	ArrayList<Extension> conflictFree = argumentFramework.getConflictFreeSets();
    	
    	for(Extension e: conflictFree){
    		System.out.println("{" + e.getArgumentNames() + "}");
    	}
    }
    
    @FXML
    public void onCompleteClick(){
    	ArrayList<Extension> complete = argumentFramework.getCompleteExtensions(previousCheckBox.isSelected());
    	
    	for(Extension e: complete){
    		System.out.println("{" + e.getArgumentNames() + "}");
    	}
    }
    
    @FXML
    public void onPreferredClick(){
    	ArrayList<Extension> preferred = argumentFramework.getPreferredExtensions(previousCheckBox.isSelected());
    	
    	for(Extension e: preferred){
    		System.out.println("{" + e.getArgumentNames() + "}");
    	}
    }
    
    @FXML
    public void onStableClick(){
    	ArrayList<Extension> stable = argumentFramework.getStableExtensions(previousCheckBox.isSelected());
    	
    	for(Extension e: stable){
    		System.out.println("{" + e.getArgumentNames() + "}");
    	}
    }
    
    @FXML
    public void onGroundedClick(){
    	Extension grounded = argumentFramework.getGroundedExtension(previousCheckBox.isSelected());
    	
    	System.out.println("{" + grounded.getArgumentNames() + "}");
    }
    
    @FXML
    public void onAdmissibleClick(){
    	ArrayList<Extension> admissible = argumentFramework.getAdmissibleSets(previousCheckBox.isSelected());
    	
    	for(Extension e: admissible){
    		System.out.println("{" + e.getArgumentNames() + "}");
    	}
    }
    
    @FXML
    public void onBackClick(){
    	interactor.removeLine();
    }
    
    @FXML
    public void onNextClick(){
    	interactor.addLine();
    }
    
    @FXML
    public void onArrowClick(){
    	//TODO go back to MainInput
    	System.out.println("<");
    }
    
}
