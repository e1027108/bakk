package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import dto.ArgumentDto;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import logic.Argument;
import logic.Extension;
import logic.Framework;

public class DemonstrationWindowController {

	private static WrapperController wrapper;

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private Button backButton, nextButton, showAllButton, arrowButton, completeBtn, preferredBtn, stableBtn, groundedBtn, conflictFreeBtn, admissibleBtn;

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
		interactor = Interactor.getInstance(explanationArea); //TODO add parameters later (for nodes)
		readArguments(interactor.getRawArguments());

		/* --------------------------- testing purposes ------------------------------- */

      	ArrayList<Argument> arguments1 = new ArrayList<Argument>();
      	ArrayList<Argument> arguments2 = new ArrayList<Argument>();
      	ArrayList<Argument> arguments3 = new ArrayList<Argument>();
      	ArrayList<Argument> arguments4 = new ArrayList<Argument>();
      	ArrayList<Argument> arguments5 = new ArrayList<Argument>();
      	ArrayList<Argument> arguments6 = new ArrayList<Argument>();
      	ArrayList<Argument> arguments7 = new ArrayList<Argument>();
      	ArrayList<Argument> arguments8 = null;

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

    	// test seven is empty

    	// test eight is null
    	// TODO fix exceptions when getting null

    	/* --------------------------- /testing purposes ------------------------------ */

		argumentFramework = new Framework(arguments, interactor);
	}

	private void readArguments(ArrayList<ArgumentDto> rawArguments) {
		arguments = new ArrayList<Argument>();
		
		if(null != rawArguments && !rawArguments.isEmpty()){
			
			for(ArgumentDto a: rawArguments){
				arguments.add(new Argument(a.getName(), a.getStatement(), a.getAttacks()));
			}
		}
	}

	@FXML
	public void onConflictFreeClick(){
		interactor.emptyQueue();

		ArrayList<Extension> conflictFree = argumentFramework.getConflictFreeSets();

		//printExtensions(conflictFree);

		setUI();
	}

	@FXML
	public void onCompleteClick(){
		interactor.emptyQueue();

		ArrayList<Extension> complete = argumentFramework.getCompleteExtensions(previousCheckBox.isSelected());

		//printExtensions(complete);

		setUI();
	}

	@FXML
	public void onPreferredClick(){
		interactor.emptyQueue();

		ArrayList<Extension> preferred = argumentFramework.getPreferredExtensions(previousCheckBox.isSelected());

		//printExtensions(preferred);

		setUI();
	}

	@FXML
	public void onStableClick(){
		interactor.emptyQueue();

		ArrayList<Extension> stable = argumentFramework.getStableExtensions(previousCheckBox.isSelected());

		//printExtensions(stable);

		setUI();
	}

	@FXML
	public void onGroundedClick(){
		interactor.emptyQueue();

		Extension grounded = argumentFramework.getGroundedExtension(previousCheckBox.isSelected());

		/*if(grounded != null){
			System.out.println("{" + grounded.getArgumentNames() + "}");
		}*/

		setUI();
	}

	@FXML
	public void onAdmissibleClick(){
		interactor.emptyQueue();

		ArrayList<Extension> admissible = argumentFramework.getAdmissibleSets(previousCheckBox.isSelected());

		//printExtensions(admissible);

		setUI();
	}

	@FXML
	public void onBackClick(){
		interactor.removeLine();

		if(explanationArea.getText().isEmpty()){
			backButton.setDisable(true);
		}

		showAllButton.setDisable(false);
		nextButton.setDisable(false);
	}

	@FXML
	public void onNextClick(){
		interactor.printLine();

		backButton.setDisable(false);

		if(interactor.hasNext()){
			disableForwardButtons();
		}
	}

	@FXML
	public void onShowAllClick(){
		interactor.printAllLines();

		backButton.setDisable(false);

		if(interactor.hasNext()){
			disableForwardButtons();
		}
	}

	public void disableForwardButtons(){
		nextButton.setDisable(true);
		showAllButton.setDisable(true);
	}

	@FXML
	public void onArrowClick(){
		wrapper.loadMain();
	}

	public void setUI(){
		explanationArea.setText("");
		nextButton.setDisable(false);
		showAllButton.setDisable(false);
		interactor.printLine();
	}

	public void setArguments(ArrayList<ArgumentDto> arguments){
		for(ArgumentDto a: arguments){
			this.arguments.add(new Argument(a.getName(),"","")); //TODO replace strings with something that makes sense
		}

		System.out.println("erfolg");
	}

	public static void setWrapper(WrapperController wrapperController){
		wrapper = wrapperController;
	}
	
	//for testing
	public void printExtensions(ArrayList<Extension> ext){
		for(Extension e: ext){
			System.out.println("{" + e.getArgumentNames() + "}");
		}
	}
}
