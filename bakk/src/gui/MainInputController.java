package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import dto.ArgumentDto;
import exceptions.InvalidInputException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
	private Label useLbl, addLbl, attackLbl, headlineLbl, aLbl, bLbl, cLbl, dLbl, eLbl, fLbl, gLbl, hLbl, iLbl, jLbl;

	@FXML
	private Button showGraphBtn;

	// TODO create tooltips

	private ArrayList<ArgumentDto> arguments;

	@FXML
	void initialize() {
		interactor = Interactor.getInstance(null);
	}

	@FXML
	public void onShowButton(){
		arguments = new ArrayList<ArgumentDto>();
		
		try{
			if(ABox.isSelected()){
				arguments.add(new ArgumentDto('A', parseArgument(argumentATxt), parseAttacks(attackATxt)));
			}
			if(BBox.isSelected()){
				arguments.add(new ArgumentDto('B', parseArgument(argumentBTxt), parseAttacks(attackBTxt)));
			}
			if(CBox.isSelected()){
				arguments.add(new ArgumentDto('C', parseArgument(argumentCTxt), parseAttacks(attackCTxt)));
			}
			if(DBox.isSelected()){
				arguments.add(new ArgumentDto('D', parseArgument(argumentDTxt), parseAttacks(attackDTxt)));
			}
			if(EBox.isSelected()){
				arguments.add(new ArgumentDto('E', parseArgument(argumentETxt), parseAttacks(attackETxt)));
			}
			if(FBox.isSelected()){
				arguments.add(new ArgumentDto('F', parseArgument(argumentFTxt), parseAttacks(attackFTxt)));
			}
			if(GBox.isSelected()){
				arguments.add(new ArgumentDto('G', parseArgument(argumentGTxt), parseAttacks(attackGTxt)));
			}
			if(HBox.isSelected()){
				arguments.add(new ArgumentDto('H', parseArgument(argumentHTxt), parseAttacks(attackHTxt)));
			}
			if(IBox.isSelected()){
				arguments.add(new ArgumentDto('I', parseArgument(argumentITxt), parseAttacks(attackITxt)));
			}
			if(JBox.isSelected()){
				arguments.add(new ArgumentDto('J', parseArgument(argumentJTxt), parseAttacks(attackJTxt)));
			}
		} catch(InvalidInputException e){
			System.out.println(e.getMessage());
			//TODO put into error label
			return;
		}

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

		//Most beautiful ugly code!
		if(ABox.isSelected()) selected += "A";
		if(BBox.isSelected()) selected += "B";
		if(CBox.isSelected()) selected += "C";
		if(DBox.isSelected()) selected += "D";
		if(EBox.isSelected()) selected += "E";
		if(FBox.isSelected()) selected += "F";
		if(GBox.isSelected()) selected += "G";
		if(HBox.isSelected()) selected += "H";
		if(IBox.isSelected()) selected += "I";
		if(JBox.isSelected()) selected += "J";

		return selected;
	}

	public static void setWrapper(WrapperController wrapperController) {
		wrapper = wrapperController;
	}
}
