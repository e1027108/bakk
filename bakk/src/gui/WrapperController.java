package gui;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

/**
 * Controller controlling the Wrapper.fxml file behaviour
 * 	for controlling which other scenes are shown on screen
 * @author Patrick Bellositz
 */
public class WrapperController {
	@FXML
	private AnchorPane root, mainPane, demonstrationPane, contentPane; //panes that can be shown and the root pane (their parent)
	//TODO let the WrapperController hold all examples for both window types
	
	@FXML
	private Hyperlink paperLink;
	
	@FXML
	private Label descriptionLbl;
	
	private String description;

	/**
	 * at program start shows the main input window
	 */
	@FXML
	void initialize(){
		description = "Not sure what to refer to here";
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
				initializeLabels();
			} catch (IOException e) {
				descriptionLbl.setStyle("-fx-text-fill: red;");
				descriptionLbl.setText("Could not load main input window!");
			}
		}

		contentPane.getChildren().setAll(mainPane);
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
				descriptionLbl.setStyle("-fx-text-fill: red;");
				descriptionLbl.setText("Could not load demonstration window!");
				e.printStackTrace();
				return;
			}
		}

		contentPane.getChildren().setAll(demonstrationPane);
	}

	@FXML
	public void onLinkClick(){
		try{
			java.awt.Desktop.getDesktop().browse(new URI(paperLink.getText()));
		} catch(IOException | URISyntaxException e){
			paperLink.setDisable(true);
			descriptionLbl.setStyle("-fx-text-fill: red;");
			descriptionLbl.setText("Link could not be loaded!");
		}
	}
	
	public void initializeLabels(){
		paperLink.setText("unsure about link");
		paperLink.setDisable(false);
		descriptionLbl.setText(description);
		descriptionLbl.setStyle("-fx-text-fill: black;");
	}
}