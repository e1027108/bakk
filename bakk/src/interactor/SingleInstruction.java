package interactor;

import javafx.scene.paint.Color;

public class SingleInstruction {

	private static enum InstructionType {EDGEINSTRUCTION, NODEINSTRUCTION};
	public static final InstructionType NODEINSTRUCTION = InstructionType.NODEINSTRUCTION;
	public static final InstructionType EDGEINSTRUCTION = InstructionType.EDGEINSTRUCTION;
	private String name;
	private Color color;
	private InstructionType type;
	
	public SingleInstruction(String name, Color color){
		setName(name);
		this.color = color;
	}
	
	public void setName(String name){
		if(name.length() == 1){
			type = NODEINSTRUCTION;
		}
		else if(name.length() == 2){
			type = EDGEINSTRUCTION;
		}
		else{
			throw new IllegalArgumentException("Instruction name doesn't fit an edge or node name. (2 or 1 letters)");
		}
	}
	
	public String getName(){
		return name;
	}
	
	public InstructionType getType(){
		return type;
	}
	
	public Color getColor(){
		return color;
	}
}
