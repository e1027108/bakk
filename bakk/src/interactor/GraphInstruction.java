package interactor;

import java.util.ArrayList;

/**
 * The GraphInstruction holds two sets of names (edges and nodes) and
 * a color for every name in which that object should be colored
 * @author Patrick Bellositz
 */
public class GraphInstruction {

	private ArrayList<SingleInstruction> nodeInstructions, edgeInstructions; //sets of names of edges and nodes with corresponding colors
	
	/**
	 * Creates a GraphInstruction, given two sets of SingleInstructions (containing names and colors)
	 * @param nodeInstructions a set of node names, each with a color
	 * @param edgeInstructions a set of edge names, each with a color
	 */
	public GraphInstruction(ArrayList<SingleInstruction> nodeInstructions, ArrayList<SingleInstruction> edgeInstructions){
		this.nodeInstructions = nodeInstructions;
		this.edgeInstructions = edgeInstructions;
	}
	
	/**
	 * @return the set of node names/colors
	 */
	public ArrayList<SingleInstruction> getNodeInstructions(){
		return nodeInstructions;
	}
	
	/**
	 * @return the set of edge names/colors
	 */
	public ArrayList<SingleInstruction> getEdgeInstructions(){
		return edgeInstructions;
	}
	
	/**
	 * sets the set of node names
	 * @param instructions the new set
	 */
	public void setNodeInstructions(ArrayList<SingleInstruction> instructions){
		this.nodeInstructions = instructions;
	}
	
	/**
	 * sets the set of edge names
	 * @param instructions the new set
	 */
	public void setEdgeInstructions(ArrayList<SingleInstruction> instructions){
		this.edgeInstructions = instructions;
	}
}
