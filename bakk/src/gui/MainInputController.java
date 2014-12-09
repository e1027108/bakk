package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import dto.ArgumentDto;
import exceptions.InvalidInputException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;


public class MainInputController {
	
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
	private Label addLbl, attackLbl, headlineLbl, aLbl, bLbl, cLbl, dLbl, eLbl, fLbl, gLbl, hLbl, iLbl, jLbl;

	@FXML
	private Button showGraphBtn;

	// TODO create hover over explanations

	private ArrayList<ArgumentDto> arguments;

	@FXML
	void initialize() {
		/* if needed add assertions (get them from scene builder) */

		//TODO later fix window size and possible scaling problems

		arguments = new ArrayList<ArgumentDto>();
	}

	@FXML
	public void onShowButton() throws InvalidInputException{ //TODO handle exceptions thrown by @FXML annotated methods
		if(!argumentATxt.getText().isEmpty() || !attackATxt.getText().isEmpty()){
			arguments.add(new ArgumentDto('A', parseArgument(argumentATxt), parseAttacks(attackATxt)));
		}
		if(!argumentBTxt.getText().isEmpty() || !attackBTxt.getText().isEmpty()){
			arguments.add(new ArgumentDto('B', parseArgument(argumentBTxt), parseAttacks(attackBTxt)));
		}
		if(!argumentCTxt.getText().isEmpty() || !attackCTxt.getText().isEmpty()){
			arguments.add(new ArgumentDto('C', parseArgument(argumentCTxt), parseAttacks(attackCTxt)));
		}
		if(!argumentDTxt.getText().isEmpty() || !attackDTxt.getText().isEmpty()){
			arguments.add(new ArgumentDto('D', parseArgument(argumentDTxt), parseAttacks(attackDTxt)));
		}
		if(!argumentETxt.getText().isEmpty() || !attackETxt.getText().isEmpty()){
			arguments.add(new ArgumentDto('E', parseArgument(argumentETxt), parseAttacks(attackETxt)));
		}
		if(!argumentFTxt.getText().isEmpty() || !attackFTxt.getText().isEmpty()){
			arguments.add(new ArgumentDto('F', parseArgument(argumentFTxt), parseAttacks(attackFTxt)));
		}
		if(!argumentGTxt.getText().isEmpty() || !attackGTxt.getText().isEmpty()){
			arguments.add(new ArgumentDto('G', parseArgument(argumentGTxt), parseAttacks(attackGTxt)));
		}
		if(!argumentHTxt.getText().isEmpty() || !attackHTxt.getText().isEmpty()){
			arguments.add(new ArgumentDto('H', parseArgument(argumentHTxt), parseAttacks(attackHTxt)));
		}
		if(!argumentITxt.getText().isEmpty() || !attackITxt.getText().isEmpty()){
			arguments.add(new ArgumentDto('I', parseArgument(argumentITxt), parseAttacks(attackITxt)));
		}
		if(!argumentJTxt.getText().isEmpty() || !attackJTxt.getText().isEmpty()){
			arguments.add(new ArgumentDto('J', parseArgument(argumentJTxt), parseAttacks(attackJTxt)));
		}

		//TODO replace window with demonstrationwindow, send info there, show it
		
	}

	private String parseArgument(TextField argument) {
		if(argument.getText().isEmpty()){
			return "no argument description";
		}
		return argument.getText();
	}

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
				}
				else if(input.contains(stringValue.toLowerCase())){ //check for lowercase
					attackValues += stringValue;
					input = input.replace(stringValue.toLowerCase(), "");
				}
			}
		}

		if(input.length() > 0){
			throw new InvalidInputException("Invalid or duplicate attacks detected.");
		}

		return attackValues;
	}
}
