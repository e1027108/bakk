package main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class Main extends Application{
	static AnchorPane mainInput;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {

		/* TODO remove after testing and connecting windows */
		String fxmlName = "/DemonstrationWindow.fxml"; //alt: "/MainInput.fxml"
		
		try{
			mainInput = FXMLLoader.load(getClass().getResource(fxmlName));
		} catch (Exception e){
			e.printStackTrace();
			System.exit(-1);
		}

		Scene scene = new Scene(mainInput);
		stage.setScene(scene);
		stage.setTitle("Abstract argumentation frameworks");
		stage.setResizable(false);
		stage.centerOnScreen();
		stage.show();
	}
}
