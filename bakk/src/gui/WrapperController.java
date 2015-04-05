package gui;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import main.Main;

import com.sun.deploy.uitoolkit.impl.fx.HostServicesFactory;

import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.AnchorPane;

/**
 * Controller controlling the Wrapper.fxml file behaviour
 * 	for controlling which other scenes are shown on screen
 * @author Patrick Bellositz
 */
public class WrapperController {
	@FXML
	private AnchorPane root, mainPane, demonstrationPane, contentPane; //panes that can be shown and the root pane (their parent)
	
	@FXML
	private Hyperlink paperLink;

	/**
	 * at program start shows the main input window
	 */
	@FXML
	void initialize(){
		paperLink.setText("https://github.com/e1027108/bakk");
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
				e.printStackTrace(); //TODO handle
			}
		}
		
		contentPane.getChildren().setAll(demonstrationPane);
	}
	
	@FXML
	public void onLinkClick() throws IOException, URISyntaxException{ //TODO handle
		java.awt.Desktop.getDesktop().browse(new URI(paperLink.getText()));
	}
}
