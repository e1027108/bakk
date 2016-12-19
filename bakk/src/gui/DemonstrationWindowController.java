package gui;

import interactor.GraphInstruction;
import interactor.Interactor;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;

import datacontainers.Example;
import datacontainers.Line;
import dto.ArgumentDto;
import exceptions.InvalidInputException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import logic.Argument;
import logic.Attack;
import logic.Extension;
import logic.Framework;

/**
 * Controller controlling the DemonstrationWindow.fxml file behaviour
 * 	for demonstrating how abstract argumentation frameworks work
 * @author Patrick Bellositz
 */
public class DemonstrationWindowController {

	//TODO later implement dynamic changing of frameworks (context menu?, predefined action?)

	/**
	 * wrapper object controlling what is shown on screen
	 */
	private static WrapperController wrapper;

	@FXML
	private ResourceBundle resources; //resource bundle

	@FXML
	private URL location; //location of file

	@FXML
	private Button backBtn, nextBtn, showAllBtn, resultsBtn, arrowBtn, computeBtn, toggleBtn, expandBtn; //buttons in demonstration window

	@FXML
	private CheckBox previousCheckBox; //checkbox whether to use previously computed sets or extensions

	@FXML
	private TextArea explanationArea; //textArea describing every computation

	@FXML
	private AnchorPane root; //root pane containing all the UI elements

	@FXML
	private ComboBox<String> setsComboBox, extensionComboBox, comparisonComboBox, expandingComboBox; //dropdown for result sets

	@FXML
	private Label extensionLbl, numberLbl, expandOptionsLbl;

	@FXML
	private ToggleGroup expansionGroup;

	@FXML
	private RadioButton strongRadio, normalRadio, weakRadio; //TODO use for expansion comparison

	private Tooltip conflictFreeTip, admissibleTip, completeTip, stableTip, preferredTip, groundedTip, previousTip, arrowTip, 
	backTip, nextTip, allTip, resultsTip, choiceTip, extensionTip; //tooltips for all buttons etc

	//TODO implement expanding of frameworks
	private Framework argumentFramework, comparisonFramework, expArgFramework, expComFramework; //argument framework containing the arguments
	private ArrayList<Argument> arguments, compArguments, expArguments; //arguments of the framework
	private ArrayList<Attack> attacks, compAttacks, expAttacks; //attacks of the framework
	private Interactor interactor, comparisonInteractor; //Interactor controlling the results the user sees
	private ArrayList<Extension> resultSet; //set containing computation results
	private NodePane graphPane, comparisonPane; //pane where node illustrations are shown
	private boolean expanded; //whether we check extended frameworks

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
		setsComboBox.setTooltip(choiceTip);

		previousTip = new Tooltip("If checked the program will not repeat computations it already has performed,"
				+ "\nbut instead use the previous computations' results for further computations.");
		previousCheckBox.setTooltip(previousTip);

		extensionTip = new Tooltip("Choose the type of extension semantics you want to compute!");
		extensionComboBox.setTooltip(extensionTip);
		extensionLbl.setTooltip(extensionTip);

		/* use on hover over option in dropdown
		conflictFreeTip = new Tooltip("A set of arguments is conflict-free,\nif none of it's arguments attack another.\n\nClick to compute all conflict-free sets.");

		admissibleTip = new Tooltip("A conflict-free set is an admissible extension,\nif it defends each of it's arguments.\n\nClick to compute all admissible extensions.");

		completeTip = new Tooltip("An admissible extension is a complete extension,\nif it contains every argument it defends.\n\nClick to compute all complete extensions.");

		stableTip = new Tooltip("A conflict-free set is a stable extension,\nif it attacks every argument it doesn't contain.\n\nClick to compute all stable extensions.");

		preferredTip = new Tooltip("An admissible extension is a preferred extension,\nif it is not a subset of another admissible extension.\n\nClick to compute all preferred extensions.");

		groundedTip = new Tooltip("The extension containing all arguments that all\ncomplete extensions have in common is the grounded extension.\n\nClick to compute the grounded extension.");
		 */
	}

	/**
	 * sets the initial UI and data values
	 */
	public void setInitialValues() {
		numberLbl.setText("");

		root.getChildren().remove(graphPane);
		graphPane = new NodePane();
		root.getChildren().add(graphPane);
		graphPane.setPrefHeight(470);
		graphPane.setPrefWidth(445);
		graphPane.setLayoutX(15); //prevents arcs from going out of visual bounds, y stays 0
		graphPane.setVisible(true);

		//for testing
		//toggleBtn.setDisable(false);

		interactor = Interactor.getInstance(this);
		readArguments(interactor.getRawArguments());
		argumentFramework = new Framework(arguments, attacks, interactor);

		graphPane.createGraph(argumentFramework);

		try {
			graphPane.drawGraph();
			explanationArea.setText("");
			explanationArea.setStyle("-fx-text-fill: black;");
		} catch (InvalidInputException e) {
			interactor.emptyQueue();
			explanationArea.setText(e.getMessage() + "\n The graph may not be correctly displayed!");
			explanationArea.setStyle("-fx-text-fill: red;");
		}

		backBtn.setDisable(true);
		nextBtn.setDisable(true);
		showAllBtn.setDisable(true);
		resultsBtn.setDisable(true);

		//set now the values for extension box
		String[] extarr = new String[]{"","conflict-free","admissible","complete","preferred","stable","grounded"};
		ArrayList<String> extensionTypes = new ArrayList<String>();
		extensionTypes.addAll(Arrays.asList(extarr));
		extensionComboBox.setItems(FXCollections.observableArrayList(extensionTypes));

		//set comparable examples
		showExamplesInComboBoxes();
		expanded = false;

		toggleDisableRadioButtons();
	}

	//needs lbl and all buttons to be set to the same disable status in fxml file
	private void toggleDisableRadioButtons() {
		expandOptionsLbl.setDisable(!expandOptionsLbl.isDisabled());
		
		for(int i = 0;i<expansionGroup.getToggles().size();i++){
			Object tmp = expansionGroup.getToggles().get(i);

			if(tmp instanceof RadioButton){
				((RadioButton) tmp).setDisable(!((RadioButton) tmp).isDisabled());
			}
		}
	}

	/**
	 * converts Arguments from ArgumentDtos and saves them
	 * @param rawArguments a list of ArgumentDtos
	 */
	private void readArguments(ArrayList<ArgumentDto> rawArguments) {
		arguments = new ArrayList<Argument>();
		attacks = new ArrayList<Attack>();

		for(ArgumentDto a: rawArguments){
			arguments.add(new Argument(a.getName(),a.getStatement()));
		}

		for(ArgumentDto a: rawArguments){
			char attackString[] = a.getAttacks().toCharArray();
			for(char att: attackString){
				attacks.add(new Attack(getArgument(arguments,a.getName()),getArgument(arguments,att)));
			}
		}
	}

	private Argument getArgument(ArrayList<Argument> args, char att) {
		for(Argument a: args){
			if(a.getName() == att){
				return a; 
			}
		}

		return null;
	}

	/**
	 * initiates the computation of the conflict free sets of the framework
	 */
	public void conflictFreeComputation() {
		interactor.emptyQueue();

		resultSet = argumentFramework.getConflictFreeSets();

		//printExtensions(resultSet);

		setUI();
	}

	/**
	 * initiates the computation of the admissible sets of the framework
	 */
	public void admissibleComputation(){
		interactor.emptyQueue();

		resultSet = argumentFramework.getAdmissibleExtensions(previousCheckBox.isSelected());

		//printExtensions(resultSet);

		setUI();
	}

	/**
	 * initiates the computation of the complete extensions of the framework
	 */
	public void completeComputation(){
		interactor.emptyQueue();

		try {
			resultSet = argumentFramework.getCompleteExtensions(previousCheckBox.isSelected());
			//printExtensions(resultSet);
			setUI();
		} catch (InvalidInputException e) {
			interactor.emptyQueue();
			explanationArea.setText(e.getMessage() + " Extension could not be computed!");
			explanationArea.setStyle("-fx-text-fill: red;");
		}
	}

	/**
	 * initiates the computation of the preferred extensions of the framework
	 */
	public void preferredComputation(){
		interactor.emptyQueue();

		resultSet = argumentFramework.getPreferredExtensions(previousCheckBox.isSelected());

		//printExtensions(resultSet);

		setUI();
	}

	/**
	 * initiates the computation of the stable extensions of the framework
	 */
	public void stableComputation(){
		interactor.emptyQueue();

		resultSet = argumentFramework.getStableExtensions(previousCheckBox.isSelected());

		//printExtensions(resultSet);

		setUI();
	}

	/**
	 * initiates the computation of the grounded extension of the framework
	 */
	public void groundedComputation(){
		interactor.emptyQueue();

		Extension grounded;
		try {
			grounded = argumentFramework.getGroundedExtension(previousCheckBox.isSelected());
			resultSet = new ArrayList<Extension>();
			resultSet.add(grounded);
			//printExtensions(resultSet);
			setUI();
		} catch (InvalidInputException e) {
			interactor.emptyQueue();
			explanationArea.setText(e.getMessage() + " Extension could not be computed!");
			explanationArea.setStyle("-fx-text-fill: red;");
		}
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
			showSetChoices();
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

		resetSetChoices();
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
		showSetChoices();
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
		showSetChoices();
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
		resetSetChoices();

		wrapper.loadMain();
	}

	/**
	 * activates the choicebox (dropdown menu) that shows the extensions
	 * of the chosen type
	 */
	public void showSetChoices(){
		setsComboBox.setDisable(false);

		ArrayList<String> formatList = new ArrayList<String>();

		for(Extension e: resultSet){
			formatList.add(e.format());
		}

		setsComboBox.setItems(FXCollections.observableArrayList(formatList));
		setsComboBox.getSelectionModel().selectedIndexProperty().addListener(new SetsChoiceListener<Number>());
		setsComboBox.getSelectionModel().selectFirst();
	}

	/**
	 * removes all elements from the choicebox and deactivates it
	 */
	public void resetSetChoices(){
		setsComboBox.getItems().setAll(FXCollections.observableList(new ArrayList<String>()));
		setsComboBox.setDisable(true);
	}

	/**
	 * the ChoiceListener listens for changes on a choicebox, and computes
	 * an instruction for the graph to show the newly chosen extension
	 * @author patrick.bellositz
	 * @param <Number> the index of the chosen element
	 */
	@SuppressWarnings("hiding")
	private class SetsChoiceListener<Number> implements ChangeListener<Number>{
		@Override
		public void changed(ObservableValue<? extends Number> oval, Number sval, Number nval){
			if((Integer) nval == -1){ //because with a new graph an empty selection (id:-1) is shown
				return;
			}

			Object item = setsComboBox.getItems().get((Integer) nval); 

			if(item instanceof String){
				GraphInstruction instruction = argumentFramework.getInstructionFromString((String) item);
				try {
					graphPane.executeInstruction(instruction);
				} catch (InvalidInputException e) {
					interactor.emptyQueue();
					explanationArea.setText(e.getMessage() + "\n The graph may not be correctly displayed!");
					explanationArea.setStyle("-fx-text-fill: red;");
				}
			}
		}
	}

	@FXML
	public void onComputeClick(){
		int exChoice = extensionComboBox.getSelectionModel().getSelectedIndex();

		switch(exChoice){
		case 0:
			explanationArea.setText("No extension type was chosen, no computation performed!");
			break;
		case 1:
			conflictFreeComputation();
			break;
		case 2:
			admissibleComputation();
			break;
		case 3:
			completeComputation();
			break;
		case 4:
			preferredComputation();
			break;
		case 5:
			stableComputation();
			break;
		case 6:
			groundedComputation();
			break;
		default:
			explanationArea.setText("Invalid extension type was chosen, no computation performed!");
			break;
		}
	}

	@FXML
	public void onCompareClick(){
		//TODO implement equivalency comparison
	}

	//TODO add changelistener so that when expansion framework is chosen, radiobuttons are enabled
	@FXML
	public void onExpandClick(){
		if(!expanded){
			expanded = true;
			expandBtn.setText("Unexpand");
			extendFrameworks();
		}
		else{
			expanded = false;
			expandBtn.setText("Expand");
			restoreOriginalFrameworks();
		}
	}

	private void restoreOriginalFrameworks() {
		// TODO Auto-generated method stub

	}

	private void extendFrameworks() {
		// TODO Auto-generated method stub

	}

	@FXML
	public void onToggleClick(){
		if(graphPane.isVisible()){
			graphPane.setVisible(false);
			comparisonPane.setVisible(true);
			numberLbl.setText("B");
		}
		else{
			graphPane.setVisible(true);
			comparisonPane.setVisible(false);
			numberLbl.setText("A");
		}
	}

	private void fillComparisonPane(){
		boolean wasVisible;

		if(comparisonPane != null && comparisonPane.isVisible()){
			wasVisible = true;
		}
		else{
			wasVisible = false;
		}

		root.getChildren().remove(comparisonPane);

		//initialize new comparison pane
		comparisonPane = new NodePane();
		root.getChildren().add(comparisonPane);
		comparisonPane.setPrefHeight(470);
		comparisonPane.setPrefWidth(445);
		comparisonPane.setLayoutX(15);
		comparisonPane.setVisible(wasVisible);

		Example current = MainInputController.getExamples().get(comparisonComboBox.getSelectionModel().getSelectedIndex());

		//testing
		System.out.println(current.getName());

		compArguments = new ArrayList<Argument>();
		compAttacks = new ArrayList<Attack>();

		for(Line l: current.getLines()){
			compArguments.add(new Argument(l.getChar(),l.getDescription()));
		}

		for(Line l: current.getLines()){
			Argument attacker = getArgumentByName(l.getChar(),compArguments);
			for(int i = 0;i<l.getAttacks().length();i++){
				Argument defender = getArgumentByName(l.getAttacks().charAt(i),compArguments);
				if(defender == null){
					explanationArea.setText("Illegal attack detected!");
					return;
				}
				compAttacks.add(new Attack(attacker,defender));
			}
		}

		comparisonFramework = new Framework(compArguments, compAttacks, comparisonInteractor);

		comparisonPane.createGraph(comparisonFramework);
		toggleBtn.setDisable(false);
		numberLbl.setText("A");

		//TODO write comparisonInteractor?

		try {
			comparisonPane.drawGraph();
		} catch (InvalidInputException e) {
			explanationArea.setText("Could not load comparison!");
			return;
		}
	}

	private Argument getArgumentByName(char name, ArrayList<Argument> args){
		for(Argument a: args){
			if(String.valueOf(name).toUpperCase().equals(String.valueOf(a.getName()))){
				return a;
			}
		}

		System.out.println("this");
		return null;
	}

	/**
	 * brings the UI into a state where viewing of the computation process is possible
	 */
	public void setUI(){
		resetSetChoices();
		explanationArea.setText("");
		explanationArea.setStyle("-fx-text-fill: black;");
		backBtn.setDisable(true);
		nextBtn.setDisable(false);
		showAllBtn.setDisable(false);
		resultsBtn.setDisable(false);
		interactor.executeNextCommand();
	}

	public void showExamplesInComboBoxes(){
		ArrayList<String> formatList = new ArrayList<String>();

		for(Example e: MainInputController.getExamples()){
			formatList.add(e.getName());
		}

		comparisonComboBox.setItems(FXCollections.observableArrayList(formatList));
		comparisonComboBox.getSelectionModel().selectFirst();
		comparisonComboBox.getSelectionModel().selectedIndexProperty().addListener(new ComparisonChoiceListener<Number>());

		expandingComboBox.setItems(FXCollections.observableArrayList(formatList));
		expandingComboBox.getSelectionModel().selectFirst();
		expandingComboBox.getSelectionModel().selectedIndexProperty().addListener(new ComparisonChoiceListener<Number>());
	}

	/**
	 * the ChoiceListener listens for changes on a combobox, and executes fill comparison to fill the second comparison framework
	 * @author patrick.bellositz
	 * @param <Number> the index of the chosen element
	 */
	@SuppressWarnings("hiding")
	private class ComparisonChoiceListener<Number> implements ChangeListener<Number>{
		@Override
		public void changed(ObservableValue<? extends Number> oval, Number sval, Number nval){
			if((Integer) nval == -1){ //because with a new graph an empty selection (id:-1) is shown
				return;
			}
			else if((Integer) nval == 0){
				if(!comparisonPane.isDisabled()){
					onToggleClick();
				}
				toggleBtn.setDisable(true);
				numberLbl.setText("");
			}
			else{
				fillComparisonPane();
			}
		}
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

	public ArrayList<Example> getExampleList(){
		return MainInputController.getExamples();
	}
}
