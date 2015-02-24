package gui;

import java.util.Stack;

import javafx.scene.control.TextArea;

public class Interactor {
	
	private TextArea textArea;
	private Stack<String> storedMessages;
	
	public Interactor(TextArea textArea){ //TODO add graphical node representation
		this.textArea = textArea;
		storedMessages = new Stack<String>();
	}
	
	public void overwrite(){
		if(!storedMessages.isEmpty()){
			textArea.setText(storedMessages.pop());
		}
	}
	
	public void addLine(){
		if(!storedMessages.isEmpty()){
			if(!textArea.getText().isEmpty()){
				textArea.setText(textArea.getText() + "\n" + storedMessages.pop());
			}
			else{
				overwrite();
			}
		}
	}
	
	public void removeLine(){
		String tmp = textArea.getText();
		
		if(!tmp.isEmpty()){
			if(tmp.contains("\n")){
				addToStoredMessages(tmp.substring(tmp.lastIndexOf('\n'), tmp.length()).replace("\n", ""));
				textArea.setText(tmp.substring(0,tmp.lastIndexOf('\n')));
			}
			else{
				addToStoredMessages(tmp);
				textArea.setText("");
			}
		}
	}
	
	//TODO make more efficient
	public void addToStart(String message){
		Stack<String> tmp = new Stack<String>();
		tmp.push(message);
		tmp.addAll(storedMessages);
		storedMessages = tmp;
	}
	
	public void manipulateNodes(){
		//TODO will be used to interact with graphical node representation
	}
	
	public void addToStoredMessages(String message){
		storedMessages.push(message);
	}
}
