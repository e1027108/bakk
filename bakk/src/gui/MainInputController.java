package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

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
	
	private ArrayList<TextField> arguments, attacks;

	@FXML
	void initialize() {
		/* if needed add assertions (get them from scene builder) */
		
		//TODO fix window size and possible scaling problems
		
		arguments = new ArrayList<TextField>();
		attacks = new ArrayList<TextField>();
		
		arguments.add(argumentATxt);
		arguments.add(argumentBTxt);
		arguments.add(argumentCTxt);
		arguments.add(argumentDTxt);
		arguments.add(argumentETxt);
		arguments.add(argumentFTxt);
		arguments.add(argumentGTxt);
		arguments.add(argumentHTxt);
		arguments.add(argumentITxt);
		arguments.add(argumentJTxt);
		
		attacks.add(attackATxt);
		attacks.add(attackBTxt);
		attacks.add(attackCTxt);
		attacks.add(attackDTxt);
		attacks.add(attackETxt);
		attacks.add(attackFTxt);
		attacks.add(attackGTxt);
		attacks.add(attackHTxt);
		attacks.add(attackITxt);
		attacks.add(attackJTxt);
	}

}
