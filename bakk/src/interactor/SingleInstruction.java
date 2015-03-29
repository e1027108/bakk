package interactor;

import javafx.scene.paint.Color;

public class SingleInstruction {

	private String name;
	private Color color;

	public SingleInstruction(String name, Color green){
		this.name = name;
		this.color = green;
	}
	
	public String getName(){
		return name;
	}
	
	public Color getColor(){
		return color;
	}
}
