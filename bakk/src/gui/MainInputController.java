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
	attackETxt, attackFTxt, attackGTxt, attackHTxt, attackITxt, attackJTxt, nameTxt; //text fields for input

	@FXML
	private CheckBox ABox, BBox, CBox, DBox, EBox, FBox, GBox, HBox, IBox, JBox; //checkboxes selecting which textfields to be read from

	@FXML
	private Label argLbl, addLbl, attackLbl, headlineLbl, errorLbl, presetLbl, saveLbl; //descriptive labels

	@FXML
	private Button showGraphBtn, saveBtn, newBtn, clearBtn;

	@FXML
	private ComboBox<String> presetComboBox;

	private Tooltip showTip, argTip, descriptionTip, attackTip, generalAttackTip, choiceTip, saveTip, newTip,
	clearTip; //tooltips describing what to input or what happens

	private Interactor interactor; //Interactor controlling the results the user sees
	private ArrayList<ArgumentDto> arguments; //arguments read from the text fields
	private ArrayList<CheckBox> checkBoxes; //list of checkboxes selecting information to be used
	private ArrayList<TextField> statements; //list of textfields containing argument describing statements
	private ArrayList<TextField> attacks; //list of textfields containing the attack information
	private ArrayList<?> alphabetical[]; //array of input containing lists
	private static ArrayList<Example> examples;
	private int autosave; //each automatically saved framework is called autosave 1,2,...

	/**
	 * gets an Interactor and adds tooltips for elements
	 */
	@FXML
	void initialize() {
		interactor = Interactor.getInstance(null);
		examples = initializeExamples();
		autosave = 0;

		errorLbl.setText("");

		showTip = new Tooltip("Shows a graph representation of the created abstract argumentation framework.\n"
				+ "There you can compute various extensions or compare it to another framework.\n"
				+ "Note: You can only load frameworks that contain all the arguments that have attacks.");
		showGraphBtn.setTooltip(showTip);

		checkBoxes = new ArrayList<CheckBox>();
		statements = new ArrayList<TextField>();
		attacks = new ArrayList<TextField>();
		Collections.addAll(checkBoxes, ABox, BBox, CBox, DBox, EBox, FBox, GBox, HBox, IBox, JBox);
		Collections.addAll(statements, argumentATxt, argumentBTxt, argumentCTxt, argumentDTxt, argumentETxt,
				argumentFTxt, argumentGTxt, argumentHTxt, argumentITxt, argumentJTxt);
		Collections.addAll(attacks, attackATxt, attackBTxt, attackCTxt, attackDTxt, attackETxt, attackFTxt,
				attackGTxt, attackHTxt, attackITxt, attackJTxt);

		alphabetical = new ArrayList<?>[3];
		alphabetical[0] = checkBoxes;
		alphabetical[1] = statements;
		alphabetical[2] = attacks;

		argTip = new Tooltip("Select which arguments are to be in the framework.");
		argLbl.setTooltip(argTip);
		for(CheckBox c: checkBoxes){
			c.setTooltip(argTip);
		}

		descriptionTip = new Tooltip("Assign a statement or description to the argument.");
		addLbl.setTooltip(descriptionTip);
		for(TextField f: statements){
			f.setTooltip(descriptionTip);
		}

		attackTip = new Tooltip("Write the names of the arguments\nthis argument should attack here.\n"
				+ "Note: the argument does not need to be selected.");
		for(TextField f: attacks){
			f.setTooltip(attackTip);
		}

		generalAttackTip = new Tooltip("Set attacks between arguments.");
		attackLbl.setTooltip(generalAttackTip);

		choiceTip = new Tooltip("You can choose a preset to load an example framework into the textfields above.");
		presetComboBox.setTooltip(choiceTip);

		saveTip = new Tooltip("Save the currently selected arguments and attacks as a new framework\n"
				+ "with the name given in the textfield or overwrite the framework specified there.");
		newTip = new Tooltip("Clears all textfields and selections to start a new framework, automatically saves the current selection.");
		clearTip = new Tooltip("Clears all textfields, does not save any selection.");

		saveBtn.setTooltip(saveTip);
		newBtn.setTooltip(newTip);
		clearBtn.setTooltip(clearTip);

		showChoices();
		clearAll();
	}

	/**
	 * creates a list of pre-defined examples
	 * each example needs to contain an entry for every letter!
	 * @return the list of examples
	 */
	private ArrayList<Example> initializeExamples() {
		ArrayList<Example>exampleSet = new ArrayList<Example>(); //now also use to save new (implement save/delete behaviour) frameworks

		exampleSet.add(new Example("",null));

		exampleSet.add(new Example(
				"Woltran Standard 1a",
				new Line[] {
						new Line('A', "", "B", true),
						new Line('B', "", "C", true),
						new Line('C', "", "A", true),
						new Line('D',"","",false), new Line('E',"","",false), new Line('F',"","",false), new Line('G',"","",false),
						new Line('H',"","",false), new Line('I',"","",false), new Line('J',"","",false)
				}));

		exampleSet.add(new Example(
				"Woltran Standard 1b",
				new Line[] {
						new Line('A', "", "C", true),
						new Line('B', "", "A", true),
						new Line('C', "", "B", true),
						new Line('D',"","",false), new Line('E',"","",false), new Line('F',"","",false), new Line('G',"","",false),
						new Line('H',"","",false), new Line('I',"","",false), new Line('J',"","",false)
				}));
		
		exampleSet.add(new Example(
				"Woltran Expansion 1",
				new Line[] {
						new Line('A', "", "", false), new Line('B', "", "", false),	new Line('C', "", "", false),
						new Line('D',"","B",true), new Line('E',"","",false), new Line('F',"","",false), new Line('G',"","",false),
						new Line('H',"","",false), new Line('I',"","",false), new Line('J',"","",false)
				}));

		return exampleSet;
	}



	/**
	 * shows predefined examples in drop down menu
	 * @param selection 
	 */
	public void showChoices(){
		ArrayList<String> formatList = new ArrayList<String>();

		for(Example e: examples){
			formatList.add(e.getName());
		}

		presetComboBox.setItems(FXCollections.observableArrayList(formatList));
		presetComboBox.getSelectionModel().selectedIndexProperty().addListener(new ChoiceListener<Number>());
	}

	/**
	 * an action handler to change the arguments/attacks/descriptions show in the maininput
	 * according to which example was chosen from the example drop down
	 * @author Patrick
	 */
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
						int number = l.getChar() - 65;
						Object cb = alphabetical[0].get(number);
						Object t1 = alphabetical[1].get(number);
						Object t2 = alphabetical[2].get(number);

						if(cb instanceof CheckBox && t1 instanceof TextField && t2 instanceof TextField){
							((CheckBox) cb).selectedProperty().set(l.isExists());
							((TextField) t1).setText(l.getDescription());
							((TextField) t2).setText(l.getAttacks());
						}
					}
					nameTxt.setText(e.getName());
					break;
				}
			}
		}
	}

	/**
	 * resets all input fields and check boxes to be empty/unselected
	 */
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
		arguments = createTransferObjectList();

		if(hasDanglingAttacks(arguments)){
			errorLbl.setText("You can only use a framework containing attacks on arguments it does not contain, as an expansion!" +
					"\nPlease save your framework and use it as an expansion instead!");
			return;
		}

		autosave++; //we have standard 0, we start with 1 and so on
		String autoName = "Autosave " + autosave;

		Example tmp = convertToExample(arguments,autoName);
		if(!exampleExists(tmp)){
			examples.add(tmp);
			showChoices();
		}
		else{
			autosave--;
		}

		errorLbl.setText("");
		interactor.setRawArguments(arguments);
		interactor.setDemonstrationValues();
		wrapper.loadDemonstration();
	}

	/**
	 * checks whether an example has attacks on/from arguments it does not
	 * contain itself
	 * @param input the list of arguments + attacks to check
	 * @return whether such a "Dangling" attack exists
	 */
	private boolean hasDanglingAttacks(ArrayList<ArgumentDto> input) {
		for(ArgumentDto a: input){
			if(!a.isSelected() && a.getAttacks().length() != 0){
				return true;
			}
		}

		return false;
	}

	/**
	 * creates ArgumentDtos from all arguments that have input
	 * @return list of ArgumentDtos if available, otw null
	 */
	private ArrayList<ArgumentDto> createTransferObjectList(){
		ArrayList<ArgumentDto> arguments = new ArrayList<ArgumentDto>();

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

				arguments.add(new ArgumentDto(ctmp.getText().charAt(0), parseArgument(stmp), parseAttacks(atmp), ctmp.isSelected()));
			}
		} catch(InvalidInputException e){
			errorLbl.setText(e.getMessage());
			return null;
		} catch(IndexOutOfBoundsException e){
			errorLbl.setText("Critical error: " + e.getMessage());
			return null;
		}

		return arguments;
	}

	/**
	 * checks whether the example in question is already saved
	 * @param tmp example to check
	 * @return whether it already exists
	 */
	private boolean exampleExists(Example tmp) {
		for(Example e:examples){
			if (e.equals(tmp)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * combines a list of arguments and attacks into an example
	 * @param arguments a list of argumentDtos containing arguments and their attacks
	 * @param name how the example created is to be called
	 * @return the created example
	 */
	private Example convertToExample(ArrayList<ArgumentDto> arguments, String name) {
		Line[] lines = new Line[arguments.size()];

		for(int i = 0;i<arguments.size();i++){
			ArgumentDto tmp = arguments.get(i);
			lines[i] = new Line(tmp.getName(),tmp.getStatement(),tmp.getAttacks(),tmp.isSelected());
		}

		return new Example(name,lines);
	}

	/**
	 * clears the old input after saving it (if new or changed)
	 */
	@FXML
	public void onNewClick(){ //overwrites old example, if name is same, otherwise writes new autosave
		Example oldExample;
		String oldName = nameTxt.getText();

		if(oldName == null || oldName.equals("")){
			autosave++;
			oldName = "Autosave " + autosave;
		}

		ArrayList<ArgumentDto> toList = createTransferObjectList();	

		if(toList == null){
			return; //handled?
		}

		oldExample = convertToExample(toList,oldName);
		Example prevVersion = getExampleByName(oldName);

		if(prevVersion != null){
			examples.set(examples.indexOf(prevVersion),oldExample); // previous name version gets overwritten
			showChoices();
		}
		else if(!exampleExists(oldExample)){
			examples.add(oldExample);
			showChoices();
		}

		clearAll();
	}

	/**
	 * clears all inputs on button click
	 */
	@FXML
	public void onClearClick(){
		clearAll();
	}

	/**
	 * clears all inputs
	 */
	private void clearAll(){
		presetComboBox.getSelectionModel().selectFirst();
		nameTxt.setText("");
	}


	/**
	 * saves the input as a new example, given the name in the name text field
	 * and adds it to the dropdown, then selects it from there
	 */
	@FXML 
	public void onSaveClick(){
		String name = nameTxt.getText();
		Example toSave = getExampleByName(name);

		ArrayList<ArgumentDto> toList = createTransferObjectList();

		if(toList == null){
			errorLbl.setText("Could not read input arguments!");
			return;
		}

		if(name == null || name.length() == 0){
			errorLbl.setText("Could not save framework, no name was given.");
			return;
		}

		if(toSave == null){
			examples.add(convertToExample(toList,name));
			showChoices();
			presetComboBox.getSelectionModel().selectLast();
		}
		else{
			int id = examples.indexOf(toSave);
			examples.set(id, convertToExample(toList,name));
			presetComboBox.getSelectionModel().select(id);
			errorLbl.setText("no change detected");
		}

	}

	/**
	 * searches examples for an example of the name given
	 * @param name the name of the example to be found
	 * @return the found example, otw null
	 */
	private Example getExampleByName(String name){
		for (Example e:examples){
			if (name.equals(e.getName())){
				return e;
			}
		}

		return null;
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
		String argumentNames = "ABCDEFGHIJ";
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

	/**
	 * sets the wrapper for all windows to apply to this controller's window
	 * @param wrapperController the wrapper controlling which window is shown
	 */
	public static void setWrapper(WrapperController wrapperController) {
		wrapper = wrapperController;
	}

	static ArrayList<Example> getExamples() {
		return examples;
	}
}
