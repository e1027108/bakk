package interactor;

import gui.DemonstrationWindowController;
import gui.NodePane;

import java.util.ArrayList;
import java.util.LinkedList;

import dto.ArgumentDto;
import exceptions.InvalidInputException;
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
	private NodePane comparisonGraph; //a comparison to the first graph
	private DemonstrationWindowController controller; //the controller class of the graph and textArea window
	private LinkedList<Command> storedCommands; //queue storing the commands to be executed (graph/textarea changes)
	private LinkedList<Command> history; //queue storing previously executed commands
	private ArrayList<ArgumentDto> rawArguments; //ArgumentDtos stored for further use in an argument Framework

	/**
	 * creates an interactor, responsible for interaction between logic, input and output
	 * @param controller the controller containing the textarea and nodepane needed to show
	 * 		command content (text area messages and graphical changes)
	 */
	private Interactor(DemonstrationWindowController controller){
		this.controller = controller;

		if(controller != null){
			this.textArea = controller.getTextArea();
			this.graph = controller.getGraphPane();
			this.comparisonGraph = controller.getComparisonPane();
		}

		storedCommands = new LinkedList<Command>();
		history = new LinkedList<Command>();
	}

	/**
	 * static method responsible for only creating a single Interactor for all objects
	 * @param controller the controller containing the textarea and nodepane needed to show
	 * 		command content (text area messages and graphical changes)
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
	 * executes the first stored command effectively overwriting all
	 * contents of the textArea with a single message
	 */
	private void overwrite(){
		if(!storedCommands.isEmpty()){
			Command cmd = storedCommands.pollLast();

			textArea.setText(cmd.getText());

			GraphInstruction cmdInst = cmd.getInstruction();

			manipulateGraph(cmdInst);
			scrollDown();

			history.push(cmd);
		}
	}

	/**
	 * adds the queued message to the textarea's text and 
	 * changes the graph correspondingly
	 */
	public void executeNextCommand(){
		if(!storedCommands.isEmpty()){
			if(!textArea.getText().isEmpty()){
				Command cmd = storedCommands.pollLast();

				textArea.setText(textArea.getText() + "\n" + cmd.getText());
				scrollDown();

				GraphInstruction cmdInst = cmd.getInstruction();

				manipulateGraph(cmdInst);

				history.push(cmd);
			}
			else{
				overwrite();
			}
		}
	}

	/**
	 * adds all remaining messages in the queue to the textarea,
	 * and (effectively) only showing the last instruction in the graph
	 */
	public void printAllLines(){
		while(!storedCommands.isEmpty()){
			executeNextCommand();
		}
		scrollDown();
	}

	/**
	 * executes the last instruction and prints the last line contained in the queue
	 */
	public void skipToLastCommand() { //no push to history!
		if(!storedCommands.isEmpty()){
			Command cmd = storedCommands.getFirst();

			textArea.setText(cmd.getText());

			GraphInstruction cmdInst = cmd.getInstruction();

			manipulateGraph(cmdInst);
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
	 * re-applies the last command before the current command (from history), moves current command on top of queue
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
				GraphInstruction peek = history.peekFirst().getInstruction();
				manipulateGraph(peek);
				scrollDown();
			}
			else{
				storedCommands.addLast(history.pollFirst());
				textArea.setText("");
			}
		}
	}

	/**
	 * sends the instruction to the NodePane or comparisonPane, where it is executed
	 * @param instruction the instruction to be executed
	 * @param pane 1 for graph, 2 for comparison graph
	 */
	public void manipulateGraph(GraphInstruction instruction){
		try {
			if(instruction != null){
				if(instruction.getPane() == 1){
					controller.conditionalToggle(1);
					graph.executeInstruction(instruction);
				}
				else if(instruction.getPane() == 2){
					controller.conditionalToggle(2);
					comparisonGraph.executeInstruction(instruction);
				}
				else if(instruction.getPane() == 0){
					//do nothing
				}
				else {
					throw new InvalidInputException("Not an existing pane!");
				}
			}
		} catch (InvalidInputException e) {
			emptyQueue();
			textArea.setText(e.getMessage() + " The graph could not be displayed!");
		}
	}

	/**
	 * adds a command to the end of the queue
	 * @param command the command added to the end of the queue
	 */
	public void addToCommands(Command command){
		storedCommands.push(command);
	}

	/**
	 * deletes all the contents from the queue (and the history)
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
	
	public void updateComparisonGraph() {
		comparisonGraph = controller.getComparisonPane();
	}
	
	public void updateGraph(){
		graph = controller.getGraphPane();
	}
	
}
