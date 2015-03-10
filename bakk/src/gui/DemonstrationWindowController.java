package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import dto.ArgumentDto;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import logic.Argument;
import logic.Extension;
import logic.Framework;

public class DemonstrationWindowController {

	private static WrapperController wrapper;

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private Button backButton, nextButton, showAllButton, arrowButton, completeBtn, preferredBtn, stableBtn, groundedBtn, conflictFreeBtn, admissibleBtn;

	@FXML
	private CheckBox previousCheckBox;

	@FXML
	private TextArea explanationArea;

	@FXML
	private AnchorPane graphPane;

	@FXML
	private AnchorPane root;

	private Framework argumentFramework;
	private ArrayList<Argument> arguments;
	private Interactor interactor;
	private ArrayList<Extension> resultSet;
	
	@FXML
	void initialize() {		
		interactor = Interactor.getInstance(explanationArea); //TODO add parameters later (for nodes)
		readArguments(interactor.getRawArguments());
		argumentFramework = new Framework(arguments, interactor);
		
		
	}

	private void readArguments(ArrayList<ArgumentDto> rawArguments) {
		arguments = new ArrayList<Argument>();
		
		if(null != rawArguments && !rawArguments.isEmpty()){
			for(ArgumentDto a: rawArguments){
				arguments.add(new Argument(a.getName(), a.getStatement(), a.getAttacks()));
			}
		}
	}

	@FXML
	public void onConflictFreeClick() {
		interactor.emptyQueue();
		
		resultSet = argumentFramework.getConflictFreeSets();

		//printExtensions(resultSet);

		setUI();
	}
	
	@FXML
	public void onAdmissibleClick(){
		interactor.emptyQueue();

		resultSet = argumentFramework.getAdmissibleSets(previousCheckBox.isSelected());

		//printExtensions(resultSet);

		setUI();
	}

	@FXML
	public void onCompleteClick(){
		interactor.emptyQueue();

		resultSet = argumentFramework.getCompleteExtensions(previousCheckBox.isSelected());

		//printExtensions(resultSet);

		setUI();
	}

	@FXML
	public void onPreferredClick(){
		interactor.emptyQueue();

		resultSet = argumentFramework.getPreferredExtensions(previousCheckBox.isSelected());

		//printExtensions(resultSet);

		setUI();
	}

	@FXML
	public void onStableClick(){
		interactor.emptyQueue();

		resultSet = argumentFramework.getStableExtensions(previousCheckBox.isSelected());

		//printExtensions(resultSet);

		setUI();
	}

	@FXML
	public void onGroundedClick(){
		interactor.emptyQueue();

		Extension grounded = argumentFramework.getGroundedExtension(previousCheckBox.isSelected());

		/*if(grounded != null){
			System.out.println("{" + grounded.getArgumentNames() + "}");
		}*/

		setUI();
	}

	@FXML
	public void onBackClick(){
		interactor.removeLine();

		if(explanationArea.getText().isEmpty()){
			backButton.setDisable(true);
		}

		showAllButton.setDisable(false);
		nextButton.setDisable(false);
	}

	@FXML
	public void onNextClick(){
		interactor.printLine();

		backButton.setDisable(false);

		if(interactor.hasNext()){
			disableForwardButtons();
		}
	}

	@FXML
	public void onShowAllClick(){
		interactor.printAllLines();

		backButton.setDisable(false);

		if(interactor.hasNext()){
			disableForwardButtons();
		}
	}

	public void disableForwardButtons(){
		nextButton.setDisable(true);
		showAllButton.setDisable(true);
	}

	@FXML
	public void onArrowClick(){
		wrapper.loadMain();
	}

	public void setUI(){
		explanationArea.setText("");
		nextButton.setDisable(false);
		showAllButton.setDisable(false);
		interactor.printLine();
	}

	public void setArguments(ArrayList<ArgumentDto> arguments){
		for(ArgumentDto a: arguments){
			this.arguments.add(new Argument(a.getName(),"","")); //TODO replace strings with something that makes sense
		}

		System.out.println("erfolg");
	}

	public static void setWrapper(WrapperController wrapperController){
		wrapper = wrapperController;
	}
	
	//for testing
	public void printExtensions(ArrayList<Extension> ext){
		for(Extension e: ext){
			System.out.println("{" + e.getArgumentNames() + "}");
		}
	}
}
