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
	
	@FXML
	public void onShowButton(){
		System.out.println("test");
	}
}
