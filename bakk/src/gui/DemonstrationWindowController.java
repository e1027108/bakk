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
	
	/**
	 * initializes the controller
	 * @details gets the interactor, reads arguments from it,
	 * 			creates the Framework and sets tooltips
	 */
	@FXML
	void initialize() {		
		interactor = Interactor.getInstance(explanationArea); //TODO add parameters later (for nodes)
		readArguments(interactor.getRawArguments());
		argumentFramework = new Framework(arguments, interactor);
		
		//TODO tooltips
	}

	/**
	 * converts Arguments from ArgumentDtos and saves them
	 * @param rawArguments a list of ArgumentDtos
	 */
	private void readArguments(ArrayList<ArgumentDto> rawArguments) {
		arguments = new ArrayList<Argument>();
		
		if(null != rawArguments && !rawArguments.isEmpty()){
			for(ArgumentDto a: rawArguments){
				arguments.add(new Argument(a.getName(), a.getStatement(), a.getAttacks()));
			}
		}
	}

	/**
	 * initiates the computation of the conflict free sets of the framework
	 */
	@FXML
	public void onConflictFreeClick() {
		interactor.emptyQueue();
		
		resultSet = argumentFramework.getConflictFreeSets();

		//printExtensions(resultSet);

		setUI();
	}
	
	/**
	 * initiates the computation of the admissible sets of the framework
	 */
	@FXML
	public void onAdmissibleClick(){
		interactor.emptyQueue();

		resultSet = argumentFramework.getAdmissibleSets(previousCheckBox.isSelected());

		//printExtensions(resultSet);

		setUI();
	}

	/**
	 * initiates the computation of the complete extensions of the framework
	 */
	@FXML
	public void onCompleteClick(){
		interactor.emptyQueue();

		resultSet = argumentFramework.getCompleteExtensions(previousCheckBox.isSelected());

		//printExtensions(resultSet);

		setUI();
	}

	/**
	 * initiates the computation of the preferred extensions of the framework
	 */
	@FXML
	public void onPreferredClick(){
		interactor.emptyQueue();

		resultSet = argumentFramework.getPreferredExtensions(previousCheckBox.isSelected());

		//printExtensions(resultSet);

		setUI();
	}

	/**
	 * initiates the computation of the stable extensions of the framework
	 */
	@FXML
	public void onStableClick(){
		interactor.emptyQueue();

		resultSet = argumentFramework.getStableExtensions(previousCheckBox.isSelected());

		//printExtensions(resultSet);

		setUI();
	}

	/**
	 * initiates the computation of the grounded extension of the framework
	 */
	@FXML
	public void onGroundedClick(){
		interactor.emptyQueue();

		Extension grounded = argumentFramework.getGroundedExtension(previousCheckBox.isSelected());

		/*if(grounded != null){
			System.out.println("{" + grounded.getArgumentNames() + "}");
		}*/

		setUI();
	}

	/**
	 * moves the output of the computation one step forward
	 */
	@FXML
	public void onNextClick(){
		interactor.printLine();

		backButton.setDisable(false);

		if(interactor.hasNext()){
			disableForwardButtons();
		}
		
		//TODO show node change
	}
	
	/**
	 * moves the output of the computation one step back
	 */
	@FXML
	public void onBackClick(){
		interactor.removeLine();

		if(explanationArea.getText().isEmpty()){
			backButton.setDisable(true);
		}

		showAllButton.setDisable(false);
		nextButton.setDisable(false);
		
		//TODO revert node change
	}

	/**
	 * shows the complete computation process in text
	 */
	@FXML
	public void onShowAllClick(){
		interactor.printAllLines();

		backButton.setDisable(false);

		if(interactor.hasNext()){
			disableForwardButtons();
		}
	}

	/**
	 * disables the next and show all buttons
	 */
	public void disableForwardButtons(){
		nextButton.setDisable(true);
		showAllButton.setDisable(true);
	}

	/**
	 * returns to main input window
	 */
	@FXML
	public void onArrowClick(){
		wrapper.loadMain();
	}

	/**
	 * brings the UI into a state where viewing of the computation process is possible
	 */
	public void setUI(){
		explanationArea.setText("");
		nextButton.setDisable(false);
		showAllButton.setDisable(false);
		interactor.printLine();
	}

	/**
	 * sets the wrapper for all windows to apply to this controller's window
	 * @param wrapperController the wrapper controlling which window is shown
	 */
	public static void setWrapper(WrapperController wrapperController){
		wrapper = wrapperController;
	}
	
	/**
	 * prints the arguments of all computed extensions (only for testing)
	 * @param ext the extensions to be printed
	 */
	//TODO remove some time
	public void printExtensions(ArrayList<Extension> ext){
		for(Extension e: ext){
			System.out.println("{" + e.getArgumentNames() + "}");
		}
	}
}
