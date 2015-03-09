package gui;

import java.io.IOException;
import java.util.ArrayList;

import dto.ArgumentDto;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

public class WrapperController {
	@FXML
	private AnchorPane root, mainPane, demonstrationPane;

	@FXML
	void initialize(){
		loadMain();
	}

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
