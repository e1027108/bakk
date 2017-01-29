package interactor;

/**
 * A Command contains an instruction for a graph and a message
 * @author Patrick Bellositz
 */
public class Command {
	
	private String message; //the command's message
	private GraphInstruction instruction; //the instruction of the command
	private int pane; //where the command is applied in the gui

	/**
	 * Command constructor, taking a message and an instruction
	 * @param message is a message explaining what the instruction shows
	 * @param instruction is a set of nodes and edges and information how they should be colored
	 */
	public Command(String message, GraphInstruction instruction, int pane) {
		this.message = message;
		this.instruction = instruction;
		this.pane = pane;
	}

	/**
	 * @return the command message
	 */
	public String getText() {
		return message;
	}
	
	/**
	 * @return the command's instruction
	 */
	public GraphInstruction getInstruction() {
		return instruction;
	}
	
	public int getPane(){
		return pane;
	}

}
