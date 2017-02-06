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
import javafx.scene.paint.Color;
import logic.Argument;
import logic.Attack;
import logic.Equivalency;
import logic.Expansion;
import logic.Extension;
import logic.Framework;
import logic.Framework.Type;

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
	private Button backBtn, nextBtn, showAllBtn, resultsBtn, arrowBtn, computeBtn, toggleBtn, expandBtn, compareBtn; //buttons in demonstration window

	@FXML
	private CheckBox previousCheckBox; //checkbox whether to use previously computed sets or extensions

	@FXML
	private TextArea explanationArea; //textArea describing every computation

	@FXML
	private AnchorPane root; //root pane containing all the UI elements

	@FXML
	private ComboBox<String> setsComboBox, extensionComboBox, comparisonComboBox, expandingComboBox; //dropdown for result sets

	@FXML
	private Label extensionLbl, numberLbl, expandOptionsLbl, expansionLbl, compareLbl;

	@FXML
	private ToggleGroup expansionGroup;

	@FXML
	private RadioButton standardRadio, strongRadio;

	private Tooltip previousTip, arrowTip, backTip, nextTip, allTip, resultsTip, choiceTip, extensionTip, expansionTip, expandTip, comparisonTip, compareTip, typeTip, standardTip, 
	strongTip, toggleTip; //tooltips for all buttons etc

	private Framework argumentFramework, comparisonFramework, expansionFramework; //argument framework containing the arguments
	private ArrayList<Argument> arguments, compArguments, expArguments; //arguments of the framework
	private ArrayList<Attack> attacks, compAttacks, expAttacks; //attacks of the framework
	private Interactor interactor; //Interactor controlling the results the user sees
	private ArrayList<Extension> resultSet; //set containing computation results
	private NodePane graphPane, comparisonPane; //pane where node illustrations are shown
	private boolean expanded; //whether we check extended frameworks
	private Equivalency eq; //this computes equivalencies

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

		expansionTip = new Tooltip("Choose an expansion to your framework and comparison framework.");
		expandingComboBox.setTooltip(expansionTip);
		expansionLbl.setTooltip(expansionTip);

		expandTip = new Tooltip("Expand both frameworks (if available) with your chosen expansion.");
		expandBtn.setTooltip(expandTip);

		comparisonTip = new Tooltip("Choose a framework to compare your framework to."
				+ "\nYour chosen option can be shown by clicking the toggle button.");
		comparisonComboBox.setTooltip(comparisonTip);
		compareLbl.setTooltip(comparisonTip);

		compareTip = new Tooltip("Compare your framework to your chosen comparison framework,"
				+ "\ncomputing your chosen equivalency type.");
		compareBtn.setTooltip(compareTip);

		typeTip = new Tooltip("Chose the type of equivalency to check for your frameworks.");
		expandOptionsLbl.setTooltip(typeTip);

		standardTip = new Tooltip("Set comparison to check for standard equivalency. This checks whether the"
				+ "\nframeworks have the same extensions for the chosen semantics.");
		standardRadio.setTooltip(standardTip);

		strongTip = new Tooltip("Set comparison to check for strong expansion equivalency. This checks whether the"
				+ "\nframeworks have equal kernels for the chosen semantics.");
		strongRadio.setTooltip(strongTip);

		toggleTip = new Tooltip("Switch between the graphs.");
		toggleBtn.setTooltip(toggleTip);
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

		//we need to have an initial comparison pane
		root.getChildren().remove(comparisonPane);
		comparisonPane = new NodePane();
		root.getChildren().add(comparisonPane);
		comparisonPane.setPrefHeight(470);
		comparisonPane.setPrefWidth(445);
		comparisonPane.setLayoutX(15);
		comparisonPane.setVisible(false);

		interactor = Interactor.getInstance(this);
		interactor.updateComparisonGraph(); //in case the interactor already exists
		interactor.updateGraph();
		readArguments(interactor.getRawArguments());
		argumentFramework = new Framework(arguments, attacks, interactor, 1);

		initializeGraph(graphPane, argumentFramework, null);
		explanationArea.setText("");
		explanationArea.setStyle("-fx-text-fill: black;");

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

	/**
	 * initiates the computation of the conflict free sets of the framework
	 */
	public void conflictFreeComputation(Framework framework) {
		interactor.emptyQueue();

		resultSet = framework.getConflictFreeSets();

		//printExtensions(resultSet);

		setUI(true);
	}

	/**
	 * initiates the computation of the admissible sets of the framework
	 */
	public void admissibleComputation(Framework framework){
		interactor.emptyQueue();

		resultSet = framework.getAdmissibleExtensions(previousCheckBox.isSelected());

		//printExtensions(resultSet);

		setUI(true);
	}

	/**
	 * initiates the computation of the complete extensions of the framework
	 */
	public void completeComputation(Framework framework){
		interactor.emptyQueue();

		resultSet = framework.getCompleteExtensions(previousCheckBox.isSelected());

		//printExtensions(resultSet);

		setUI(true);
	}

	/**
	 * initiates the computation of the preferred extensions of the framework
	 */
	public void preferredComputation(Framework framework){
		interactor.emptyQueue();

		resultSet = framework.getPreferredExtensions(previousCheckBox.isSelected());

		//printExtensions(resultSet);

		setUI(true);
	}

	/**
	 * initiates the computation of the stable extensions of the framework
	 */
	public void stableComputation(Framework framework){
		interactor.emptyQueue();

		resultSet = framework.getStableExtensions(previousCheckBox.isSelected());

		//printExtensions(resultSet);

		setUI(true);
	}

	/**
	 * initiates the computation of the grounded extension of the framework
	 */
	public void groundedComputation(Framework framework){
		interactor.emptyQueue();

		Extension grounded = framework.getGroundedExtension(previousCheckBox.isSelected());
		resultSet = new ArrayList<Extension>();
		resultSet.add(grounded);

		//printExtensions(resultSet);

		setUI(true);
	}

	/**
	 * initiates the computation of all semi-stable extensions of the framework
	 * @param framework
	 */
	public void semiStableComputation(Framework framework){
		interactor.emptyQueue();

		resultSet = framework.getSemiStableExtensions(previousCheckBox.isSelected());

		//printExtensions(resultSet);

		setUI(true);
	}

	/**
	 * computes the type of expansion the expansion framework would be to the framework
	 * and to the comparison framework (if available)
	 */
	private void checkExpansionType() {
		interactor.emptyQueue();

		Expansion frameworkExpansion, comparisonExpansion;
		frameworkExpansion = new Expansion(argumentFramework,expansionFramework);
		frameworkExpansion.determineExpansionType(""); //TODO fill in right name?

		if(comparisonFramework != null){
			comparisonExpansion = new Expansion(comparisonFramework,expansionFramework);
			comparisonExpansion.determineExpansionType(expandingComboBox.getAccessibleText());
			System.out.println(expandingComboBox.getAccessibleText()); //TODO remove after testing
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
	 * returns to main input window
	 */
	@FXML
	public void onArrowClick(){
		explanationArea.setText("");
		resetSetChoices();
		expanded = false;
		expandBtn.setText("Expand");
		restoreOriginalFrameworks();

		wrapper.loadMain();
	}

	/**
	 * initiates the computation of a semantics extensions, according to the dropdown menu option chosen
	 */
	@FXML
	public void onComputeClick(){
		int exChoice = extensionComboBox.getSelectionModel().getSelectedIndex();

		Framework framework = Framework.expandFramework(argumentFramework,expansionFramework);

		switch(exChoice){
		case 0:
			explanationArea.setText("No extension type was chosen, no computation performed!");
			break;
		case 1:
			conflictFreeComputation(framework);
			break;
		case 2:
			admissibleComputation(framework);
			break;
		case 3:
			completeComputation(framework);
			break;
		case 4:
			preferredComputation(framework);
			break;
		case 5:
			stableComputation(framework);
			break;
		case 6:
			groundedComputation(framework);
			break;
		case 7:
			semiStableComputation(framework);
			break;
		default:
			explanationArea.setText("Invalid extension type was chosen, no computation performed!");
			break;
		}
	}

	/**
	 * reads the frameworks available (expanded or not), then compares them according to the chosen type
	 */
	@FXML
	public void onCompareClick(){
		interactor.emptyQueue();

		javafx.scene.control.SingleSelectionModel<String> selectExtension, selectComparison;
		selectExtension = extensionComboBox.getSelectionModel();
		selectComparison = comparisonComboBox.getSelectionModel();
		boolean expanded = false;
		eq = new Equivalency(argumentFramework, comparisonFramework, interactor);

		if(selectExtension.isEmpty() || selectExtension.getSelectedIndex() <= 0){
			explanationArea.setText("Can not compare frameworks, since no semantics for comparison was chosen.");
			return;
		}
		else if(selectComparison.isEmpty() || selectComparison.getSelectedIndex() <= 0){
			explanationArea.setText("Can not compare frameworks, since no comparison framework was chosen.");
			return;
		}
		else if(expandBtn.getText().equals(EXPANDED)){
			expanded = true;
		}

		//if we are here, it is possible to compare something
		if(expanded){
			eq.expandFrameworks(expansionFramework); //this should have to exist
		}
	
		RadioButton selectedToggle = (RadioButton) expansionGroup.getSelectedToggle();
		boolean standard = selectedToggle.getText().equals("standard");

		if(standard){
			try {
				resultSet = eq.areStandardEquivalent(extensionComboBox.getSelectionModel().getSelectedIndex(),previousCheckBox.isSelected());
			} catch (InvalidInputException e) {
				explanationArea.setText(e.getMessage());
				return;
			}
		}
		else if(selectedToggle.getText().equals("strong")){
			try {
				eq.areExpansionEquivalent(idToType(extensionComboBox.getSelectionModel().getSelectedIndex()),previousCheckBox.isSelected());
				resultSet = null; //so it knows not to show anything
			} catch (InvalidInputException e) {
				explanationArea.setText(e.getMessage());
				e.printStackTrace();
				return;
			}
		}
		else{
			explanationArea.setText("No equivalence type was chosen, could not perform action!");
			return;
		}

		setUI(standard);
	}

	/**
	 * initiates the expansion of the framework (and comparison framework, if available)
	 * also re-sets the text of the button and the name labels of the pane(s)
	 * then initiates the check for the expansion type
	 * 
	 * if a framework is already expanded, this expansion is rolled back
	 */
	@FXML
	public void onExpandClick(){
		if(!expanded){
			try {
				expandFrameworks();
			} catch (InvalidInputException e) {
				explanationArea.setText(e.getMessage());
				restoreOriginalFrameworks();
				return;
			}
			expanded = true;
			expandBtn.setText(EXPANDED);
			numberLbl.setText(numberLbl.getText() + " + C"); //TODO "+ C" appears on one framework only (alone) fix
			checkExpansionType();
		}
		else{
			expanded = false;
			expandBtn.setText("Expand");
			restoreOriginalFrameworks();
			numberLbl.setText(numberLbl.getText().replace(" + C",""));
		}
	}

	/**
	 * toggles between showing the original framework and the comparison framework
	 * also re-sets name label (including expansions)
	 */
	@FXML
	public void onToggleClick(){
		String text = "";

		if(expansionFramework != null){
			text = " + C";
		}

		if(graphPane.isVisible()){
			graphPane.setVisible(false);
			comparisonPane.setVisible(true);			
			numberLbl.setText("B" + text);
		}
		else{
			graphPane.setVisible(true);
			comparisonPane.setVisible(false);
			numberLbl.setText("A" + text);
		}
	}

	/**
	 * triggers the onToggleClick method depending on which pane is visible and should be visible
	 * @param pane the id of the pane which is to be shown (1=framework, 2=comparison)
	 */
	public void conditionalToggle(int pane){
		if(graphPane.isVisible() && pane == 2){
			onToggleClick();
		}
		else if(comparisonPane.isVisible() && pane == 1){
			onToggleClick();
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
			if(a.isSelected()){
				arguments.add(new Argument(a.getName(),a.getStatement()));
			}
		}

		for(ArgumentDto a: rawArguments){ //two loops, instead there are arguments attacked, that don't exist
			char attackString[] = a.getAttacks().toCharArray();
			for(char att: attackString){
				attacks.add(new Attack(Framework.getArgument(arguments,a.getName()),Framework.getArgument(arguments,att)));
			}
		}
	}

	/**
	 * fills the variables expansionFramework, expAttacks and expArguments
	 * with the values of the expansion that was chosen
	 * following that, the panes showing the frameworks also show the expansions (so showing an expanded framework)
	 * @throws InvalidInputException if there is an invalid attack
	 */
	private void expandFrameworks() throws InvalidInputException {
		Example current = MainInputController.getExamples().get(expandingComboBox.getSelectionModel().getSelectedIndex());

		if(expandingComboBox.getSelectionModel().getSelectedIndex() < 1){
			explanationArea.setText("No expansion framework was chosen, could not expand framework!");
			return;
		}

		expArguments = new ArrayList<Argument>();
		expAttacks = new ArrayList<Attack>();

		for(Line l: current.getLines()){
			if(l.isExists()){
				expArguments.add(new Argument(l.getChar(),l.getDescription()));
			}
		}

		for(Line l: current.getLines()){
			Argument attacker = Framework.getArgument(expArguments,l.getChar());
			Argument compAtt = null;
			Argument compDef = null;

			if(attacker == null){
				attacker = Framework.getArgument(argumentFramework.getArguments(), l.getChar());
				if(comparisonFramework != null){
					compAtt = Framework.getArgument(comparisonFramework.getArguments(), l.getChar());
				}
			}

			for(int i = 0;i<l.getAttacks().length();i++){
				Argument defender = Framework.getArgument(expArguments,l.getAttacks().charAt(i));
				if(defender == null){
					defender = Framework.getArgument(argumentFramework.getArguments(), l.getAttacks().charAt(i));
					if(comparisonFramework != null){
						compDef = Framework.getArgument(comparisonFramework.getArguments(), l.getAttacks().charAt(i));
					}
				}

				if(attacker != null && defender != null && 
						(comparisonFramework == null || (comparisonFramework != null && compAtt != null && compDef != null))){
					expAttacks.add(new Attack(attacker,defender));
				}
				else{
					throw new InvalidInputException("Invalid attack detected! (" + l.getChar() + 
							"," + l.getAttacks().charAt(i) + ")");
				}
			}
		}

		expansionFramework = new Framework(expArguments, expAttacks, interactor, 0); //pane should never be used --> 0

		//this should draw the expanded version
		initializeGraph(graphPane,argumentFramework,expansionFramework);

		if(comparisonFramework != null){
			initializeGraph(comparisonPane,comparisonFramework,expansionFramework);
		}
	}

	/**
	 * removes references to the chosen expansion from data and from the gui
	 */
	private void restoreOriginalFrameworks() {
		expansionFramework = null;
		expArguments = null;
		expAttacks = null;
		initializeGraph(graphPane,argumentFramework,null);
		if(comparisonFramework != null){
			initializeGraph(comparisonPane,comparisonFramework,null);
		}
	}

	/**
	 * loads the framework given into the pane given, removes old frameworks, and draws a graph
	 * @param pane the pane we want to show the graph in
	 * @param framework the framework we want to show as a graph
	 * @param expansion an (optional) expansion to the framework we might want to also show
	 */
	private void initializeGraph(NodePane pane, Framework framework, Framework expansion){
		pane.createGraph(framework,expansion);

		try {
			pane.getChildren().clear();
			pane.drawGraph();
		} catch (InvalidInputException e) {
			interactor.emptyQueue();
			explanationArea.setText(e.getMessage() + "\n The graph may not be correctly displayed!");
			explanationArea.setStyle("-fx-text-fill: red;");
		}
	}

	/**
	 * sets up the UI for showing comparison results
	 */
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

		interactor.updateComparisonGraph();

		Example current = MainInputController.getExamples().get(comparisonComboBox.getSelectionModel().getSelectedIndex());

		compArguments = new ArrayList<Argument>();
		compAttacks = new ArrayList<Attack>();

		for(Line l: current.getLines()){
			if(l.isExists()){
				compArguments.add(new Argument(l.getChar(),l.getDescription()));
			}
		}

		for(Line l: current.getLines()){
			if(l.isExists()){
				Argument attacker = Framework.getArgument(compArguments,l.getChar());
				for(int i = 0;i<l.getAttacks().length();i++){
					Argument defender = Framework.getArgument(compArguments,l.getAttacks().charAt(i));
					if(defender == null){
						explanationArea.setText("Illegal attack detected!");
						return;
					}
					compAttacks.add(new Attack(attacker,defender));
				}
			}
		}

		comparisonFramework = new Framework(compArguments, compAttacks, interactor, 2);

		comparisonPane.createGraph(comparisonFramework,null);
		toggleBtn.setDisable(false);
		numberLbl.setText("A");

		try {
			comparisonPane.drawGraph();
		} catch (InvalidInputException e) {
			explanationArea.setText("Could not load comparison!");
			return;
		}
	}

	/**
	 * brings the UI into a state where viewing of the computation process is possible
	 */
	public void setUI(boolean showExtensions){
		if(showExtensions){
			resetSetChoices();
		}
		else{
			setsComboBox.setVisible(false);
		}

		explanationArea.setText("");
		explanationArea.setStyle("-fx-text-fill: black;");
		backBtn.setDisable(true);
		nextBtn.setDisable(false);
		showAllBtn.setDisable(false);
		resultsBtn.setDisable(false);

		interactor.executeNextCommand();
	}

	/**
	 * activates the choicebox (dropdown menu) that shows the extensions
	 * of the chosen type
	 */
	public void showSetChoices(){
		setsComboBox.setDisable(false);

		ArrayList<String> formatList = new ArrayList<String>();

		if(resultSet != null && !resultSet.isEmpty()){
			for(Extension e: resultSet){
				formatList.add(e.format());
			}
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
	 * loads all examples from the maininput into a combobox
	 */
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
	 * disables the next and show all buttons
	 */
	public void disableForwardButtons(){
		nextBtn.setDisable(true);
		showAllBtn.setDisable(true);
		resultsBtn.setDisable(true);
	}

	/**
	 * dis/en-ables the radio buttons for chosing the type of equivalency, depending on if the 
	 * possibilty of comparison exists
	 * @param disabled whether the buttons are already disabled
	 */
	private void setDisableRadioButtons(boolean disabled) {
		for(int i = 0;i<expansionGroup.getToggles().size();i++){
			Object tmp = expansionGroup.getToggles().get(i);

			if(tmp instanceof RadioButton){
				((RadioButton) tmp).setDisable(disabled);
			}
		}

		expandOptionsLbl.setDisable(disabled);
	}

	/**
	 * helper method to convert a semantics id (used here)
	 * to its Framework.Type (used in other classes)
	 * @param id id of type
	 * @return type of id
	 */
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
				GraphInstruction instruction = argumentFramework.getNodeInstructionsFromArgumentList((String) item, Color.GREEN);
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
				computeBtn.setDisable(false);
				comparisonFramework = null;
			}
			else{ // on choosing a framework, old expansion on just one example are unexpanded
				if(expansionFramework != null){
					onExpandClick();
				}

				computeBtn.setDisable(true);
				setDisableRadioButtons(false);
				initializeComparisonResources();
			}
		}
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
	 * @return the comparison NodePane containing the graphical representation of the framework
	 */
	public NodePane getComparisonPane(){
		return comparisonPane;
	}

	public ArrayList<Example> getExampleList(){
		return MainInputController.getExamples();
	}
}
