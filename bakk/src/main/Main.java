package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * The Abstract Argumentation Framework program enables the user to
 *  create abstract argumentation frameworks, compute their extensions
 *  and to view a step-by-step guide through the computation process
 * @author Patrick Bellositz
 */
public class Main extends Application{

	private static AnchorPane wrapper; //the wrapper pane for all subsequently shown panes (i.e. input, demonstration)
	
	/**
	 * main method
	 * @param args arguments from terminal
	 */
	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * loads the Wrapper file creates a scene for it and shows it on screen
	 */
	@Override
	public void start(Stage stage) throws Exception {

		String fxmlName = "/Wrapper.fxml";
		
		try{
			wrapper = FXMLLoader.load(getClass().getResource(fxmlName));
		} catch (Exception e){
			e.printStackTrace();
			System.exit(-1);
		}

		Scene scene = new Scene(wrapper);
		stage.setScene(scene);
		stage.setTitle("Abstract argumentation frameworks");
		stage.setResizable(false);
		stage.centerOnScreen();
		stage.show();
	}
}
