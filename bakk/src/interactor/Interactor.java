package interactor;

import gui.DemonstrationWindowController;
import gui.NodePane;

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
	private NodePane graph; //the anchorpane in which the graph is drawn
	private DemonstrationWindowController controller;
	private LinkedList<Command> storedCommands; //queue storing the messages to be shown to the user
	private LinkedList<Command> history;
	private ArrayList<ArgumentDto> rawArguments; //ArgumentDtos stored for further use in an argument Framework

	/**
	 * creates an interactor, responsible for interaction between logic, input and output
	 * @param textArea the textarea to be read by the user
	 */
	private Interactor(DemonstrationWindowController controller){
		this.controller = controller;

		if(controller != null){
			this.textArea = this.controller.getTextArea();
			this.graph = this.controller.getGraphPane();
		}

		storedCommands = new LinkedList<Command>();
		history = new LinkedList<Command>();
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
		else{ //to be able to get a textarea into the interactor even after it was instantiated
			singleton.controller = controller;
			singleton.textArea = controller.getTextArea();
			singleton.graph = controller.getGraphPane();
		}

		return singleton;
	}

	/**
	 * prints the first stored message into the textarea, overwriting its previous contents
	 */
	private void overwrite(){
		if(!storedCommands.isEmpty()){
			Command cmd = storedCommands.pollLast();
			
			textArea.setText(cmd.getText());
			manipulateGraph(cmd.getInstruction());
			scrollDown();
			
			history.push(cmd);
		}
	}

	/**
	 * adds the queued message to the textarea's text
	 */
	public void executeNextCommand(){
		if(!storedCommands.isEmpty()){
			if(!textArea.getText().isEmpty()){
				Command cmd = storedCommands.pollLast();
				
				textArea.setText(textArea.getText() + "\n" + cmd.getText());
				scrollDown();
				
				manipulateGraph(cmd.getInstruction());
				
				history.push(cmd);
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
			executeNextCommand();
		}
		scrollDown();
	}
	
	public void skipToLastCommand() { //no push to history!
		if(!storedCommands.isEmpty()){
			Command cmd = storedCommands.getFirst();
			
			textArea.setText(cmd.getText());
			manipulateGraph(cmd.getInstruction());
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
	 * removes the last line from the textArea and places it first in the queue
	 */
	public void revertCommand(){
		String tmp = textArea.getText();

		if(history.isEmpty()){
			return;
		}
		
		if(!tmp.isEmpty()){
			if(tmp.contains("\n")){
				textArea.setText(tmp.substring(0,tmp.lastIndexOf('\n')));
				storedCommands.addLast(history.pollFirst());
				manipulateGraph(history.peekFirst().getInstruction());
				scrollDown();
			}
			else{
				storedCommands.addLast(history.pollFirst());
				textArea.setText("");
			}
		}
	}

	public void manipulateGraph(GraphInstruction instruction){
		graph.executeInstruction(instruction);
	}

	/**
	 * adds a message to the end of the queue
	 * @param message the message added to the end of the queue
	 */
	public void addToCommands(Command command){
		storedCommands.push(command);
	}

	/**
	 * deletes all the contents from the queue
	 */
	public void emptyQueue(){
		storedCommands = new LinkedList<Command>();
		history = new LinkedList<Command>();
	}

	/**
	 * checks if the queue holds elements
	 * @return whether there are still elements in the queue
	 */
	public boolean hasNext(){
		return (storedCommands.size()>0);
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
