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
import logic.Equivalency;
import logic.ExpandedEquivalency;
import logic.Extension;
import logic.Framework;
import logic.Framework.Type;

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
	private Button backBtn, nextBtn, showAllBtn, resultsBtn, arrowBtn, computeBtn, toggleBtn, expandBtn, checkBtn; //buttons in demonstration window

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

	//TODO implement expanding of frameworks, maybe an extension of the framework class?
	private Framework argumentFramework, comparisonFramework, expansionFramework; //argument framework containing the arguments
	private ArrayList<Argument> arguments, compArguments, expArguments; //arguments of the framework
	private ArrayList<Attack> attacks, compAttacks, expAttacks; //attacks of the framework
	private Interactor interactor; //Interactor controlling the results the user sees //TODO check if one interactor for multiple frameworks suffices
	private ArrayList<Extension> resultSet; //set containing computation results
	private NodePane graphPane, comparisonPane; //pane where node illustrations are shown
	private boolean expanded; //whether we check extended frameworks
	private Equivalency eq; //this computes equivalencies //TODO make create a list of previously compared frameworks to not have to compute stuff again

	private static final String EXPANDED = "Unexpand";

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
		setDisableRadioButtons(true);

		//set now the values for extension box
		String[] extarr = new String[]{"","conflict-free","admissible","complete","preferred","stable","grounded","semi-stable"};
		ArrayList<String> extensionTypes = new ArrayList<String>();
		extensionTypes.addAll(Arrays.asList(extarr));
		extensionComboBox.setItems(FXCollections.observableArrayList(extensionTypes));

		//set comparable examples
		showExamplesInComboBoxes();
		expanded = false;
	}

	//needs lbl and all buttons to be set to the same disable status in fxml file
	private void setDisableRadioButtons(boolean disabled) {
		for(int i = 0;i<expansionGroup.getToggles().size();i++){
			Object tmp = expansionGroup.getToggles().get(i);

			if(tmp instanceof RadioButton){
				((RadioButton) tmp).setDisable(disabled);
			}
		}
		
		expandOptionsLbl.setDisable(disabled);
		checkBtn.setDisable(disabled);
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

	public void semiStableComputation(){
		interactor.emptyQueue();

		resultSet = argumentFramework.getSemiStableExtensions(previousCheckBox.isSelected());

		printExtensions(resultSet);

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
		case 7:
			semiStableComputation();
			break;
		default:
			explanationArea.setText("Invalid extension type was chosen, no computation performed!");
			break;
		}
	}

	// for two discrete frameworks (optionally with a discrete expansion), not for general expansion equivalency
	@FXML
	public void onCompareClick(){
		javafx.scene.control.SingleSelectionModel<String> selExt, selCom, selExp;
		selExt = extensionComboBox.getSelectionModel();
		selCom = comparisonComboBox.getSelectionModel();
		selExp = expandingComboBox.getSelectionModel();
		boolean expanded = false;
		eq = new Equivalency(argumentFramework, comparisonFramework, interactor); //TODO separate interactor?

		if(selExt.isEmpty() || selExt.getSelectedIndex() <= 0){
			explanationArea.setText("Can not compare frameworks, since no semantics for comparison was chosen.");
			return;
		}
		else if(selCom.isEmpty() || selCom.getSelectedIndex() <= 0){
			explanationArea.setText("Can not compare frameworks, since no comparison framework was chosen.");
			return;
		}
		else if(expandBtn.getText().equals(EXPANDED)){
			expanded = true;
		}

		//if we are here, it is possible to compare something
		if(!expanded){
			boolean stdEquiv = false;
			try {
				stdEquiv = eq.areStandardEquivalent(extensionComboBox.getSelectionModel().getSelectedIndex(),previousCheckBox.isSelected());
				System.out.println(stdEquiv);
			} catch (InvalidInputException e) {
				explanationArea.setText(e.getMessage());
			}
		}
		else{
			//TODO ensure interactor accesses expanded panes
			boolean expEquiv = false;

			eq = new ExpandedEquivalency(argumentFramework, comparisonFramework, expansionFramework, interactor);

			try {
				expEquiv = ((ExpandedEquivalency) eq).areExpandedEquivalent(extensionComboBox.getSelectionModel().getSelectedIndex(),previousCheckBox.isSelected());
				System.out.println(expEquiv);
			} catch (InvalidInputException e) {
				explanationArea.setText(e.getMessage());
			}

			//testing
			if(expEquiv){
				explanationArea.setText("true");
			}
			else{
				explanationArea.setText("false");
			}
		}
	}

	@FXML
	public void onExpandClick(){
		if(!expanded){
			expanded = true;
			expandBtn.setText(EXPANDED);
			expandFrameworks();
		}
		else{
			expanded = false;
			expandBtn.setText("Expand");
			restoreOriginalFrameworks();
		}
	}

	private void restoreOriginalFrameworks() {
		// TODO load standard version into pane from data
	}

	//TODO account for case when only one framework is shown! (no button for this or just expand one ... uselessly)
	//TODO expand even if this button is clicked before the comparisonframework is chosen
	private void expandFrameworks() {
		//TODO expand frameworks for panes
		//something here

		//TODO load expansion into framework
		//TODO outsource parts?
		Example current = MainInputController.getExamples().get(expandingComboBox.getSelectionModel().getSelectedIndex());

		expArguments = new ArrayList<Argument>();
		expAttacks = new ArrayList<Attack>();

		for(Line l: current.getLines()){
			expArguments.add(new Argument(l.getChar(),l.getDescription()));
		}

		for(Line l: current.getLines()){
			Argument attacker = getArgumentByName(l.getChar(),expArguments);
			for(int i = 0;i<l.getAttacks().length();i++){
				Argument defender = getArgumentByName(l.getAttacks().charAt(i),expArguments);
				if(defender == null){
					explanationArea.setText("Illegal attack detected!");
					return;
				}
				compAttacks.add(new Attack(attacker,defender));
			}
		}

		//TODO other interactor
		expansionFramework = new Framework(expArguments, expAttacks, interactor);
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

	@FXML
	public void onCheckClick(){
		Equivalency eq;
		boolean equiv, usePrevious;
		usePrevious = previousCheckBox.isSelected();

		if(!expandBtn.getText().equals(EXPANDED)){
			eq = new Equivalency(argumentFramework,comparisonFramework,interactor);
		}
		else{
			eq = new ExpandedEquivalency(argumentFramework,comparisonFramework,expansionFramework,interactor);
		}

		//we are quite certain that the group only contains radiobuttons
		//TODO complete
		switch(((RadioButton) expansionGroup.getSelectedToggle()).getText()){
		case "strong":
			try {
				equiv = eq.checkStrongExpansionEquivalency(idToType(comparisonComboBox.getSelectionModel().getSelectedIndex()), usePrevious);
			} catch (InvalidInputException e) {
				explanationArea.setText(e.getMessage());
			}
			break;
		case "normal":
			equiv = eq.checkNormalExpansionEquivalency(idToType(comparisonComboBox.getSelectionModel().getSelectedIndex()), usePrevious);
			break;
		case "weak":
			equiv = eq.checkWeakExpansionEquivalency(idToType(comparisonComboBox.getSelectionModel().getSelectedIndex()), usePrevious);
			break;
		default:
			explanationArea.setText("No equivalency type chosen!");
			return;
		}

		//TODO do something with equiv
	}

	private Type idToType(int id) {
		switch(id){
		case 1:
			return Type.cf;
		case 2:
			return Type.ad;
		case 3:
			return Type.co;
		case 4:
			return Type.pr;
		case 5:
			return Type.st;
		case 6:
			return Type.gr;
		case 7:
			return Type.ss;
		default:
			return null;
		}
	}

	private void initializeComparisonResources(){	
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
		//System.out.println(current.getName());

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

		comparisonFramework = new Framework(compArguments, compAttacks, interactor);

		comparisonPane.createGraph(comparisonFramework);
		toggleBtn.setDisable(false);
		numberLbl.setText("A");

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
				setDisableRadioButtons(true);
				//TODO empty comparisonPane!!!
			}
			else{
				setDisableRadioButtons(false);
				initializeComparisonResources();
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
		if(ext.size() == 0){
			System.out.println("no extensions availiable");
		}

		for(Extension e: ext){
			System.out.println(e.format());
		}
	}

	public ArrayList<Example> getExampleList(){
		return MainInputController.getExamples();
	}
}
