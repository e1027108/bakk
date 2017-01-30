package interactor;

import javafx.scene.paint.Color;

/**
 * A single instruction for coloring a single edge or node
 * @author Patrick Bellositz
 */
public class SingleInstruction {

	private String name; //the name of the edge or node
	private Color color; //the color to be applied to the object
	public enum Type {EDGE, NODE, INVALID};
	private Type type;
	
	/**
	 * Creates a SingleInstruction object, containing name and color of a graph's element
	 * @param name the name of the element
	 * @param color the future color of the element in the graph
	 */
	public SingleInstruction(String name, Color color){
		this.name = name;
		this.color = color;
		
		switch(this.name.length()){
			case 1:
				type = Type.NODE;
				break;
			case 2:
				type = Type.EDGE;
				break;
			default:
				type = Type.INVALID;
				break;
		}
	}
	
	public SingleInstruction(char name, Color color){
		this.name = String.valueOf(name);
		this.color = color;
		type = Type.NODE;
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
	
	public Type getType(){
		return type;
	}
}
