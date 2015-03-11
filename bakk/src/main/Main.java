package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Main extends Application{
	static AnchorPane wrapper;

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
