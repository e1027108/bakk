package gui;

import javafx.scene.control.TextArea;

public class Interactor {
	
	private TextArea textArea;
	
	public Interactor(TextArea textArea){ //TODO add graphical node representation
		this.textArea = textArea;
	}
	
	public void overwrite(String msg){
		textArea.setText(msg);
	}
	
	public void write(String msg){
		textArea.setText(textArea.getText() + "\n" + msg);
	}
	
	public void manipulateNodes(){
		//TODO will be used to interact with graphical node representation
	}
}
