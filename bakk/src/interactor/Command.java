package interactor;

public class Command {
	
	private String message;
	private GraphInstruction instruction;

	public Command(String message, GraphInstruction instruction) {
		this.message = message;
		this.instruction = instruction;
	}

	public String getText() {
		return message;
	}
	
	public GraphInstruction getInstruction() {
		return instruction;
	}

}
