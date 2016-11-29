package gui;

import interactor.Interactor;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
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
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;

/**
 * Controller controlling the MainInput.fxml file behaviour
 *  for managing input to further use in computations
 * @author Patrick Bellositz
 */
public class MainInputController {

	/**
	 * wrapper object controlling what is shown on screen
	 */
	private static WrapperController wrapper;

	@FXML
	private AnchorPane root; //root pane

	@FXML
	private ResourceBundle resources; //resource bundle

	@FXML
	private URL location; //file location

	@FXML
	private TextField argumentATxt, argumentBTxt, argumentCTxt, argumentDTxt, argumentETxt, argumentFTxt, 
	argumentGTxt, argumentHTxt, argumentITxt, argumentJTxt, attackATxt, attackBTxt, attackCTxt, attackDTxt,
	attackETxt, attackFTxt, attackGTxt, attackHTxt, attackITxt, attackJTxt; //text fields for input

	@FXML
	private CheckBox ABox, BBox, CBox, DBox, EBox, FBox, GBox, HBox, IBox, JBox; //checkboxes selecting which textfields to be read from

	@FXML
	private Label useLbl, addLbl, attackLbl, headlineLbl, errorLbl, presetLbl; //descriptive labels

	@FXML
	private Button showGraphBtn, previousBtn, nextBtn, compareBtn, computeBtn;
	
	@FXML
	private ComboBox<String> presetComboBox;

	private Tooltip showTip, useTip, descriptionTip, attackTip, generalAttackTip, choiceTip, nextTip, previousTip; //tooltips describing what to input or what happens

	/*
	 * TODO implement naming/numbering of frameworks -->
	 * create new class that contains alphabetical, which then contains checkboxes, arguments, statements, attacks
	 * maybe rename choose preset to choose between saved frameworks? and add them to list with generic name?
	 */
	private Interactor interactor; //Interactor controlling the results the user sees
	private ArrayList<ArgumentDto> arguments; //arguments read from the text fields
	private ArrayList<CheckBox> checkBoxes; //list of checkboxes selecting information to be used
	private ArrayList<TextField> statements; //list of textfields containing argument describing statements
	private ArrayList<TextField> attacks; //list of textfields containing the attack information
	private ArrayList<?> alphabetical[]; //array of input containing lists
	private ArrayList<Example> examples;
	private int currentPointer;
	
	/**
	 * gets an Interactor and adds tooltips for elements
	 */
	@FXML
	void initialize() {
		interactor = Interactor.getInstance(null);

		errorLbl.setText("");

		showTip = new Tooltip("Shows a graph representation of the created abstract argumentation framework.\nThere you can compute various extensions.");
		showGraphBtn.setTooltip(showTip);

		checkBoxes = new ArrayList<CheckBox>();
		statements = new ArrayList<TextField>();
		attacks = new ArrayList<TextField>();
		Collections.addAll(checkBoxes, ABox, BBox, CBox, DBox, EBox, FBox, GBox, HBox, IBox, JBox);
		Collections.addAll(statements, argumentATxt, argumentBTxt, argumentCTxt, argumentDTxt, argumentETxt,
				argumentFTxt, argumentGTxt, argumentHTxt, argumentITxt, argumentJTxt);
		Collections.addAll(attacks, attackATxt, attackBTxt, attackCTxt, attackDTxt, attackETxt, attackFTxt,
				attackGTxt, attackHTxt, attackITxt, attackJTxt);

		//TODO integrate TextData class to have multiple frameworks saved --> actually have multiple frameworks saved in dtos?
		
		alphabetical = new ArrayList<?>[3];
		alphabetical[0] = checkBoxes;
		alphabetical[1] = statements;
		alphabetical[2] = attacks;

		useTip = new Tooltip("Select which arguments you want\nto use for further computation.");
		useLbl.setTooltip(useTip);
		for(CheckBox c: checkBoxes){
			c.setTooltip(useTip);
		}

		descriptionTip = new Tooltip("Assign a statement or description to the argument.");
		addLbl.setTooltip(descriptionTip);
		for(TextField f: statements){
			f.setTooltip(descriptionTip);
		}

		attackTip = new Tooltip("Write the names of the arguments\nthis argument should attack here.");
		for(TextField f: attacks){
			f.setTooltip(attackTip);
		}
		
		generalAttackTip = new Tooltip("Set attacks between arguments.");
		attackLbl.setTooltip(generalAttackTip);
		
		choiceTip = new Tooltip("You can choose a preset to load an example framework into the textfields above.");
		presetComboBox.setTooltip(choiceTip);
		
		nextTip = new Tooltip("Show the next available framework's arguments and attack relations.");
		previousTip = new Tooltip("Show the previous available frameworks's arguments and attack relations.");
		//noneTip = new Tooltip("There are no other frameworks available, therfore they can not be shown."); //use according to context?
		
		nextBtn.setTooltip(nextTip);
		previousBtn.setTooltip(previousTip);
		
		examples = initializeExamples();
		
		showChoices();
	}

	private ArrayList<Example> initializeExamples() {
		ArrayList<Example> exampleSet = new ArrayList<Example>(); //now also use to save new (implement save/delete behaviour) frameworks
		currentPointer = 0;
		
		exampleSet.add(new Example("",null));
		
		exampleSet.add(new Example("DUNG Example 1",
				new Line[] {
					new Line('a', "I: My government can not negotiate with your government because your government doesn�t even recognize my government.", "b"),
					new Line('b', "A: Your government doesn't recognize my government either.", "a"),
					new Line('c', "I: But your government is a terrorist government.", "b")
		}));
		
		exampleSet.add(new Example("Nixon Diamond",
				new Line[] {
					new Line('a', "Nixon is anti-pacifist since he is a republican.", "b"),
					new Line('b', "Nixon is a pacifist since he is a quaker.", "a"),
		}));
		
		exampleSet.add(new Example("Thesis Example 1-7",
				new Line[] {
					new Line('a', "A: Blue is the most beautiful of all colors.", "b"),
					new Line('b', "B: No, black is much more beautiful!", "a"),
					new Line('c', "A: That's wrong, black isn't even a color.", "b")
		}));
		
		exampleSet.add(new Example("Thesis Example 8",
				new Line[] {
					new Line('a', "", "a"),
					new Line('b', "", "ac"),
					new Line('c', "", "b")
		}));
		
		exampleSet.add(new Example("Thesis Example 9",
				new Line[] {
					new Line('a', "", "b"),
					new Line('b', "", "a"),
					new Line('c', "", "bd"),
					new Line('d', "", "c")
		}));
		
		exampleSet.add(new Example("Thesis Figure 4.1",
				new Line[] {
					new Line('a', "", ""),
					new Line('b', "", "ac"),
					new Line('c', "", "bd"),
					new Line('d', "", "ace"),
					new Line('e', "", "e")
		}));
		
		exampleSet.add(new Example("Egly Example 1",
				new Line[] {
					new Line('a', "", "b"),
					new Line('b', "", ""),
					new Line('c', "", "bd"),
					new Line('d', "", "ce"),
					new Line('e', "", "e")
		}));
		
		return exampleSet;
	}
	
	
	public void showChoices(){
		ArrayList<String> formatList = new ArrayList<String>();
		
		for(Example e: examples){
			formatList.add(e.getName());
		}

		presetComboBox.setItems(FXCollections.observableArrayList(formatList));
		presetComboBox.getSelectionModel().selectedIndexProperty().addListener(new ChoiceListener<Number>());
		presetComboBox.getSelectionModel().selectFirst();
	}
	
	@SuppressWarnings("hiding")
	private class ChoiceListener<Number> implements ChangeListener<Number>{
		@Override
		public void changed(ObservableValue<? extends Number> oval, Number sval, Number nval){
			if((Integer) nval == -1){
				return;
			}
			
			Object item = presetComboBox.getItems().get((Integer) nval); 

			if(item instanceof String){
				resetMask();
				loadExample((String) item);
			}
		}

		private void loadExample(String item) {
			for(Example e: examples){
				if(e.getName().equals(item) && e.getLines() != null){
					for(Line l: e.getLines()){
						Object cb = alphabetical[0].get(l.getNumber());
						Object t1 = alphabetical[1].get(l.getNumber());
						Object t2 = alphabetical[2].get(l.getNumber());
						
						if(cb instanceof CheckBox && t1 instanceof TextField && t2 instanceof TextField){
							((CheckBox) cb).selectedProperty().set(true);
							((TextField) t1).setText(l.getDescription());
							((TextField) t2).setText(l.getAttacks());
						}
					}
					break;
				}
			}
		}
	}
	
	private void resetMask() {
		for(int i = 0; i<alphabetical[0].size(); i++){
			Object a = alphabetical[0].get(i);
			Object b = alphabetical[1].get(i);
			Object c = alphabetical[2].get(i);
			
			if(a instanceof CheckBox && b instanceof TextField && c instanceof TextField){
				((CheckBox) a).selectedProperty().set(false);
				((TextField) b).setText("");
				((TextField) c).setText("");
			}
		}
	}

	/**
	 * reads text from selected rows and stores it into ArgumentDtos, then initiates screen change to Demonstration Window
	 */
	@FXML
	public void onShowButton(){
		arguments = new ArrayList<ArgumentDto>();

		try{
			for(int i = 0; i <= alphabetical[0].size()-1; i++){
				CheckBox ctmp;
				TextField stmp;
				TextField atmp;
				
				if((alphabetical[0].get(i) instanceof CheckBox) && (alphabetical[1].get(i) instanceof TextField)
						&& (alphabetical[2].get(i) instanceof TextField)){
					ctmp = (CheckBox) alphabetical[0].get(i);
					stmp = (TextField) alphabetical[1].get(i);
					atmp = (TextField) alphabetical[2].get(i);
				}
				else{
					throw new IndexOutOfBoundsException("UI loading error!");
				}

				if(ctmp.isSelected()){
					arguments.add(new ArgumentDto(ctmp.getText().charAt(0), parseArgument(stmp), parseAttacks(atmp)));
				}
			}
		} catch(InvalidInputException e){
			errorLbl.setText(e.getMessage());
			return;
		} catch(IndexOutOfBoundsException e){
			errorLbl.setText("critical error: " + e.getMessage());
			return;
		}

		errorLbl.setText("");
		interactor.setRawArguments(arguments);
		interactor.setDemonstrationValues();
		wrapper.loadDemonstration();
	}
	
	//TODO change on new button click instead of changelistening?
	@FXML
	public void onPreviousButton(){
		//compare to last selected here
		
		if(currentPointer - 1 < 0){
			currentPointer = examples.size()-1;
		}
		else{
			currentPointer--;
		}
		
		//TODO trigger changelistener?
		
	}
	
	@FXML
	public void onNextButton(){
		if(currentPointer + 1 >= examples.size()){
			currentPointer = 0;
		}
		else{
			currentPointer++;
		}
		
		//TODO implement switching (analogous to above)
	}

	/**
	 * creates standard description for an argument or returns given one
	 * @param argument the TextField containing the description
	 * @return the standard or given description
	 */
	private String parseArgument(TextField argument) {
		if(argument.getText().isEmpty()){
			return "no argument description";
		}
		return argument.getText();
	}

	/**
	 * reads attack String, filters and checks for invalid input
	 * @param attack the TextField containing the attack String
	 * @return a String of valid attacks
	 * @throws InvalidInputException if there is invalid input, throws error message to calling method
	 */
	private String parseAttacks(TextField attack) throws InvalidInputException {
		String input = attack.getText();
		String argumentNames = getSelected();
		String attackValues = "";

		if(input.isEmpty()){
			return "";
		}
		else{
			input = input.replaceAll("[ ,]", "");

			for(int i = 0; i < argumentNames.length(); i++){
				String stringValue = String.valueOf(argumentNames.charAt(i)); 
				if(input.contains(stringValue)){ //check for uppercase
					attackValues += stringValue;
					input = input.replace(stringValue, "");
					input = input.replace(stringValue.toLowerCase(), "");
				}
				else if(input.contains(stringValue.toLowerCase())){ //check for lowercase
					attackValues += stringValue;
					input = input.replace(stringValue.toLowerCase(), "");
				}
			}
		}

		if(input.length() > 0){
			throw new InvalidInputException("Invalid attacks detected: " + input);
		}

		return attackValues;
	}
	
	//TODO implement comparing mechanism (choose extension to compare totally new?)
	//TODO maybe rename to equivalence check or something
	@FXML
	public void onCompareClick(){
		//TODO implement
	}

	/**
	 * checks which Arguments' are selected
	 * @return a String of selected Arguments
	 */
	private String getSelected() {
		String selected = "";

		for(CheckBox c: checkBoxes){
			if(c.isSelected()){
				selected += c.getText();
			}
		}

		return selected;
	}

	/**
	 * sets the wrapper for all windows to apply to this controller's window
	 * @param wrapperController the wrapper controlling which window is shown
	 */
	public static void setWrapper(WrapperController wrapperController) {
		wrapper = wrapperController;
	}
}
