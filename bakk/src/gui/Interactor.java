package gui;

import java.util.ArrayList;
import java.util.LinkedList;

import dto.ArgumentDto;
import javafx.scene.control.TextArea;

/**
 * Class connecting information transfer between user and program
 * 	as well as between the different UI scenes
 * @author Patrick Bellositz
 */
public class Interactor {
	/**
	 * the only (singleton) instance of the Interactor class
	 */
	private static Interactor singleton;

	private TextArea textArea; //the textArea controlled by the Interactor
	private DemonstrationWindowController controller;
	private LinkedList<String> storedMessages; //queue storing the messages to be shown to the user
	private ArrayList<ArgumentDto> rawArguments; //ArgumentDtos stored for further use in an argument Framework

	/**
	 * creates an interactor, responsible for interaction between logic, input and output
	 * @param textArea the textarea to be read by the user
	 */
	private Interactor(DemonstrationWindowController controller){ //TODO add graphical node representation
		this.controller = controller;

		if(controller != null){
			this.textArea = this.controller.getTextArea();
		}

		storedMessages = new LinkedList<String>();
	}

	/**
	 * static method responsible for only creating a single Interactor for all objects
	 * @param textArea the textArea into which the interactor writes
	 * @return the only instance of the Interactor
	 */
	public static Interactor getInstance(DemonstrationWindowController controller){
		if(singleton == null){
			singleton = new Interactor(controller);
		}

		//to be able to get a textarea into the interactor even after it was instantiated
		if(singleton.controller == null && controller != null){
			singleton.controller = controller;
			singleton.textArea = controller.getTextArea();
		}

		return singleton;
	}

	/**
	 * prints the first stored message into the textarea, overwriting its previous contents
	 */
	public void overwrite(){
		if(!storedMessages.isEmpty()){
			textArea.setText(storedMessages.pollLast());
			scrollDown();
		}
	}

	/**
	 * adds the queued message to the textarea's text
	 */
	public void printLine(){
		if(!storedMessages.isEmpty()){
			if(!textArea.getText().isEmpty()){
				textArea.setText(textArea.getText() + "\n" + storedMessages.pollLast());
				scrollDown();
			}
			else{
				overwrite();
			}
		}
	}

	/**
	 * adds all remaining messages in the queue to the textarea
	 */
	public void printAllLines(){
		while(!storedMessages.isEmpty()){
			printLine();
		}
		scrollDown();
	}

	/**
	 * scrolls the textArea to the bottom
	 */
	public void scrollDown(){
		textArea.setScrollTop(Double.MAX_VALUE);
		textArea.appendText("");
	}

	/**
	 * removes the last line from the textarea and places it first in the queue
	 */
	public void removeLine(){
		String tmp = textArea.getText();

		if(!tmp.isEmpty()){
			if(tmp.contains("\n")){
				storedMessages.addLast(tmp.substring(tmp.lastIndexOf('\n'), tmp.length()).replace("\n", ""));
				textArea.setText(tmp.substring(0,tmp.lastIndexOf('\n')));
				scrollDown();
			}
			else{
				storedMessages.addLast(tmp);
				textArea.setText("");
			}
		}
	}

	public void manipulateNodes(){
		//TODO will be used to interact with graphical node representation
	}

	/**
	 * adds a message to the end of the queue
	 * @param message the message added to the end of the queue
	 */
	public void addToStoredMessages(String message){
		storedMessages.push(message);
	}

	/**
	 * deletes all the contents from the queue
	 */
	public void emptyQueue(){
		storedMessages = new LinkedList<String>();
	}

	/**
	 * checks if the queue holds elements
	 * @return whether there are still elements in the queue
	 */
	public boolean hasNext(){
		return !(storedMessages.size()>0);
	}

	/**
	 * stores the ArgumentDtos
	 * @param arguments the ArgumentDtos to be stored
	 */
	public void setRawArguments(ArrayList<ArgumentDto> arguments) {
		rawArguments = new ArrayList<ArgumentDto>();
		rawArguments.addAll(arguments);
	}

	/**
	 * @return the ArgumentDtos stored for further use
	 */
	public ArrayList<ArgumentDto> getRawArguments() {
		return rawArguments;
	}

	/**
	 * resets the demonstrationValues and loads the current interactor values
	 */
	public void setDemonstrationValues() {
		if(controller != null){
			controller.setInitialValues();
		}
	}
}
