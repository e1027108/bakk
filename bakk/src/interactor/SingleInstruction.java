package interactor;

import javafx.scene.paint.Color;

/**
 * A single instruction for coloring a single edge or node
 * @author Patrick Bellositz
 */
public class SingleInstruction {

	private String name; //the name of the edge or node
	private Color color; //the color to be applied to the object
	//TODO differentiate between edge and node?
	
	/**
	 * Creates a SingleInstruction object, containing name and color of a graph's element
	 * @param name the name of the element
	 * @param green the future color of the element in the graph
	 */
	public SingleInstruction(String name, Color green){
		this.name = name;
		this.color = green;
	}
	
	/**
	 * @return the name of the element
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * @return the color to be applied to the element
	 */
	public Color getColor(){
		return color;
	}
}
