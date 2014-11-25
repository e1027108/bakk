package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import dto.ArgumentDto;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;


public class MainInputController {

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
	
	private ArrayList<ArgumentDto> arguments;
	
	@FXML
	void initialize() {
		/* if needed add assertions (get them from scene builder) */
		
		//TODO later fix window size and possible scaling problems
		
		arguments = new ArrayList<ArgumentDto>();
	}
	
	//TODO maybe change from A...J to A0...A9
	@FXML
	public void onShowButton(){
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
		
		//TODO create graphwindow, send info there, open it (instead of maininput, but in the same window)
	}
	
	private String parseArgument(TextField argument) {
		if(argument.getText().isEmpty()){
			return "no argument description";
		}
		return argument.getText();
	}

	private String parseAttacks(TextField attack) {
		if(attack.getText().isEmpty()){
			return "";
		}
		else{
			String argumentNames = "ABCDEFGHIJ";
			
			//TODO remove everything like separators, but throw exception if there are invalid letters
			return null; //TODO replace null with something
		}
	}

}
