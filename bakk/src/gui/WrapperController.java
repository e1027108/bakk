package gui;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

public class WrapperController {
	@FXML
	private AnchorPane root, mainPane, demonstrationPane; //panes that can be shown and the root pane

	/**
	 * at program start shows the main input window
	 */
	@FXML
	void initialize(){
		loadMain();
	}

	/**
	 * loads the main input file and shows it on screen
	 */
	@FXML
	public void loadMain() {
		if(mainPane == null){
			try {
				mainPane = FXMLLoader.load(getClass().getResource("/MainInput.fxml"));
				MainInputController.setWrapper(this);
			} catch (IOException e) {
				e.printStackTrace(); //TODO handle
			}
		}

		root.getChildren().setAll(mainPane);
	}

	/**
	 * loads the demonstration window file and shows it on screen
	 */
	@FXML
	public void loadDemonstration(){
		if(demonstrationPane == null){
			try {
				demonstrationPane = FXMLLoader.load(getClass().getResource("/DemonstrationWindow.fxml"));
				DemonstrationWindowController.setWrapper(this);
			} catch (IOException e) {
				e.printStackTrace(); //TODO handle
			}
		}
		
		root.getChildren().setAll(demonstrationPane);
	}
}
