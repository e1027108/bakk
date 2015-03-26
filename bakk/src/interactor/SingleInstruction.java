package interactor;

import javafx.scene.paint.Color;

public class SingleInstruction {

	private String name;
	private Color color;

	public SingleInstruction(String name, Color color){
		this.name = name;
		this.color = color;
	}
	
	public String getName(){
		return name;
	}
	
	public Color getColor(){
		return color;
	}
}
