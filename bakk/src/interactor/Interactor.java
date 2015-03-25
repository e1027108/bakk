package interactor;

import gui.DemonstrationWindowController;

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
	private LinkedList<Command> storedCommands; //queue storing the messages to be shown to the user
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

		storedCommands = new LinkedList<Command>();
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
	private void overwrite(){
		if(!storedCommands.isEmpty()){
			textArea.setText(storedCommands.pollLast().getText());
			scrollDown();
		}
	}

	/**
	 * adds the queued message to the textarea's text
	 */
	public void printLine(){
		if(!storedCommands.isEmpty()){
			if(!textArea.getText().isEmpty()){
				textArea.setText(textArea.getText() + "\n" + storedCommands.pollLast().getText());
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
		while(!storedCommands.isEmpty()){
			printLine();
		}
		scrollDown();
	}
	
	public void skipToLastLine() {
		if(!storedCommands.isEmpty()){
			textArea.setText(storedCommands.getFirst().getText());
			emptyQueue();
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
				storedCommands.addLast(new Command(tmp.substring(tmp.lastIndexOf('\n'), tmp.length()).replace("\n", ""), null/*TODO instruction*/));
				textArea.setText(tmp.substring(0,tmp.lastIndexOf('\n')));
				scrollDown();
			}
			else{
				storedCommands.addLast(new Command(tmp, null/*TODO instruction*/));
				textArea.setText("");
			}
		}
	}

	public void manipulateGraph(){
		//TODO use to manipulate graph or replace with other method(s)
	}

	/**
	 * adds a message to the end of the queue
	 * @param message the message added to the end of the queue
	 */
	public void addToCommands(Command command){
		storedCommands.push(command); //TODO fix compiling problems resulting from rewriting this method
	}

	/**
	 * deletes all the contents from the queue
	 */
	public void emptyQueue(){
		storedCommands = new LinkedList<Command>();
	}

	/**
	 * checks if the queue holds elements
	 * @return whether there are still elements in the queue
	 */
	public boolean hasNext(){
		return !(storedCommands.size()>0);
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
