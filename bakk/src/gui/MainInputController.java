package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;

import dto.ArgumentDto;
import exceptions.InvalidInputException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;

public class MainInputController {

	private static WrapperController wrapper;

	private Interactor interactor;

	@FXML
	private AnchorPane root;

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private TextField argumentATxt, argumentBTxt, argumentCTxt, argumentDTxt, argumentETxt, argumentFTxt, 
	argumentGTxt, argumentHTxt, argumentITxt, argumentJTxt, attackATxt, attackBTxt, attackCTxt, attackDTxt,
	attackETxt, attackFTxt, attackGTxt, attackHTxt, attackITxt, attackJTxt;

	@FXML
	private CheckBox ABox, BBox, CBox, DBox, EBox, FBox, GBox, HBox, IBox, JBox; 

	@FXML
	private Label useLbl, addLbl, attackLbl, headlineLbl, errorLbl;

	@FXML
	private Button showGraphBtn;

	private Tooltip showTip, useTip, descriptionTip, attackTip, generalAttackTip;

	// TODO create tooltips

	private ArrayList<ArgumentDto> arguments;
	private ArrayList<CheckBox> checkBoxes;
	private ArrayList<TextField> statements;
	private ArrayList<TextField> attacks;
	private ArrayList<?> alphabetical[];

	@FXML
	void initialize() {
		interactor = Interactor.getInstance(null);

		errorLbl.setText("");

		showTip = new Tooltip();
		showTip.setText("Shows a graph representation of the created abstract argumentation framework.\nThere you can compute various extensions.");
		showGraphBtn.setTooltip(showTip);

		checkBoxes = new ArrayList<CheckBox>();
		statements = new ArrayList<TextField>();
		attacks = new ArrayList<TextField>();
		Collections.addAll(checkBoxes, ABox, BBox, CBox, DBox, EBox, FBox, HBox, IBox, JBox);
		Collections.addAll(statements, argumentATxt, argumentBTxt, argumentCTxt, argumentDTxt, argumentETxt,
				argumentFTxt, argumentGTxt, argumentHTxt, argumentITxt, argumentJTxt);
		Collections.addAll(attacks, attackATxt, attackBTxt, attackCTxt, attackDTxt, attackETxt, attackFTxt,
				attackGTxt, attackHTxt, attackITxt, attackJTxt);

		alphabetical = new ArrayList<?>[3];
		alphabetical[0] = checkBoxes;
		alphabetical[1] = statements;
		alphabetical[2] = attacks;

		useTip = new Tooltip();
		useTip.setText("Select which arguments you want\nto use for further computation.");
		useLbl.setTooltip(useTip);
		for(CheckBox c: checkBoxes){
			c.setTooltip(useTip);
		}

		descriptionTip = new Tooltip();
		descriptionTip.setText("Assign a statement or description to the argument.");
		addLbl.setTooltip(descriptionTip);
		for(TextField f: statements){
			f.setTooltip(descriptionTip);
		}

		attackTip = new Tooltip();
		attackTip.setText("Write the name of the arguments\nthis argument should attack here.");
		for(TextField f: attacks){
			f.setTooltip(attackTip);
		}
		
		generalAttackTip = new Tooltip();
		generalAttackTip.setText("Set attacks between arguments.");
		attackLbl.setTooltip(generalAttackTip);
		
	}

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
		wrapper.loadDemonstration();
	}

	private String parseArgument(TextField argument) {
		if(argument.getText().isEmpty()){
			return "no argument description";
		}
		return argument.getText();
	}

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
				}
				else if(input.contains(stringValue.toLowerCase())){ //check for lowercase
					attackValues += stringValue;
					input = input.replace(stringValue.toLowerCase(), "");
				}
			}
		}

		if(input.length() > 0){
			throw new InvalidInputException("Invalid or duplicate attacks detected: " + input);
		}

		return attackValues;
	}

	private String getSelected() {
		String selected = "";

		for(CheckBox c: checkBoxes){
			if(c.isSelected()){
				selected += c.getText();
			}
		}

		return selected;
	}

	public static void setWrapper(WrapperController wrapperController) {
		wrapper = wrapperController;
	}
}
