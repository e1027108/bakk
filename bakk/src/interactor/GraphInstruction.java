package interactor;

import java.util.ArrayList;

public class GraphInstruction {

	ArrayList<SingleInstruction> nodeInstructions, edgeInstructions;
	
	public GraphInstruction(ArrayList<SingleInstruction> nodeInstructions, ArrayList<SingleInstruction> edgeInstructions){
		this.nodeInstructions = nodeInstructions;
		this.edgeInstructions = edgeInstructions;
	}
	
	public ArrayList<SingleInstruction> getNodeInstructions(){
		return nodeInstructions;
	}
	
	public ArrayList<SingleInstruction> getEdgeInstructions(){
		return edgeInstructions;
	}
}
