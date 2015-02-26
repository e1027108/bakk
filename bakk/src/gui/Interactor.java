package gui;

import java.util.LinkedList;

import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

public class Interactor {
	
	private TextArea textArea;
	private Button nextButton, backButton, skipButton;
	private LinkedList<String> storedMessages;
	
	public Interactor(TextArea textArea, Button nextButton, Button backButton, Button skipButton){ //TODO add graphical node representation
		this.textArea = textArea;
		this.nextButton = nextButton;
		this.backButton = backButton;
		this.skipButton = skipButton;
		storedMessages = new LinkedList<String>();
	}
	
	public void overwrite(){
		if(!storedMessages.isEmpty()){
			textArea.setText(storedMessages.pollLast());
		}
	}
	
	public void printLine(){
		if(!storedMessages.isEmpty()){
			if(!textArea.getText().isEmpty()){
				textArea.setText(textArea.getText() + "\n" + storedMessages.pollLast());
			}
			else{
				overwrite();
			}
		}
	}
	
	public void printAllLines(){
		while(!storedMessages.isEmpty()){
			printLine();
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
		LinkedList<String> tmp = new LinkedList<String>();
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
