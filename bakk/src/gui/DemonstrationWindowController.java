package gui;

import interactor.GraphInstruction;
import interactor.Interactor;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import dto.ArgumentDto;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
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
	private Button backBtn, nextBtn, showAllBtn, resultsBtn, arrowBtn, completeBtn, preferredBtn, stableBtn, groundedBtn, conflictFreeBtn, admissibleBtn; //buttons in demonstration window

	@FXML
	private CheckBox previousCheckBox; //checkbox whether to use previously computed sets or extensions

	@FXML
	private TextArea explanationArea; //textArea describing every computation

	@FXML
	private AnchorPane root; //root pane containing all the UI elements

	@FXML
	private ChoiceBox<String> setsChoiceBox; //dropdown for

	private Tooltip conflictFreeTip, admissibleTip, completeTip, stableTip, preferredTip, groundedTip, previousTip, arrowTip, backTip, nextTip, allTip, resultsTip, choiceTip; //tooltips for all buttons etc

	private Framework argumentFramework; //argument framework containing the arguments
	private ArrayList<Argument> arguments; //arguments of the framework
	private Interactor interactor; //Interactor controlling the results the user sees
	private ArrayList<Extension> resultSet; //set containing computation results
	private NodePane graphPane; //pane where node illustrations are shown

	/**
	 * initializes the controller
	 * @details gets the interactor, reads arguments from it,
	 * 			creates Framework and Graph and sets tooltips
	 */
	@FXML
	void initialize() {
		setInitialValues();

		arrowTip = new Tooltip("Returns to input view.");
		arrowBtn.setTooltip(arrowTip);

		backTip = new Tooltip("Goes back a step of the computation.");
		backBtn.setTooltip(backTip);

		nextTip = new Tooltip("Shows the next step of the computation.");
		nextBtn.setTooltip(nextTip);

		allTip = new Tooltip("Shows all steps of the computation.");
		showAllBtn.setTooltip(allTip);

		resultsTip = new Tooltip("Shows the results of the computation, without showing any steps");
		resultsBtn.setTooltip(resultsTip);

		choiceTip = new Tooltip("Highlights the chosen set in the graph.");
		setsChoiceBox.setTooltip(choiceTip);

		previousTip = new Tooltip("If checked the program will not repeat computations it already has performed,"
				+ "\nbut instead use the previous computations' results for further computations.");
		previousCheckBox.setTooltip(previousTip);

		conflictFreeTip = new Tooltip("A set of arguments is conflict-free,\nif none of it's arguments attack another.\n\nClick to compute all conflict-free sets.");
		conflictFreeBtn.setTooltip(conflictFreeTip);

		admissibleTip = new Tooltip("A conflict-free set is an admissible extension,\nif it defends each of it's arguments.\n\nClick to compute all admissible extensions.");
		admissibleBtn.setTooltip(admissibleTip);

		completeTip = new Tooltip("An admissible extension is a complete extension,\nif it contains every argument it defends.\n\nClick to compute all complete extensions.");
		completeBtn.setTooltip(completeTip);

		stableTip = new Tooltip("A conflict-free set is a stable extension,\nif it attacks every argument it doesn't contain.\n\nClick to compute all stable extensions.");
		stableBtn.setTooltip(stableTip);

		preferredTip = new Tooltip("An admissible extension is a preferred extension,\nif it is not a subset of another admissible extension.\n\nClick to compute all preferred extensions.");
		preferredBtn.setTooltip(preferredTip);

		groundedTip = new Tooltip("The extension containing all arguments that all\ncomplete extensions have in common is the grounded extension.\n\nClick to compute the grounded extension.");
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
		resultSet = new ArrayList<Extension>();
		resultSet.add(grounded);

		//printExtensions(resultSet);

		setUI();
	}

	/**
	 * moves the output of the computation one step forward
	 */
	@FXML
	public void onNextClick(){
		interactor.executeNextCommand();

		backBtn.setDisable(false);

		if(!interactor.hasNext()){
			disableForwardButtons();
			showChoices();
		}
	}

	/**
	 * moves the output of the computation one step back
	 */
	@FXML
	public void onBackClick(){
		interactor.revertCommand();

		if(explanationArea.getText().isEmpty()){
			backBtn.setDisable(true);
		}

		resetChoices();
		showAllBtn.setDisable(false);
		nextBtn.setDisable(false);
		resultsBtn.setDisable(false);
	}

	/**
	 * shows the complete computation process in text
	 */
	@FXML
	public void onShowAllClick(){
		interactor.printAllLines();

		backBtn.setDisable(false);

		disableForwardButtons();
		showChoices();
	}

	/**
	 * instantly shows only the results, but prevents further use of forward and back buttons
	 */
	@FXML
	public void onResultsClick(){
		interactor.skipToLastCommand();
		interactor.emptyQueue();
		
		backBtn.setDisable(true);
		disableForwardButtons();
		showChoices();
	}

	/**
	 * disables the next and show all buttons
	 */
	public void disableForwardButtons(){
		nextBtn.setDisable(true);
		showAllBtn.setDisable(true);
		resultsBtn.setDisable(true);
	}

	/**
	 * returns to main input window
	 */
	@FXML
	public void onArrowClick(){
		explanationArea.setText("");
		resetChoices();

		wrapper.loadMain();
	}

	/**
	 * activates the choicebox (dropdown menu) that shows the extensions
	 * of the chosen type
	 */
	public void showChoices(){
		setsChoiceBox.setDisable(false);

		ArrayList<String> formatList = new ArrayList<String>();

		for(Extension e: resultSet){
			formatList.add(e.format());
		}

		setsChoiceBox.setItems(FXCollections.observableArrayList(formatList));
		setsChoiceBox.getSelectionModel().selectedIndexProperty().addListener(new ChoiceListener<Number>());
		setsChoiceBox.getSelectionModel().selectFirst();
	}
	
	/**
	 * the ChoiceListener listens for changes on a choicebox, and computes
	 * an instruction for the graph to show the newly chosen extension
	 * @author patrick.bellositz
	 * @param <Number> the index of the chosen element
	 */
	@SuppressWarnings("hiding")
	private class ChoiceListener<Number> implements ChangeListener<Number>{
		@Override
		public void changed(ObservableValue<? extends Number> oval, Number sval, Number nval){
			if((Integer) nval == -1){ //because with a new graph an empty selection (id:-1) is shown
				return;
			}
			
			Object item = setsChoiceBox.getItems().get((Integer) nval); 

			if(item instanceof String){
				GraphInstruction instruction = argumentFramework.getInstructionFromString((String) item);
				graphPane.executeInstruction(instruction);
			}
		}
	}
	
	/**
	 * removes all elements from the choicebox and deactivates it
	 */
	public void resetChoices(){
		setsChoiceBox.getItems().setAll(FXCollections.observableList(new ArrayList<String>()));
		setsChoiceBox.setDisable(true);
	}

	/**
	 * brings the UI into a state where viewing of the computation process is possible
	 */
	public void setUI(){
		resetChoices();
		explanationArea.setText("");
		backBtn.setDisable(true);
		nextBtn.setDisable(false);
		showAllBtn.setDisable(false);
		resultsBtn.setDisable(false);
		interactor.executeNextCommand();
	}

	/**
	 * sets the initial UI and data values that can be changed but not unchangable values
	 */
	public void setInitialValues() {		
		root.getChildren().remove(graphPane);
		graphPane = new NodePane();
		root.getChildren().add(graphPane);
		graphPane.setPrefHeight(470);
		graphPane.setPrefWidth(460);

		interactor = Interactor.getInstance(this);
		readArguments(interactor.getRawArguments());
		argumentFramework = new Framework(arguments, interactor);

		graphPane.createGraph(argumentFramework);
		graphPane.drawGraph();

		explanationArea.setText("");
		backBtn.setDisable(true);
		nextBtn.setDisable(true);
		showAllBtn.setDisable(true);
		resultsBtn.setDisable(true);
	}

	/**
	 * sets the wrapper for all windows to apply to this controller's window
	 * @param wrapperController the wrapper controlling which window is shown
	 */
	public static void setWrapper(WrapperController wrapperController){
		wrapper = wrapperController;
	}

	/**
	 * @return the text area used for result output
	 */
	public TextArea getTextArea(){
		return explanationArea;
	}

	/**
	 * @return the NodePane containing the graphical representation of the framework
	 */
	public NodePane getGraphPane(){
		return graphPane;
	}

	/**
	 * prints the arguments of all computed extensions (only for testing)
	 * @param ext the extensions to be printed
	 */
	public void printExtensions(ArrayList<Extension> ext){
		for(Extension e: ext){
			System.out.println(e.format());
		}
	}
}
