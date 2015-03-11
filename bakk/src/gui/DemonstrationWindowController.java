package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import dto.ArgumentDto;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import logic.Argument;
import logic.Extension;
import logic.Framework;

/**
 * Controller controlling the DemonstrationWindow.fxml file behaviour
 * 	for demonstrating how abstract argumentation frameworks work
 * @author Patrick Bellositz
 */
public class DemonstrationWindowController {

	/**
	 * wrapper object controlling what is shown on screen
	 */
	private static WrapperController wrapper;

	@FXML
	private ResourceBundle resources; //resource bundle

	@FXML
	private URL location; //location of file

	@FXML
	private Button backBtn, nextBtn, showAllBtn, arrowBtn, completeBtn, preferredBtn, stableBtn, groundedBtn, conflictFreeBtn, admissibleBtn; //buttons in demonstration window

	@FXML
	private CheckBox previousCheckBox; //checkbox whether to use previously computed sets or extensions

	@FXML
	private TextArea explanationArea; //textArea describing every computation

	@FXML
	private AnchorPane graphPane; //pane where node illustrations are shown

	@FXML
	private AnchorPane root; //root pane containing all the UI elements
	
	private Tooltip conflictFreeTip, admissibleTip, completeTip, stableTip, preferredTip, groundedTip, previousTip, arrowTip, backTip, nextTip, allTip;

	private Framework argumentFramework; //argument framework containing the arguments
	private ArrayList<Argument> arguments; //arguments of the framework
	private Interactor interactor; //Interactor controlling the results the user sees
	private ArrayList<Extension> resultSet; //set containing computation results
	
	/**
	 * initializes the controller
	 * @details gets the interactor, reads arguments from it,
	 * 			creates the Framework and sets tooltips
	 */
	@FXML
	void initialize() {		
		interactor = Interactor.getInstance(explanationArea); //TODO add parameters later (for nodes)
		readArguments(interactor.getRawArguments());
		argumentFramework = new Framework(arguments, interactor);
		
		arrowTip = new Tooltip("Returns to input view.");
		arrowBtn.setTooltip(arrowTip);
		
		backTip = new Tooltip("Goes back a step of the computation.");
		backBtn.setTooltip(backTip);
		
		nextTip = new Tooltip("Shows the next step of the computation.");
		nextBtn.setTooltip(nextTip);
		
		allTip = new Tooltip("Shows all steps of the computation.");
		showAllBtn.setTooltip(allTip);
		
		previousTip = new Tooltip("If checked the program will not repeat computations it already has performed,"
				+ "\nbut instead use the previous computations' results for further computations.");
		previousCheckBox.setTooltip(previousTip);
		
		//TODO add text to following tooltips
		
		conflictFreeTip = new Tooltip("");
		conflictFreeBtn.setTooltip(conflictFreeTip);
		
		admissibleTip = new Tooltip("");
		admissibleBtn.setTooltip(admissibleTip);
		
		completeTip = new Tooltip("");
		completeBtn.setTooltip(completeTip);
		
		stableTip = new Tooltip("");
		stableBtn.setTooltip(stableTip);
		
		preferredTip = new Tooltip("");
		preferredBtn.setTooltip(preferredTip);
		
		groundedTip = new Tooltip("");
		groundedBtn.setTooltip(groundedTip);
	}

	/**
	 * converts Arguments from ArgumentDtos and saves them
	 * @param rawArguments a list of ArgumentDtos
	 */
	private void readArguments(ArrayList<ArgumentDto> rawArguments) {
		arguments = new ArrayList<Argument>();
		
		if(null != rawArguments && !rawArguments.isEmpty()){
			for(ArgumentDto a: rawArguments){
				arguments.add(new Argument(a.getName(), a.getStatement(), a.getAttacks()));
			}
		}
	}

	/**
	 * initiates the computation of the conflict free sets of the framework
	 */
	@FXML
	public void onConflictFreeClick() {
		interactor.emptyQueue();
		
		resultSet = argumentFramework.getConflictFreeSets();

		//printExtensions(resultSet);

		setUI();
	}
	
	/**
	 * initiates the computation of the admissible sets of the framework
	 */
	@FXML
	public void onAdmissibleClick(){
		interactor.emptyQueue();

		resultSet = argumentFramework.getAdmissibleSets(previousCheckBox.isSelected());

		//printExtensions(resultSet);

		setUI();
	}

	/**
	 * initiates the computation of the complete extensions of the framework
	 */
	@FXML
	public void onCompleteClick(){
		interactor.emptyQueue();

		resultSet = argumentFramework.getCompleteExtensions(previousCheckBox.isSelected());

		//printExtensions(resultSet);

		setUI();
	}

	/**
	 * initiates the computation of the preferred extensions of the framework
	 */
	@FXML
	public void onPreferredClick(){
		interactor.emptyQueue();

		resultSet = argumentFramework.getPreferredExtensions(previousCheckBox.isSelected());

		//printExtensions(resultSet);

		setUI();
	}

	/**
	 * initiates the computation of the stable extensions of the framework
	 */
	@FXML
	public void onStableClick(){
		interactor.emptyQueue();

		resultSet = argumentFramework.getStableExtensions(previousCheckBox.isSelected());

		//printExtensions(resultSet);

		setUI();
	}

	/**
	 * initiates the computation of the grounded extension of the framework
	 */
	@FXML
	public void onGroundedClick(){
		interactor.emptyQueue();

		Extension grounded = argumentFramework.getGroundedExtension(previousCheckBox.isSelected());

		/*if(grounded != null){
			System.out.println("{" + grounded.getArgumentNames() + "}");
		}*/

		setUI();
	}

	/**
	 * moves the output of the computation one step forward
	 */
	@FXML
	public void onNextClick(){
		interactor.printLine();

		backBtn.setDisable(false);

		if(interactor.hasNext()){
			disableForwardButtons();
		}
		
		//TODO show node change
	}
	
	/**
	 * moves the output of the computation one step back
	 */
	@FXML
	public void onBackClick(){
		interactor.removeLine();

		if(explanationArea.getText().isEmpty()){
			backBtn.setDisable(true);
		}

		showAllBtn.setDisable(false);
		nextBtn.setDisable(false);
		
		//TODO revert node change
	}

	/**
	 * shows the complete computation process in text
	 */
	@FXML
	public void onShowAllClick(){
		interactor.printAllLines();

		backBtn.setDisable(false);

		if(interactor.hasNext()){
			disableForwardButtons();
		}
	}

	/**
	 * disables the next and show all buttons
	 */
	public void disableForwardButtons(){
		nextBtn.setDisable(true);
		showAllBtn.setDisable(true);
	}

	/**
	 * returns to main input window
	 */
	@FXML
	public void onArrowClick(){
		wrapper.loadMain();
	}

	/**
	 * brings the UI into a state where viewing of the computation process is possible
	 */
	public void setUI(){
		explanationArea.setText("");
		nextBtn.setDisable(false);
		showAllBtn.setDisable(false);
		interactor.printLine();
	}

	/**
	 * sets the wrapper for all windows to apply to this controller's window
	 * @param wrapperController the wrapper controlling which window is shown
	 */
	public static void setWrapper(WrapperController wrapperController){
		wrapper = wrapperController;
	}
	
	/**
	 * prints the arguments of all computed extensions (only for testing)
	 * @param ext the extensions to be printed
	 */
	//TODO remove some time
	public void printExtensions(ArrayList<Extension> ext){
		for(Extension e: ext){
			System.out.println("{" + e.getArgumentNames() + "}");
		}
	}
}
